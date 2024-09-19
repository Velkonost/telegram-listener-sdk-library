package velkonost.telegram.sdk.listener.repository.messages

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import velkonost.telegram.sdk.listener.repository.BaseRepository

internal class MessagesRepository(
    dataSource: Client,
) : BaseRepository(dataSource) {

    fun subscribeMessages(source: Flow<TdApi.Object>, eligibleChats: List<Pair<String, Long>>): Flow<Pair<String, String>> {
        return source.getUpdatesFlowOfType<TdApi.UpdateNewMessage>()
            .mapNotNull { it.message }
            .mapNotNull { message ->
                eligibleChats.firstOrNull { it.second == message.chatId }?.let { (chatTitle, chatId) ->
                    (message.content as? TdApi.MessageText)?.let { messageText ->
                        chatTitle to messageText.text.text
                    }
                }
            }
    }

}