package com.riwal.rentalapp.model.api

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.GlideRequests
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.set
import com.riwal.rentalapp.common.extensions.core.doInBackground
import com.riwal.rentalapp.common.extensions.datetime.DISTANT_PAST_IN_SQL
import com.riwal.rentalapp.common.extensions.datetime.isoString
import com.riwal.rentalapp.common.extensions.firebase.rxIdToken
import com.riwal.rentalapp.common.extensions.firebase.rxSignInAnonymously
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.json.fromJson
import com.riwal.rentalapp.common.extensions.rentalapp.isUnauthorized
import com.riwal.rentalapp.common.extensions.retrofit.UnitConverterFactory
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.requestaccountform.Account
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.rx2.await
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.NANOSECONDS
import java.util.concurrent.TimeUnit.SECONDS


class BackendClient(
        private val preferences: SharedPreferences,
        private val firebaseAuth: FirebaseAuth,
        private val glideRequests: GlideRequests
) {


    /*------------------------------------- Member variables -------------------------------------*/


    private val rentalAppApiClient: RentalAppApi
    private val access4UApiClient: Access4UApi
    private val fakeApiClient: FakeAppApi

    // TODO: Use OkHttp's Authenticator interface to handle authentication failures if possible.
    private var rentalAppApiAuthorization = RentalAppApiAuthorizationInterceptor()
    private var access4UAuthorization = BearerAuthorizationInterceptor()
    private val appDataInterceptor = AppInfoInterceptor()
    private val loggingInterceptor = HttpLoggingInterceptor()

    private var firebaseIdToken: JsonWebToken? = null
        set(value) {
            field = value
            setAccessTokensInAuthorizationInterceptors()
        }

    private var access4UAccessToken: JsonWebToken? = null
        set(value) {
            field = value
            setAccessTokensInAuthorizationInterceptors()
        }

    private var access4URefreshToken: String? = preferences["access4URefreshToken"]
        set(value) {
            field = value
            preferences["access4URefreshToken"] = access4URefreshToken
        }

    val isLoggedIn
        get() = access4URefreshToken != null


    /*--------------------------------------- Initialize -----------------------------------------*/


    init {

        // Initialize the API client
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // Disabled connection Pool to solve the SocketTimeoutException
        // It's just a work-around mentioned here: https://github.com/square/okhttp/issues/3146
        val okHttpClientForAccess4U = OkHttpClient.Builder()
                .addInterceptor(access4UAuthorization)
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .readTimeout(30, SECONDS)
                .connectionPool(ConnectionPool(0, 1, NANOSECONDS))
                .connectTimeout(60, SECONDS)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .build()

        val okHttpClientForFakeAPI = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, SECONDS)
                .connectionPool(ConnectionPool(0, 1, NANOSECONDS))
                .build()

        val retrofitForAccess4U = Retrofit.Builder()
                .baseUrl(BuildConfig.ACCESS4U_URL + "/")
                .addConverterFactory(UnitConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClientForAccess4U)
                .build()

        access4UApiClient = retrofitForAccess4U.create(Access4UApi::class.java)

        val okHttpClientForFirebase = OkHttpClient.Builder()
                .addInterceptor(rentalAppApiAuthorization)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(appDataInterceptor)
                .readTimeout(30, SECONDS)
                .connectTimeout(60, SECONDS)
                .build()

        val retrofitForFirebase = Retrofit.Builder()
                .baseUrl(BuildConfig.RENTAL_APP_SERVER_URL + "/")
                .addConverterFactory(UnitConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClientForFirebase)
                .build()

        rentalAppApiClient = retrofitForFirebase.create(RentalAppApi::class.java)

        val retrofitForfakeAPI = Retrofit.Builder()
                .baseUrl("http://demo0102191.mockable.io/")
                .addConverterFactory(UnitConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClientForFakeAPI)
                .build()

        fakeApiClient = retrofitForfakeAPI.create(FakeAppApi::class.java)

        setAccessTokensInAuthorizationInterceptors()

        doInBackground {
            try {
                refreshTokensIfNeeded()
            } catch (error: Exception) {
                // Ignore
            }
        }
    }


    /*-------------------------------------- Access4U API ----------------------------------------*/


    suspend fun login(email: String, password: String): User {
        val loginResponse = access4UApiClient.login(LoginBody(email, password)).await()
        access4UAccessToken = loginResponse.accessToken
        access4URefreshToken = loginResponse.refreshToken
        return loginResponse.run {
            // The response.email can contain multiple, comma-separated email addresses, so we use the entered email address
            User(name = "$firstName $lastName", email = email, phoneNumber = phoneNumber)
        }
    }

    fun logout() {
        access4UAccessToken = null
        access4URefreshToken = null
    }

    suspend fun resetPassword(email: String): Boolean {
        return access4UApiClient
                .resetPassword(emailAddress = email)
                .await()
    }

    suspend fun getCustomers() = withAccess4UAccessToken {
        access4UApiClient
                .getCustomers()
                .await()
                .map { it.toCustomer() }
    }

    suspend fun getAccountManager(customer: Customer) = withAccess4UAccessToken {
        access4UApiClient
                .getAccountManager(customerId = customer.id, companyId = customer.companyId, databaseName = customer.databaseName)
                .await()
                .toAccountManager()
    }

    suspend fun getRentals(customer: Customer, dateRange: ClosedRange<LocalDate>) = withAccess4UAccessToken {

        val startDate = maxOf(dateRange.start, DISTANT_PAST_IN_SQL.toLocalDate())
        val endDate = dateRange.endInclusive

        access4UApiClient
                .getRentals(startDate = startDate.isoString, endDate = endDate.isoString, customerId = customer.id, companyId = customer.companyId, databaseName = customer.databaseName)
                .await()
                .map { it.toRental() }
    }

    suspend fun getAccessoriesOnRent(customer: Customer, dateRange: ClosedRange<LocalDate>) = withAccess4UAccessToken {

        val startDate = maxOf(dateRange.start, DISTANT_PAST_IN_SQL.toLocalDate())
        val endDate = dateRange.endInclusive

        access4UApiClient
                .getAccesoriesOnRented(startDate = startDate.isoString, endDate = endDate.isoString, databaseName = customer.databaseName, customerId = customer.id, companyId = customer.companyId)
                .await()
                .map { it.toAccessoriesOnRent() }
    }

    suspend fun getInvoices(customer: Customer, dateRange: ClosedRange<LocalDate>) = withAccess4UAccessToken {
        val startDate = maxOf(dateRange.start, DISTANT_PAST_IN_SQL.toLocalDate())
        val endDate = dateRange.endInclusive

        access4UApiClient
                .getInvoices(customerId = customer.id, companyId = customer.companyId, startDate = startDate.isoString, endDate = endDate.isoString)
                .await()
//                    .map { it.toInvoice() }
    }

    suspend fun downloadInvoice(customerId: String, invoiceNumber: String) = withAccess4UAccessToken {

        access4UApiClient
                .downloadInvoice(customerId = customerId, invoiceId = invoiceNumber)
                .await()
    }

    suspend fun getQuotations(customer: Customer, dateRange: ClosedRange<LocalDate>) = withAccess4UAccessToken {

        val startDate = maxOf(dateRange.start, DISTANT_PAST_IN_SQL.toLocalDate())
        val endDate = dateRange.endInclusive

        access4UApiClient
                .getQuotations(customerId = customer.id, startDate = startDate.toString(), endDate = endDate.toString())
                .await()

    }


    suspend fun downloadQuotation(customerId: String, quotationNumber: String) = withAccess4UAccessToken {

        access4UApiClient
                .downloadQuotation(customerId = customerId, invoiceId = quotationNumber)
                .await()
    }


    suspend fun getRentalDetail(customer: Customer, inventTransId: String) = withAccess4UAccessToken {

        access4UApiClient
                .getRentalDetail(customerId = customer.id, inventTransId = inventTransId)
                .await()
                .toRentalDetail()
    }


    suspend fun loadImageFromAccess4U(url: String): Drawable {

        refreshAccess4UAccessTokenIfNeeded()

        val authHeader = LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer ${access4UAccessToken!!.string}")
                .build()

        return glideRequests
                .loadImage(GlideUrl(url, authHeader))
                .await()
    }

    suspend fun getAccessories(rentalType: String, culture: String, companyId: Int) = withAccess4UAccessToken {
        access4UApiClient
                .getAccessories(rentalType = rentalType, culture = culture, companyId = companyId)
                .await()
                .map { it.toAccessory() }
    }

    suspend fun getMyProject(customerId: String) = withAccess4UAccessToken {
        access4UApiClient.getMyProjects(customerId)
                .await()
                .toMyProject()
    }

    suspend fun getMachineTransferNotification(customerId: String) = withAccess4UAccessToken {
        access4UApiClient.getTransferMachineNotification(customerId)
                .await()
                .map { it.toTransferNotification() }
    }


    suspend fun getTrainings(CountryCode: Country) = withAccess4UAccessToken {
        access4UApiClient.getTrainings(CountryCode)
                .await()
    }


    /*----------------------------------- Rental App Server API ----------------------------------*/


    suspend fun machines(activeCountry: Country) = withFirebaseIdToken {
        try {
            rentalAppApiClient
                    .getMachines(activeCountry)
                    .await()
                    .mapNotNull { it.toMachine() }
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun appInfo() = withFirebaseIdToken {
        try {
            rentalAppApiClient
                    .getAppInfo()
                    .await()
                    .toAppInfo()

        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendAccountRequest(account: Account, country: Country) = withFirebaseIdToken {

        val accountRequestBody = AccountRequestBody(
                name = account.name!!,
                company = account.company!!,
                email = account.email!!,
                phoneNumber = account.phoneNumber!!,
                countryCode = country,
                vatNumber = account.vatNumber
        )

        try {
            rentalAppApiClient.sendAccountRequest(accountRequestBody).await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendOrderRequest(order: Order, customer: Customer?, country: Country) {

        val machines = order.machineOrders.map { it.toMachineOrderBody() }
        val body = RentalOrderBody(customer?.toCustomerBody(), order.contact!!, order.project!!, machines, order.purchaseOrder, order.notes, country)

        suspend fun performRequest() {
            rentalAppApiClient
                    .sendOrderRequest(customerId =  customer?.id , companyId = customer?.companyId, body = body)
                    .await()
        }

        try {
            if (isLoggedIn) {
                withRentalAppServerTokens { performRequest() }
            } else {
                withFirebaseIdToken { performRequest() }
            }
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendOffRentRequest(rentals: List<Rental>, offRentDateTime: LocalDateTime, notes: String, customer: Customer, country: Country, pickupLocation: String?) = withRentalAppServerTokens {

        val body = OffRentRequestBody(
                customer = customer,
                rentals = rentals,
                offRentDateTime = offRentDateTime.isoString,
                notes = notes,
                countryCode = country,
                pickupLocation = pickupLocation
        )

        try {
            rentalAppApiClient
                    .sendOffRentRequest(customerId = customer.id , companyId = customer.companyId, body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendOffRentAccessoriesRequest(accessories: List<AccessoriesOnRent>, offRentDateTime: LocalDateTime, notes: String, customer: Customer, country: Country, pickupLocation: String?) = withRentalAppServerTokens {

        val body = OffRentAccessoriesRequestBody(
                customer = customer,
                accessories = accessories,
                offRentDateTime = offRentDateTime.isoString,
                notes = notes,
                countryCode = country,
                pickupLocation = pickupLocation
        )

        try {
            rentalAppApiClient
                    .sendOffRentAccessoriesRequest(customerId = customer.id, companyId = customer.companyId, body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendCancelRentRequest(rentals: List<Rental>, onRentDateTime: LocalDateTime, notes: String, customer: Customer, country: Country) = withRentalAppServerTokens {

        val body = CancelRentRequestBody(
                customer = customer,
                rentals = rentals,
                onRentDateTime = onRentDateTime.isoString,
                notes = notes,
                countryCode = country
        )

        try {
            rentalAppApiClient
                    .sendCancelRentRequest(customerId = customer.id, companyId = customer.companyId, body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendCancelAccessoriesRequest(accessories: List<AccessoriesOnRent>, onRentDateTime: LocalDateTime, notes: String, customer: Customer, country: Country) = withRentalAppServerTokens {

        val body = CancelAccessoriesRequestBody(
                customer = customer,
                accessories = accessories,
                onRentDateTime = onRentDateTime.isoString,
                notes = notes,
                countryCode = country
        )

        try {
            rentalAppApiClient
                    .sendcancelAccessoriesRequest(customerId = customer.id, companyId = customer.companyId, body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendChangeRequest(originalRental: Rental, changedRental: Rental, comments: String, customer: Customer, country: Country) = withRentalAppServerTokens {

        val body = ChangeRequestBody(
                customer = customer.toCustomerBody(),
                orderNumber = originalRental.orderNumber,
                orderContact = originalRental.contact,
                rentalType = originalRental.rentalType,
                fleetNumber = originalRental.fleetNumber,
                changes = originalRental.differences(changedRental),
                comments = comments,
                countryCode = country,
                purchaseOrder = originalRental.purchaseOrder,
                project = originalRental.project
        )

        try {
            rentalAppApiClient
                    .sendChangeRequest(customerId = customer.id, companyId = customer.companyId, body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendChangeRequest(originalAccessories: AccessoriesOnRent, changedAccessories: AccessoriesOnRent, comments: String, customer: Customer, country: Country) = withRentalAppServerTokens {

        val body = AccessoryChangeRequestBody(
                customer = customer.toCustomerBody(),
                orderNumber = originalAccessories.orderNumber,
                orderContact = originalAccessories.contact,
                rentalType = originalAccessories.rentalType,
                fleetNumber = originalAccessories.fleetNumber,
                changes = originalAccessories.differences(changedAccessories),
                comments = comments,
                countryCode = country,
                purchaseOrder = originalAccessories.purchaseOrder,
                project = originalAccessories.project
        )

        try {
            rentalAppApiClient
                    .sendChangeRequest(customerId = customer.id, companyId = customer.companyId, body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendBreakdownRequest(rental: Rental, comments: String, customer: Customer, country: Country, contactPhoneNumber: String?) = withRentalAppServerTokens {

        val body = BreakdownRequestBody(
                customer = customer,
                rental = rental,
                breakdownMessage = comments,
                countryCode = country,
                companyName = rental.contact.company!!,
                contactPhoneNumber = contactPhoneNumber
        )

        try {
            rentalAppApiClient
                    .sendBreakdownRequest(customerId = customer.id, companyId = customer.companyId, body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendFeedback(rating: String, message: String?, email: String?, country: Country) = withFirebaseIdToken {

        val body = FeedbackBody(rating, message, email, country)
        try {
            rentalAppApiClient
                    .sendFeedback(body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun registerForNotifications(email: String, token: String) = withFirebaseIdToken {

        val body = RegisterNotificationTokenRequestBody(email, token)

        try {
            rentalAppApiClient
                    .upsertNotificationTarget(body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendTransferMachineRequest(companyId: Int, body: MachineTransferRequestBody, customerId: String) = withRentalAppServerTokens {
        try {
            rentalAppApiClient
                    .sendTransferMachineRequest(customerId = customerId, companyId = companyId.toString(), body = body)
                    .await()
        } catch (error: HttpException) {
            Log.e("Transfer Machine::", error.message())
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendRejectTransferRequest(body: RejectTransferMachineRequestBody) = withRentalAppServerTokens {

        try {
            rentalAppApiClient
                    .sendRejectTransferRequest(body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendAcceptMachineTransfer(companyId: String, acceptTransferRequestBody: AcceptTransferMachineRequestBody, selectedImageFiles: Array<String>?) = withRentalAppServerTokens {

        val photos: MutableList<String> = ArrayList()
        for (element in selectedImageFiles!!) {
            val imageFile = File(element)
            var fileInputStream: FileInputStream?
            fileInputStream = FileInputStream(imageFile)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT)

            photos.add(encodedImage)
        }
        acceptTransferRequestBody.photos.addAll(photos)

        try {
            rentalAppApiClient
                    .sendAcceptMachineTransfer(companyId = companyId, customerId = acceptTransferRequestBody.fromCustomer.id, acceptTransferRequestBody = acceptTransferRequestBody)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    suspend fun sendTrainingRequest(body: TrainingFireBaseRequestBody) = withRentalAppServerTokens {
        try {
            rentalAppApiClient
                    .sendTrainingRequest(body = body)
                    .await()
        } catch (error: HttpException) {
            throw error.toRentalAppException() ?: error
        }
    }

    /*-------------------------------------- Private methods -------------------------------------*/


    private suspend fun refreshRentalAppServerTokensIfNeeded() {
        refreshTokensIfNeeded()
    }

    private suspend fun refreshTokensIfNeeded() {
        refreshFirebaseIdTokenIfNeeded()
        refreshAccess4UAccessTokenIfNeeded()
    }

    private suspend fun refreshFirebaseIdTokenIfNeeded() {
        if (firebaseIdToken == null || firebaseIdToken!!.isExpired) {
            refreshFirebaseIdToken()
        }
    }

    private suspend fun refreshFirebaseIdToken() {
        val firebaseUser = firebaseAuth.rxSignInAnonymously().await()
        val idToken = firebaseUser.rxIdToken(forceRefresh = true).await()
        firebaseIdToken = JsonWebToken(string = idToken)
    }

    private suspend fun refreshAccess4UAccessTokenIfNeeded() {

        if (access4URefreshToken == null) {
            return
        }

        if (access4UAccessToken == null || access4UAccessToken!!.isExpired) {
            refreshAccess4UAccessToken()
        }
    }

    private suspend fun refreshAccess4UAccessToken() {
        access4UAccessToken = access4UApiClient
                .refreshAccessToken(RefreshTokenBody(access4URefreshToken))
                .await()
                .token
    }

    private suspend fun <T> withAccess4UAccessToken(block: suspend () -> T): T {
        return block.tryWithTokenRefreshHandlers(
                this::refreshAccess4UAccessTokenIfNeeded,
                this::refreshAccess4UAccessToken
        )
    }

    private suspend fun <T> withFirebaseIdToken(block: suspend () -> T): T {
        return block.tryWithTokenRefreshHandlers(
                this::refreshFirebaseIdTokenIfNeeded,
                this::refreshFirebaseIdToken
        )
    }

    private suspend fun <T> withRentalAppServerTokens(block: suspend () -> T): T {
        return block.tryWithTokenRefreshHandlers(
                this::refreshRentalAppServerTokensIfNeeded,
                this::refreshAccess4UAccessToken,
                this::refreshFirebaseIdToken
        )
    }

    private suspend fun <T> (suspend () -> T).tryWithTokenRefreshHandlers(vararg tokenRefreshHandlers: suspend () -> Unit): T {
        val iterator = tokenRefreshHandlers.iterator()

        while (iterator.hasNext()) {
            val refreshHandler = iterator.next()
            refreshHandler()
            try {
                return this()
            } catch (error: HttpException) {
                if (!error.isUnauthorized() || !iterator.hasNext()) {
                    throw error
                }
            }
        }

        throw IllegalStateException()
    }

    private fun setAccessTokensInAuthorizationInterceptors() {
        access4UAuthorization.token = access4UAccessToken?.string
        rentalAppApiAuthorization.access4UToken = access4UAccessToken?.string
        rentalAppApiAuthorization.firebaseIdToken = firebaseIdToken?.string
    }


    /*------------------------------------ Private extensions ------------------------------------*/


    private fun HttpException.toRentalAppException(): RentalAppServerException? {
        val error = fromJson<Map<String, Any>>(response().errorBody()?.string()) ?: return null
        val code = response().code()

        return when (code) {
            HTTP_BAD_REQUEST -> ClientException(error)
            HTTP_NOT_FOUND -> NotFoundException(error)
            HTTP_INTERNAL_ERROR -> InternalServerException(error)
            else -> null
        }

    }


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    interface Access4UApi {

        @POST("Login")
        fun login(@Body loginBody: LoginBody): Deferred<LoginResponse>

        @GET("forgotPassword")
        fun resetPassword(@Query("emailAddress") emailAddress: String): Deferred<Boolean>

        @POST("RefreshToken")
        fun refreshAccessToken(@Body refreshTokenBody: RefreshTokenBody): Deferred<RefreshTokenResponse>

        @GET("Customers")
        fun getCustomers(): Deferred<List<CustomerResponse>>

        @GET("AccountManagerDetails")
        fun getAccountManager(@Query("customerId") customerId: String, @Query("companyId") companyId: String, @Query("databaseName") databaseName: String): Deferred<AccountManagerResponse>

        @GET("Orders")
        fun getRentals(@Query("startDate") startDate: String, @Query("endDate") endDate: String, @Query("customerId") customerId: String, @Query("companyId") companyId: String, @Query("databaseName") databaseName: String): Deferred<List<RentalResponse>>

        @GET("orderdetail")
        fun getRentalDetail(@Query("customerId") customerId: String, @Query("inventTransId") inventTransId: String): Deferred<RentalDetailResponse>

        @GET("GetAccessories")
        fun getAccessories(@Query("rentalType") rentalType: String, @Query("culture") culture: String, @Query("company") companyId: Int): Deferred<List<AccessoriesResponse>>

        @GET("ExtraOnRent")
        fun getAccesoriesOnRented(@Query("startDate")
                                  startDate: String,
                                  @Query("endDate")
                                  endDate: String,
                                  @Query("databaseName")
                                  databaseName: String,
                                  @Query("customerId")
                                  customerId: String,
                                  @Query("companyId")
                                  companyId: String): Deferred<List<AccessoriesOnRentResponse>>

        @GET("GetProjectsInfo")
        fun getMyProjects(@Query("customerId")
                          customerId: String): Deferred<MyProjectResponse>

        @GET("GetTransfers")
        fun getTransferMachineNotification(@Query("customerId")
                                           customerId: String): Deferred<List<MachineTransferNotificationResponse>>

        @GET("GetTrainings")
        fun getTrainings(@Query("CountryCode")
                         countryCode: Country): Deferred<TrainingResponse>

        @GET("GetMyInvoices")
        fun getInvoices(@Query("CustomerId")
                        customerId: String,
                        @Query("CompanyId")
                        companyId: String,
                        @Query("StartDate")
                        startDate: String,
                        @Query("EndDate")
                        endDate: String): Deferred<InvoiceResponse>

        @GET("DownloadInvoice")
        fun downloadInvoice(@Query("customerId")
                            customerId: String,
                            @Query("invid")
                            invoiceId: String): Deferred<ResponseBody>

        @GET("GetQuotations")
        fun getQuotations(@Query("CustomerId")
                          customerId: String,
                          @Query("StartDate")
                          startDate: String,
                          @Query("EndDate")
                          endDate: String): Deferred<MyQuotationsResponse>

        @GET("DownloadQuote")
        fun downloadQuotation(@Query("customerId")
                            customerId: String,
                            @Query("qid")
                            invoiceId: String): Deferred<ResponseBody>

    }

    interface RentalAppApi {

        @GET("v3/appinfo/android")
        fun getAppInfo(): Deferred<AppInfoResponse>

        @GET("v3/machines")
        fun getMachines(@Query("countryCode") countryCode: Country): Deferred<List<MachineResponse>>

        @POST("v3/accountRequest")
        fun sendAccountRequest(@Body body: AccountRequestBody): Deferred<Unit>

        @POST("v3/orderRequests")
        fun sendOrderRequest(@Header("Customer-Id") customerId: String?, @Header("Company-Id") companyId: String?, @Body body: RentalOrderBody): Deferred<Unit>

        @POST("v3/offRent")
        fun sendOffRentRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: OffRentRequestBody): Deferred<Unit>

        @POST("v3/cancelRent")
        fun sendCancelRentRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: CancelRentRequestBody): Deferred<Unit>

        @POST("v3/changeRequests")
        fun sendChangeRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: ChangeRequestBody): Deferred<Unit>

        @POST("v3/feedback")
        fun sendFeedback(@Body body: FeedbackBody): Deferred<Unit>

        @POST("v3/upsertNotificationTarget")
        fun upsertNotificationTarget(@Body body: RegisterNotificationTokenRequestBody): Deferred<Unit>

        @POST("v3/breakdownRequest")
        fun sendBreakdownRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: BreakdownRequestBody): Deferred<Unit>

        @POST("v3/accessoriesCancelRent")
        fun sendcancelAccessoriesRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: CancelAccessoriesRequestBody): Deferred<Unit>

        @POST("v3/accessoriesOffRent")
        fun sendOffRentAccessoriesRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: OffRentAccessoriesRequestBody): Deferred<Unit>

        @POST("v3/accessoriesChangeRequests")
        fun sendChangeRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: AccessoryChangeRequestBody): Deferred<Unit>

        @POST("v3/transferMachineRequest")
        fun sendTransferMachineRequest(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body body: MachineTransferRequestBody): Deferred<Unit>

        @POST("v3/declineTransferMachine")
        fun sendRejectTransferRequest(@Body body: RejectTransferMachineRequestBody): Deferred<Unit>

        @POST("v3/acceptTransferMachine")
        fun sendAcceptMachineTransfer(@Header("Customer-Id") customerId: String, @Header("Company-Id") companyId: String, @Body acceptTransferRequestBody: AcceptTransferMachineRequestBody): Deferred<Unit>

        @POST("v3/bookTraining")
        fun sendTrainingRequest(@Body body: TrainingFireBaseRequestBody): Deferred<Unit>

    }

    interface FakeAppApi {

        @GET("myproject")
        fun getMyProject(): Deferred<MyProjectResponse>

        @GET("transferNotifications")
        fun getTransferMachineNotification(@Query("customerId") customerId: String): Deferred<List<MachineTransferNotificationResponse>>

        @GET("GetTrainings")
        fun getTrainings(@Query("company")
                         companyId: Int): Deferred<TrainingResponse>

        @GET("myInvoices")
        fun getInvoices(@Query("customerId") customerId: String): Deferred<InvoiceResponse>

    }

}
