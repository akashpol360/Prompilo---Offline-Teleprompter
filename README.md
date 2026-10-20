# Prompilo

Prompilo is a free, offline Android teleprompter for writing scripts, reading them over a live camera preview, and recording local videos with microphone audio. Its warm cream, charcoal, and amber theme follows the Prompilo app icon.

## Open and build

Open this folder in Android Studio (Android Gradle Plugin 8.13.2, JDK 17, Android SDK 36). Sync Gradle, then run the `app` configuration on an Android device or emulator. A physical device is recommended for camera and microphone recording. The project uses Kotlin, Jetpack Compose, Room, and CameraX. Minimum Android version is 8.0 (API 26).

## Use

Tap **+** to create a script, import a text file, or create a folder. Scripts are saved in the app's local Room database and can be organized into color-coded folders; folder colors can be changed, and deleting a folder returns its scripts to the main list. Open the separate video-drafts icon on the home screen to browse recordings. The landscape dashboard scrolls vertically, and prompter panel size can be adjusted with the zoom controls while reading. Open a script and choose **Start teleprompter** to grant camera and microphone access, read over the preview, adjust speed and text size, move the script area, change text color and background opacity, flip the text, switch cameras, and record. Recordings are saved under Movies/Prompilo using Android MediaStore. After stopping, the recording opens in the in-app player, stays saved in Movies/Prompilo in the gallery, and also appears in Video drafts for playback and sharing. Drafts include a private recovery copy and can be saved back to Gallery if the gallery copy is removed. Removing a draft entry deletes its private recovery copy but leaves the gallery video intact.

The script is a reading overlay in the app display and is not part of the recorded camera video. The camera permission is used for camera preview and recording; the microphone permission is used only for recording audio. The More screen includes Pro plans, purchase restore, feedback, sharing, and rating. Free users earn one recording by completing a rewarded ad; Pro users can record without ads. Rewarded ads use Google's test unit in this build. The app uses Google Play Billing for Pro entitlements and Google Mobile Ads for rewarded ads; scripts and videos remain on-device unless you explicitly share them.

Play Console products must be created with IDs `pro_yearly` (subscription), `pro_monthly` (subscription), and `pro_lifetime` (one-time product) for purchases to appear. Product prices are localized from Google Play. Before publishing with live ads, replace Google's test rewarded unit and test AdMob app ID in `app/src/main/AndroidManifest.xml` and `app/src/main/java/com/prompilo/app/Monetization.kt` with the IDs from your AdMob account.

## Google Play release

The release build targets API 36 and uses version code 9 / version name 1.8. To sign a release bundle, place a `keystore.properties` file in the project root with `storeFile`, `storePassword`, `keyAlias`, and `keyPassword`; never include that file or the private keystore in a shared source archive. The generated upload key is separate from Google Play's app-signing key. Enroll this first app in Play App Signing and upload the signed `.aab`. Keep a secure backup of the upload `.jks` and its password. Google's test AdMob IDs are configured by request for the first upload; use them only for internal/closed testing and replace them with your production IDs before production release. Create and activate the three Play Billing products above in Play Console before testing purchases.

## Privacy

See [privacy-policy.html](app/src/main/assets/privacy-policy.html).
