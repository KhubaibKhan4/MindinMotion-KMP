# MindinMotion-KMP
Mind in Motion is a Compose Multiplatform Applications that consist of Android & iOS. Basically, This is an Educational Platform, Where user can perform quizzes and can do much more. 

## Mobile Screen Shots

| ![Screenshot 1](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/1.png) | ![Screenshot 2](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/2.png) | ![Screenshot 3](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/3.png) |
| --- | --- | --- |
| ![Screenshot 4](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/4.png) | ![Screenshot 5](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/5.png) | ![Screenshot 6](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/6.png) |
| ![Screenshot 7](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/7.png) | ![Screenshot 8](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/8.png) | ![Screenshot 9](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/9.png) |
| ![Screenshot 10](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/10.png) | ![Screenshot 11](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/11.png) | ![Screenshot 12](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/12.png) |
| ![Screenshot 13](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/13.png) | ![Screenshot 14](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/14.png) | ![Screenshot 15](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/15.png) |
| ![Screenshot 16](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/16.png) | ![Screenshot 17](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/17.png) | ![Screenshot 18](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/18.png) |
| ![Screenshot 19](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/19.png) | ![Screenshot 20](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/20.png) | ![Screenshot 21](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/21.png) |
| ![Screenshot 22](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/22.png) | ![Screenshot 23](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/23.png) | ![Screenshot 24](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/24.png) |
| --- | --- | --- |
| ![Screenshot 25](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/25.png) | ![Screenshot 26](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/26.png) | ![Screenshot 27](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/27.png) |
| ![Screenshot 28](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/28.png) | ![Screenshot 29](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/29.png) | ![Screenshot 45](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/30.png) |
| ![Screenshot 46](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/31.png) | ![Screenshot 47](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/32.png) | ![Screenshot 48](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/33.png) |
| ![Screenshot 49](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/34.png) | ![Screenshot 62](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/35.png) | ![Screenshot 63](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/36.png) |
| ![Screenshot 64](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/37.png) | ![Screenshot 65](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/38.png) | ![Screenshot 66](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/39.png) |
| ![Screenshot 67](https://github.com/KhubaibKhan4/MindinMotion-KMP/blob/master/assests/40.png) |

## Before running!
 - check your system with [KDoctor](https://github.com/Kotlin/kdoctor)
 - install JDK 17 or higher on your machine
 - add `local.properties` file to the project root and set a path to Android SDK there

### Android
To run the application on android device/emulator:  
 - open project in Android Studio and run imported android run configuration

To build the application bundle:
 - run `./gradlew :composeApp:assembleDebug`
 - find `.apk` file in `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
Run android simulator UI tests: `./gradlew :composeApp:pixel5Check`

### iOS
To run the application on iPhone device/simulator:
 - Open `iosApp/iosApp.xcproject` in Xcode and run standard configuration
 - Or use [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) for Android Studio
Run iOS simulator UI tests: `./gradlew :composeApp:iosSimulatorArm64Test`

