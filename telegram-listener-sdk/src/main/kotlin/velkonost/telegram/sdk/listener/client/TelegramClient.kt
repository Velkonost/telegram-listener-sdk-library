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
import velkonost.telegram.sdk.listener.repository.auth.AuthRepository
import velkonost.telegram.sdk.listener.repository.messages.MessagesRepository


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

    fun listenChats(chats: List<Pair<String, Long>>): Flow<Pair<String, String>> {
        val result = CompletableDeferred<Flow<Pair<String, String>>>()
        coroutineScope.launch {
            while (!result.isCompleted) {
                delay(1000)
                if (::messagesRepository.isInitialized) {
                    val flow = messagesRepository.subscribeMessages(updateEventsFlow, chats)
                    result.complete(flow)
                }
            }
        }

        return runBlocking { result.await() }
    }
}