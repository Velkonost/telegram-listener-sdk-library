package velkonost.telegram.sdk.listener.repository.messages

import kotlinx.coroutines.flow.*
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import velkonost.telegram.sdk.listener.model.NewMessage
import velkonost.telegram.sdk.listener.repository.BaseRepository

internal class MessagesRepository(
    dataSource: Client,
) : BaseRepository(dataSource) {

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

    suspend fun getMessage(chatId: Long, messageId: Long): TdApi.Message =
        this.sendFunctionAsync(TdApi.GetMessage(chatId, messageId))

}