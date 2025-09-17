# NavigationForBlind

Voice-based navigation app for visually impaired users (Android, Korea-specific).

---

## Overview
NavigationForBlind is a command-based Android application designed to assist visually impaired users with simple navigation in daily life. The app enables users to interact via voice commands and provides turn-by-turn audio guidance in Korean.

---

## Features

- Limited category search: Users can search for five predefined location types:
  - Convenience store (편의점)
  - Pharmacy (약국)
  - Hospital (병원)
  - Bus stop (정류장)
  - Cafe (카페)

- Voice-based interaction:
  - The app reads aloud nearby matching locations.
  - Users select their destination by saying "1, 2, 3 ..."
  - Commands like "안내 시작" (start navigation) and "안내 중지" (stop navigation) are supported.

- Turn-by-turn guidance:
  - Provides concise instructions such as:
    - "전방 50m 후 우회전" (Turn right in 50m)
    - "100m 앞 목적지 도착" (Arrive in 100m)

- Speech technology:
  - Uses Text-to-Speech (TTS) for all navigation guidance.

---

## Tech Stack

- Language: Java
- APIs:
  - Kakao Map API for location search
  - SKT Tmap API for route guidance
- Android APIs:
  - Text-to-Speech (TTS)
  - SpeechRecognizer (voice commands)
- Platform: Android (Korean only)

---

## Usage

Note: This app requires real Android devices with microphone support. Running on emulators or laptops without proper audio input will not work.

1. Launch the app.
2. Say a command (e.g., "편의점") to search nearby convenience stores.
3. The app will list a few options aloud.
4. Say "1번" / "2번" / "3번" to select.
5. Say "안내 시작" to begin navigation or "안내 중지" to stop.

---

## Limitations

- Currently supports only Korean language.
- Search is limited to five predefined categories.
- Works only with physical Android devices, not desktop/laptop environments.

---

## License
This project is licensed under the MIT License.
 libraries and Android APIs used to make this project possible.
