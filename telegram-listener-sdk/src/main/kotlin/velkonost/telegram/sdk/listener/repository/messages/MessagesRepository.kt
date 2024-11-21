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
                (message.content as? TdApi.MessageText)?.let { messageText ->
                    NewMessage(
                        chatId = message.chatId,
                        text = messageText.text.text
                    )
                }
            }
    }

}