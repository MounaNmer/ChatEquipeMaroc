# ChatEquipeMaroc

ChatEquipeMaroc is an Android chat app themed around the Moroccan national football team. Users sign up with an email and password, find other users, and send each other one-to-one text messages in real time. The backend is Firebase.

## Features

- **Sign up / sign in**: email and password through Firebase Authentication (`RegisterActivity`, `AuthentificationActivity`). The splash screen sends users who are already signed in straight to the home screen.
- **Conversations list**: the home screen (`HomeActivity`) shows each contact you've messaged, with the last message.
- **User search**: find other registered users by name and start a chat with them (`UsersSearchActivity`).
- **Real-time chat**: messages are stored in Firestore, and the chat screen listens for new ones as they arrive (`ChatActivity`).
- **Profile settings**: change your display name and upload a profile picture to Firebase Storage (`SettingsActivity`).
- **Logout**: from the home screen menu.

## Tech stack

- Kotlin, Android Views with XML layouts, RecyclerView, and Material Components
- Firebase Authentication, Cloud Firestore, and Firebase Storage
- Glide for loading images
- Gradle (Kotlin DSL) with Android Gradle Plugin 8.1.1 and Kotlin 1.9.0
- `minSdk 26`, `targetSdk 33`, `compileSdk 34`

## Firestore data layout

```
users/{uid}                     { fullname, email, image }
users/{uid}/friends/{friendUid} { name, lastMsg, image, timestamp }
messages/{autoId}               { sender, receiver, text, timestamp }
```

## Running the project

### Requirements

- Android Studio (Giraffe or newer) with JDK 17
- An Android device or emulator running Android 8.0 (API 26) or higher

### Steps

1. Clone the repository and open the root folder in Android Studio.
2. Let Gradle sync. The wrapper downloads Gradle 8.0 automatically.
3. Choose a device or emulator and click **Run**, or build from the command line:

   ```bash
   ./gradlew assembleDebug      # build the debug APK
   ./gradlew installDebug       # install it on a connected device
   ```

### Using your own Firebase project (optional)

The repository already includes `app/google-services.json`, which points to the `chatequipemaroc-19b3d` Firebase project. To use your own backend instead:

1. Create a Firebase project and add an Android app with the package name `com.example.chatequipemaroc`.
2. Enable **Email/Password** sign-in under Authentication, then create a **Cloud Firestore** database and a **Storage** bucket.
3. Download your `google-services.json` and use it to replace `app/google-services.json`.
4. The chat screen filters messages on `sender` and `receiver` and orders them by `timestamp`, so Firestore needs a composite index for that query. The first time the query runs, Logcat shows an error containing a link that creates the index.

## Tests

The only tests in the project are the default Android Studio examples:

```bash
./gradlew test                 # unit tests
./gradlew connectedAndroidTest # instrumented tests (requires a device)
```
