package velkonost.telegram.sdk.listener.model

data class NewMessage(
    val chatId: Long,
    val text: String
)