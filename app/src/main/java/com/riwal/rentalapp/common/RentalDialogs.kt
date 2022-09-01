package com.riwal.rentalapp.common

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.riwal.rentalapp.R
import kotlinx.android.synthetic.main.dialog_comment.*

object RentalDialogs {

    fun commentsDialog(context: Context, callback: (comments: String) -> Unit) = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.dialog_comments_title))
            .setMessage(context.getString(R.string.dialog_comments_message))
            .setView(R.layout.dialog_comment)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(context.getString(R.string.confirm)) { dialog, _ ->
                dialog as AlertDialog
                val comments = dialog.commentsEditText.text.toString()
                callback(comments)
            }!!

    fun messageDialog(context: Context, message: String, title: String, actionPositive: String, cancelable: Boolean = true, callback: () -> Unit) = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(actionPositive) { dialog, _ ->
                dialog as AlertDialog
                callback()
            }!!

    fun actionDialog(context: Context, message: String, title: String, actionPositive: String, cancelable: Boolean = true, positiveCallback: () -> Unit ,negativeCallback: () -> Unit ) = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog as AlertDialog
                negativeCallback() }
            .setPositiveButton(actionPositive) { dialog, _ ->
                dialog as AlertDialog
                positiveCallback()
            }!!




}