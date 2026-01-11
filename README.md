## **🍄 Mushroom Hunt Game**

Welcome to Mushroom Hunt, an interactive Android game where players collect mushrooms while dodging obstacles!

The game features two play styles (Buttons and Sensors), a high-score leaderboard with Google Maps integration, and a clean, scalable architecture.



### 🚀 Getting Started

##### Prerequisites:

Android Studio Ladybug or newer.

Android SDK 34 or higher.

A physical Android device or Emulator with Google Play Services.

### 

### 🔑 Google Maps API Configuration

This project uses Google Maps to display player locations on the leaderboard.

To keep the API key secure, it is stored locally and not committed to version control.

Open your **local.properties** file (located in the project's root folder).

Add your API key at the bottom of the file like this: **GOOGLE\_MAPS\_API\_KEY=YOUR\_API\_KEY\_HERE**

The project is configured to automatically inject this key into the AndroidManifest.xml during the build process.



### 🏗 Architecture \& Code Structure

A core highlight of this project is the Strict Separation of Concerns.

The app follows a simplified MVC/Observer pattern to ensure that the code is readable, testable, and maintainable.



### 🧠 Game Logic vs. UI Separation

GameManager (Logic): This class is purely responsible for the game engine.

It manages scores, lives, collision detection, and object movement.

Crucially, it has no reference to Android Views (like ImageView or Binding).

It communicates only via abstract coordinates and IDs.

MainActivity (UI): Acts as the "Observer." It implements the GameCallback interface.

When the logic determines an object has moved, it notifies the Activity, which then handles the actual visual rendering, animations, and sound effects.



### 🎮 Features

##### Dual Control Modes:

Buttons Mode: Classic left/right arrows for precise movement.

Sensor Mode: Use your device's Accelerometer to tilt the character and control falling speed.

##### Smart Leaderboard:

Top 10 scores are saved locally using SharedPreferences. Integration with Google Maps shows exactly where you achieved your record.

##### Responsive UI:

Collapsible Settings menu with smooth horizontal expansion animations. Real-time life tracking and score calculation.



### 🛠 Tech Stack

Language: Kotlin

UI Engine: View Binding for type-safe view access.

Concurrency: Kotlin Coroutines for smooth game loops and timers.

Navigation: Fragment-based architecture for the Records screen.

Maps: Google Maps SDK for Android.

