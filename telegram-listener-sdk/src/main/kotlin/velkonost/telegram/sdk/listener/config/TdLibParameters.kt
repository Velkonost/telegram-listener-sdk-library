package velkonost.telegram.sdk.listener.config

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