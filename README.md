# Telegram Listener SDK

A Kotlin library for interacting with Telegram API using TDLib. This SDK provides a simple way to listen to messages from specified Telegram chats and handle authentication.

## Features

- 🔐 Secure authentication using TDLib
- 📱 Support for multiple platforms (Linux, macOS, Windows)
- 💬 Real-time message listening
- 🔄 Coroutine-based API with Kotlin Flow
- 🛡️ Error handling and state management
- 📦 Easy integration with existing projects

## Requirements

- Kotlin 1.8.0 or higher
- JDK 11 or higher
- Telegram API credentials (api_id and api_hash)

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    // Add your repository here if the library is hosted privately
}

dependencies {
    implementation("com.velkonost:telegram-listener-sdk:0.1.0") // Replace with actual version
}
```

## Getting Started

### 1. Obtain Telegram API Credentials

1. Visit https://my.telegram.org/auth
2. Log in with your phone number
3. Go to 'API development tools'
4. Create a new application
5. Note down your `api_id` and `api_hash`

### 2. Initialize the SDK

```kotlin
import velkonost.telegram.sdk.listener.TGListenerSDK

// Initialize the SDK with your credentials
TGListenerSDK.setup(
    apiId = YOUR_API_ID,           // Int
    apiHash = "YOUR_API_HASH",     // String
    phoneNumber = "+1234567890",   // String
    databaseDirectory = "database", // Optional, defaults to "database"
    filesDirectory = "files"       // Optional, defaults to "files"
)
```

### 3. Listen to Messages

```kotlin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import velkonost.telegram.sdk.listener.TGListenerSDK
import velkonost.telegram.sdk.listener.model.NewMessage

// Start listening to messages from specific chats
val messageFlow = TGListenerSDK.startListenChats(
    includeOutgoing = true,  // Set to false to exclude outgoing messages
    chats = listOf(123456789L) // List of chat IDs to listen to
)

// Collect messages
runBlocking {
    messageFlow.collect { message ->
        println("New message in chat ${message.chatId}: ${message.text}")
        message.replyMessageText?.let { reply ->
            println("Reply to: $reply")
        }
    }
}
```

## Platform Support

The SDK supports the following platforms:
- Linux (x64, arm64)
- macOS (Apple Silicon)
- Windows (x64)

The appropriate native library will be automatically loaded based on your system.

## Authentication Flow

The SDK handles the authentication process automatically:

1. When `setup()` is called, the SDK initializes TDLib
2. If this is the first run, you'll need to enter the verification code sent to your Telegram account
3. The authentication state is managed internally
4. Once authenticated, the session is persisted in the database directory

## Error Handling

The SDK uses a custom exception hierarchy for error handling:

```kotlin
try {
    // SDK operations
} catch (e: TelegramException.Error) {
    // Handle general Telegram API errors
    println("Error: ${e.message}")
} catch (e: TelegramException.UnexpectedResult) {
    // Handle unexpected API responses
    println("Unexpected result: ${e.message}")
}
```

## Message Model

Messages are represented by the `NewMessage` data class:

```kotlin
data class NewMessage(
    val chatId: Long,        // ID of the chat
    val text: String,        // Message text
    val replyMessageText: String? = null  // Text of the message being replied to, if any
)
```

## Directory Structure

The SDK uses two main directories:
- `databaseDirectory`: Stores TDLib database files
- `filesDirectory`: Stores downloaded files

Make sure these directories are writable by your application.

## Security Considerations

1. Never commit your `api_id` and `api_hash` to version control
2. Store credentials securely (e.g., in environment variables or a secure configuration)
3. The database directory may contain sensitive information, ensure it's properly secured
4. Use appropriate file permissions for the database and files directories

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For support, please open an issue in the GitHub repository.
