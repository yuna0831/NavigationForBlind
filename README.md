# Navigation App for Blind

## Overview
This project is a **Navigation App** designed to assist visually impaired users in navigating their surroundings. The app leverages Android Studio and Java to provide voice-guided directions, enabling an accessible and intuitive user experience.

## Features
- **Voice Recognition**: Allows users to input locations using voice commands.
- **Text-to-Speech (TTS)**: Provides audio feedback for navigation instructions.
- **Location Search**: Efficiently finds and selects locations for navigation.
- **Permission Support**: Ensures necessary permissions are handled seamlessly.

## Technologies Used
- **Java**: Core programming language for the application's logic.
- **Android Studio**: Integrated development environment (IDE) used for building the app.
- **Android APIs**: Location services, TTS, and voice recognition APIs for core functionality.

## Files Included
1. `MainActivity.java`: Handles the primary user interface and navigation logic.
2. `MessageType.java`: Defines constants or enumerations used for handling messages.
3. `PermissionSupport.java`: Manages runtime permissions required by the app.
4. `Place.java`: Represents data structures for locations or places.
5. `SearchingLocation.java`: Implements logic for finding and setting locations.
6. `TTSLocationCallback.java`: Manages TTS callbacks during location-based interactions.
7. `TTSUtteranceProgressListener.java`: Monitors progress of TTS operations.
8. `VoiceRecognitionListener.java`: Handles voice input recognition and processing.

## How to Run
1. Open the project in **Android Studio**.
2. Ensure that your development environment is set up with the required SDKs.
3. Build and run the app on an Android device or emulator.
   - **Note**: The app requires permissions for microphone, location services, and audio playback.

## Future Enhancements
- Add more languages for voice commands and audio feedback.
- Integrate with third-party mapping services for more accurate navigation.
- Enhance UI for users with partial visual capabilities.

## License
This project is licensed under the MIT License. Feel free to use, modify, and distribute as needed.

## Acknowledgements
Special thanks to the open-source libraries and Android APIs used to make this project possible.
