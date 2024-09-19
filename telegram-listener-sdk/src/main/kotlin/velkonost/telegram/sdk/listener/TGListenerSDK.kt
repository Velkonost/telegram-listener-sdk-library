package velkonost.telegram.sdk.listener

import kotlinx.coroutines.flow.Flow
import velkonost.telegram.sdk.listener.client.TelegramClient
import velkonost.telegram.sdk.listener.exception.TGListenerException
import java.io.File

object TGListenerSDK {

    private var client: TelegramClient? = null

    fun setup(
        apiId: Int,
        apiHash: String,
        phoneNumber: String,
        databaseDirectory: String = "database",
        filesDirectory: String = "files",
    ) {
        val projectDir = File("").absolutePath
        val tdLibPath = "$projectDir/telegram-sdk/tdlib/src/main/libs"

        val osName = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        val libFolder = when {
            osName.contains("linux") && arch.contains("x86_64") -> "linux_x64"
            osName.contains("linux") && arch.contains("arm64") -> "linux_arm64"
            osName.contains("mac") && arch.contains("aarch64") -> "macos_silicon"
            osName.contains("windows") && arch.contains("amd64") -> "windows_x64"
            else -> throw UnsupportedOperationException("Unsupported OS or architecture: $osName-$arch")
        }

        val libExtension = when {
            osName.contains("linux") && arch.contains("x86_64") -> "iso"
            osName.contains("linux") && arch.contains("arm64") -> "iso"
            osName.contains("mac") && arch.contains("aarch64") -> "dylib"
            osName.contains("windows") && arch.contains("amd64") -> "dll"
            else -> throw UnsupportedOperationException("Unsupported OS or architecture: $osName-$arch")
        }

        val libPath = "$tdLibPath/$libFolder/libtdjni.$libExtension"

        val libFile = File(libPath)
        if (!libFile.exists()) {
            throw IllegalStateException("Library file not found at $libPath")
        }

        System.load(libFile.absolutePath)

        client = TelegramClient(
            apiId = apiId,
            apiHash = apiHash,
            phoneNumber = phoneNumber,
            databaseDirectory = databaseDirectory,
            filesDirectory = filesDirectory
        )
    }

    fun startListenChats(chats: List<Pair<String, Long>>): Flow<Pair<String, String>> {
        return client?.listenChats(chats) ?: throw TGListenerException("tg client is null")
    }

}