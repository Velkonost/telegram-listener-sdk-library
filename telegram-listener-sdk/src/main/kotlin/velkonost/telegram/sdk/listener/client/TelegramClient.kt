package velkonost.telegram.sdk.listener.client

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import velkonost.telegram.sdk.listener.config.TdLibParameters
import velkonost.telegram.sdk.listener.model.NewMessage
import velkonost.telegram.sdk.listener.repository.auth.AuthRepository
import velkonost.telegram.sdk.listener.repository.messages.MessagesRepository

/**
 * Internal client implementation that handles the communication with TDLib.
 * This class manages the lifecycle of the TDLib client, authentication, and message subscription.
 *
 * @property apiId The API ID for Telegram API
 * @property apiHash The API hash for Telegram API
 * @property phoneNumber The phone number associated with the Telegram account
 * @property databaseDirectory The directory for TDLib database files
 * @property filesDirectory The directory for downloaded files
 */
internal class TelegramClient(
    apiId: Int, apiHash: String, phoneNumber: String,
    databaseDirectory: String,
    filesDirectory: String,
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var updateEventsFlow: Flow<TdApi.Object>

    private lateinit var authRepository: AuthRepository
    private lateinit var messagesRepository: MessagesRepository

    init {
        Client.setLogMessageHandler(0) { verbosityLevel, message -> Unit }
        Client.execute(TdApi.SetLogVerbosityLevel(0))
        Client.execute(TdApi.SetLogStream(TdApi.LogStreamEmpty()))

        updateEventsFlow = callbackFlow {
            val resultHandler = Client.ResultHandler { trySend(it) }

            Client.create(
                resultHandler,
                null,
                null
            ).also {
                authRepository = AuthRepository(
                    dataSource = it,
                    parameters = TdLibParameters(
                        apiId = apiId,
                        apiHash = apiHash,
                        databaseDirectory = databaseDirectory,
                        filesDirectory = filesDirectory,
                        phoneNumber = phoneNumber
                    )
                )
                messagesRepository = MessagesRepository(it)
                launch()
            }


            awaitClose(block = { Unit })
        }.shareIn(
            scope = coroutineScope,
            replay = 1,
            started = SharingStarted.Eagerly
        )
    }

    private fun launch() {
        coroutineScope.launch {
            authRepository.subscribeAuthState(updateEventsFlow)
        }
    }

    /**
     * Starts listening to messages from specified chats.
     * This method creates a Flow of [NewMessage] objects that will emit new messages as they arrive.
     * The method waits for the messages repository to be initialized before returning the flow.
     *
     * @param includeOutgoing Whether to include outgoing messages in the flow
     * @param chats List of chat IDs to listen to
     * @return Flow of [NewMessage] objects
     */
    fun listenChats(
        includeOutgoing: Boolean,
        chats: List<Long>
    ): Flow<NewMessage> {
        val result = CompletableDeferred<Flow<NewMessage>>()
        coroutineScope.launch {
            while (!result.isCompleted) {
                delay(1000)
                if (::messagesRepository.isInitialized) {
                    val flow = messagesRepository.subscribeMessages(
                        source = updateEventsFlow,
                        includeOutgoing = includeOutgoing,
                        eligibleChats = chats
                    )
                    result.complete(flow)
                }
            }
        }

        return runBlocking { result.await() }
    }
}