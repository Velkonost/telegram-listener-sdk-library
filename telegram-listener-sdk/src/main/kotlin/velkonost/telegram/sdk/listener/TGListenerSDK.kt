package velkonost.telegram.sdk.listener

import kotlinx.coroutines.flow.Flow
import velkonost.telegram.sdk.listener.client.TelegramClient
import velkonost.telegram.sdk.listener.client.TelegramException
import velkonost.telegram.sdk.listener.model.NewMessage
import java.io.File
import java.nio.file.Files

object TGListenerSDK {

    private var client: TelegramClient? = null

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

    fun startListenChats(
        includeOutgoing: Boolean = true,
        chats: List<Long>
    ): Flow<NewMessage> {
        return client?.listenChats(includeOutgoing, chats) ?: throw TelegramException.Error("tg client is null")
    }

}