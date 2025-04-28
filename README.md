# Party of the People - Android App

A patriotic mobile application built with Jetpack Compose.

## Getting Started

These instructions will help you get the app running on your local machine.

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Running the App

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select the "app" configuration
4. Choose a device or emulator
5. Click the "Run" button (green triangle)

### Troubleshooting Build Issues

If you encounter build errors:

1. Try running "Build > Clean Project" from the menu
2. Then select "Build > Rebuild Project"
3. If errors persist, try "File > Invalidate Caches / Restart"

## App Architecture

This app follows a simplified MVVM architecture:

- **UI Layer**: Jetpack Compose screens in the `ui/screens` package
- **ViewModel Layer**: ViewModels in the `viewmodels` package
- **Navigation**: Navigation components in the `navigation` package
- **Theme**: UI theme definitions in the `ui/theme` package

## Current Screens

- **Home**: Main screen with navigation options
- **Shop**: Purchase items with Patriot Points
- **Battleground**: View and interact with battleground states

## License

This project is licensed under the MIT License - see the LICENSE file for details. 