# Riwal / Manlift Rental App

The app used by customers to rent and off-rent machines and other equipment. It has 2 brands: Riwal (for Europe and UK) and Manlift (for the Middle-East and Asia).

Features:
* Search for machines
* Show machine specifications and pictures

Special features for customers with Access4U account:
* See current and previous rentals, and their status
* See current rentals on a map
* Off-rent machines
* Easily re-rent previously rented machines

## Prerequisites
* Git
* Android Studio 

## Getting Started

1. Clone this project: `git clone https://github.com/RiwalHoldingGroup/customer-app-android`
2. Get the keystore.jks and keystore.properties files from Google Drive, place them in the project root 
3. Open the project in Android Studio
4. Run the app

## How to

### Use locally run Rental App Server
1. Make sure your phone/tablet is on the same Wifi network as your development machine
2. Configure the Rental App Server for the development environment and run it locally using Visual Studio Code (cf. Rental App Server’s readme). By default it is run on http://localhost:3000
3. Find your IP address in the network. On macOS, you can click the Wifi icon in the top bar → Open Network Preferences. You can find your IP address in the “Status” section. Let’s say the IP address is 10.10.221.102.
4. Open the build.gradle (app) file in Android Studio.
5. Locate the `FIREBASE_URL` build constant in the development productFlavor. It should have the value `"\"https://us-central1-customer-app-dev.cloudfunctions.net/api\""`
6. Replace it with `"\"**http**://**10.10.221.102**:**3000**/\""`. Note that it is run over **http** instead of https, it does **not** have the /api suffix and note the port.
7. Optional: Set breakpoints in Visual Studio Code

## Links
* Production signing certificate: [Certificates & Keystores for Android](https://drive.google.com/drive/folders/1Vf2nzS7Qe8cg-0H0w9uYY8PsmbiOp5hH)
* How to make a release: [Software releases](https://docs.google.com/document/d/16a3RFFh_eP3BQDp1yyHbMqbKVrsLb0raV9UZnTyS_8s/edit#bookmark=id.5f7tzqrbxiw3)
* Diagram: -- link to diagrams --
* [Riwal Rental app in Google Play Console](https://play.google.com/apps/publish/?account=6034295336762317829#AppDashboardPlace:p=com.riwal.rentalapp&appid=4973223639120942893)
* [Manlift Rental app in Google Play Console](https://play.google.com/apps/publish/?account=7957011153780638616#AppDashboardPlace:p=com.manlift.rentalapp&appid=4973292185629750681)

## Alternative names
* Customer app (this was the unofficial name before it was published, the repo and project still have this name)