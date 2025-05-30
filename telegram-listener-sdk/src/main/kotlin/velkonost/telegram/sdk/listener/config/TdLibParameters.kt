package velkonost.telegram.sdk.listener.config

/**
 * Data class containing all parameters required for initializing TDLib.
 * These parameters configure various aspects of TDLib's behavior and functionality.
 *
 * @property useTestDc Whether to use the test data center
 * @property databaseDirectory Directory where TDLib will store its database files
 * @property filesDirectory Directory where TDLib will store downloaded files
 * @property databaseEncryptionKey Optional encryption key for the database
 * @property useFileDatabase Whether to use the file database
 * @property useChatInfoDatabase Whether to use the chat info database
 * @property useMessageDatabase Whether to use the message database
 * @property useSecretChats Whether to enable secret chats
 * @property apiId The API ID obtained from Telegram's developer portal
 * @property apiHash The API hash obtained from Telegram's developer portal
 * @property systemLanguageCode The language code to use for the system
 * @property deviceModel The model of the device
 * @property systemVersion The version of the operating system
 * @property applicationVersion The version of the application
 * @property phoneNumber The phone number associated with the Telegram account
 */
internal data class TdLibParameters(
    val useTestDc: Boolean = false,
    val databaseDirectory: String,
    val filesDirectory: String,
    val databaseEncryptionKey: ByteArray? = null,
    val useFileDatabase: Boolean = true,
    val useChatInfoDatabase: Boolean = true,
    val useMessageDatabase: Boolean = true,
    val useSecretChats: Boolean = false,
    val apiId: Int,
    val apiHash: String,
    val systemLanguageCode: String = "en",
    val deviceModel: String = "Android",
    val systemVersion: String = "Example",
    val applicationVersion: String = "1.0",
    val phoneNumber: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TdLibParameters

        if (useTestDc != other.useTestDc) return false
        if (databaseDirectory != other.databaseDirectory) return false
        if (filesDirectory != other.filesDirectory) return false
        if (databaseEncryptionKey != null) {
            if (other.databaseEncryptionKey == null) return false
            if (!databaseEncryptionKey.contentEquals(other.databaseEncryptionKey)) return false
        } else if (other.databaseEncryptionKey != null) return false
        if (useFileDatabase != other.useFileDatabase) return false
        if (useChatInfoDatabase != other.useChatInfoDatabase) return false
        if (useMessageDatabase != other.useMessageDatabase) return false
        if (useSecretChats != other.useSecretChats) return false
        if (apiId != other.apiId) return false
        if (apiHash != other.apiHash) return false
        if (systemLanguageCode != other.systemLanguageCode) return false
        if (deviceModel != other.deviceModel) return false
        if (systemVersion != other.systemVersion) return false
        if (applicationVersion != other.applicationVersion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = useTestDc.hashCode()
        result = 31 * result + databaseDirectory.hashCode()
        result = 31 * result + filesDirectory.hashCode()
        result = 31 * result + (databaseEncryptionKey?.contentHashCode() ?: 0)
        result = 31 * result + useFileDatabase.hashCode()
        result = 31 * result + useChatInfoDatabase.hashCode()
        result = 31 * result + useMessageDatabase.hashCode()
        result = 31 * result + useSecretChats.hashCode()
        result = 31 * result + apiId
        result = 31 * result + apiHash.hashCode()
        result = 31 * result + systemLanguageCode.hashCode()
        result = 31 * result + deviceModel.hashCode()
        result = 31 * result + systemVersion.hashCode()
        result = 31 * result + applicationVersion.hashCode()
        return result
    }
}