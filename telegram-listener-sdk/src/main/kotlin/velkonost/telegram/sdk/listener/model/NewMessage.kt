package velkonost.telegram.sdk.listener.model

/**
 * Data class representing a new message received from Telegram.
 * This class contains the essential information about a message that was received or sent.
 *
 * @property chatId The unique identifier of the chat where the message was sent/received
 * @property text The text content of the message
 * @property replyMessageText The text of the message being replied to, if this message is a reply
 */
data class NewMessage(
    val chatId: Long,
    val text: String,
    val replyMessageText: String? = null
)