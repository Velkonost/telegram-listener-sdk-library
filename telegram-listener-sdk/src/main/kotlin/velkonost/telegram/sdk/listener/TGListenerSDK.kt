package velkonost.telegram.sdk.listener

import kotlinx.coroutines.flow.Flow
import velkonost.telegram.sdk.listener.client.TelegramClient
import velkonost.telegram.sdk.listener.client.TelegramException
import velkonost.telegram.sdk.listener.model.NewMessage
import java.io.File
import java.nio.file.Files

/**
 * Main entry point for the Telegram Listener SDK.
 * This object provides functionality to interact with the Telegram API using TDLib.
 * It handles the initialization of the native library and provides methods to listen to chat messages.
 */
object TGListenerSDK {

    private var client: TelegramClient? = null

    /**
     * Initializes the Telegram client with the provided credentials and configuration.
     * This method must be called before using any other functionality of the SDK.
     * It handles loading the appropriate native library based on the operating system and architecture.
     *
     * @param apiId The API ID obtained from Telegram's developer portal
     * @param apiHash The API hash obtained from Telegram's developer portal
     * @param phoneNumber The phone number associated with the Telegram account
     * @param databaseDirectory The directory where TDLib will store its database files
     * @param filesDirectory The directory where TDLib will store downloaded files
     * @throws UnsupportedOperationException if the current OS or architecture is not supported
     * @throws IllegalStateException if the native library fails to load
     */
    fun setup(
        apiId: Int,
        apiHash: String,
        phoneNumber: String,
        databaseDirectory: String = "database",
        filesDirectory: String = "files",
    ) {
        val osName = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()
        val libFolder = when {
            osName.contains("linux") && arch.contains("x86_64") -> "linux_x64"
            osName.contains("linux") && arch.contains("arm64") -> "linux_arm64"
            osName.contains("linux") -> "linux_x64"
            osName.contains("mac") && arch.contains("aarch64") -> "macos_silicon"
            osName.contains("windows") && arch.contains("amd64") -> "windows_x64"
            else -> throw UnsupportedOperationException("Unsupported OS or architecture: $osName-$arch")
        }

        val libExtension = when {
            osName.contains("linux") -> "so"
            osName.contains("mac") && arch.contains("aarch64") -> "dylib"
            osName.contains("windows") && arch.contains("amd64") -> "dll"
            else -> throw UnsupportedOperationException("Unsupported OS or architecture: $osName-$arch")
        }

        val classLoader = this::class.java
        val libResource = classLoader.getResource("/libs/$libFolder/libtdjni.$libExtension")

        if (libResource != null) {
            val tempFile = Files.createTempFile("libtdjni", ".$libExtension").toFile()
            tempFile.deleteOnExit()

            libResource.openStream().use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            System.load(tempFile.absolutePath)
            println("TdLib loaded successfully")
        } else {
            throw IllegalStateException("TdLib load fails")
        }

        client = TelegramClient(
            apiId = apiId,
            apiHash = apiHash,
            phoneNumber = phoneNumber,
            databaseDirectory = databaseDirectory,
            filesDirectory = filesDirectory
        )
    }

    /**
     * Starts listening to messages from specified chats.
     * Returns a Flow of [NewMessage] objects that can be collected to receive new messages.
     *
     * @param includeOutgoing Whether to include outgoing messages in the flow
     * @param chats List of chat IDs to listen to
     * @return Flow of [NewMessage] objects
     * @throws TelegramException.Error if the client is not initialized (setup() was not called)
     */
    fun startListenChats(
        includeOutgoing: Boolean = true,
        chats: List<Long>
    ): Flow<NewMessage> {
        return client?.listenChats(includeOutgoing, chats) ?: throw TelegramException.Error("tg client is null")
    }

}