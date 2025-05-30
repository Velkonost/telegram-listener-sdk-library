package velkonost.telegram.sdk.listener.repository.messages

import kotlinx.coroutines.flow.*
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import velkonost.telegram.sdk.listener.model.NewMessage
import velkonost.telegram.sdk.listener.repository.BaseRepository

/**
 * Repository class responsible for handling message-related operations with TDLib.
 * This class manages the subscription to new messages and provides methods to retrieve message information.
 *
 * @property dataSource The TDLib client instance used for API calls
 */
internal class MessagesRepository(
    dataSource: Client,
) : BaseRepository(dataSource) {

    /**
     * Subscribes to new messages from specified chats.
     * Creates a Flow that emits [NewMessage] objects when new messages are received.
     * The flow can be configured to include or exclude outgoing messages and to filter by specific chats.
     *
     * @param source The source Flow of TDLib updates
     * @param includeOutgoing Whether to include outgoing messages in the flow
     * @param eligibleChats List of chat IDs to listen to
     * @return Flow of [NewMessage] objects
     */
    fun subscribeMessages(
        source: Flow<TdApi.Object>,
        includeOutgoing: Boolean,
        eligibleChats: List<Long>
    ): Flow<NewMessage> {
        return source.getUpdatesFlowOfType<TdApi.UpdateNewMessage>()
            .mapNotNull { it.message }
            .filter { message -> message.chatId in eligibleChats }
            .filter { includeOutgoing || !it.isOutgoing }
            .mapNotNull { message ->
                val isReply = message.replyTo != null && message.replyTo is TdApi.MessageReplyToMessage
                var replyMessageText: String? = null
                if (isReply) {
                    val repliedMessage = getMessage(
                        chatId = message.chatId,
                        messageId = (message.replyTo as TdApi.MessageReplyToMessage).messageId
                    )
                    replyMessageText = (repliedMessage.content as? TdApi.MessageText)?.text?.text
                }

                (message.content as? TdApi.MessageText)?.let { messageText ->
                    NewMessage(
                        chatId = message.chatId,
                        text = messageText.text.text,
                        replyMessageText = replyMessageText
                    )
                }
            }
    }

    /**
     * Retrieves a specific message from a chat.
     *
     * @param chatId The ID of the chat containing the message
     * @param messageId The ID of the message to retrieve
     * @return The requested message
     * @throws TelegramException.Error if the message cannot be retrieved
     */
    suspend fun getMessage(chatId: Long, messageId: Long): TdApi.Message =
        this.sendFunctionAsync(TdApi.GetMessage(chatId, messageId))

}