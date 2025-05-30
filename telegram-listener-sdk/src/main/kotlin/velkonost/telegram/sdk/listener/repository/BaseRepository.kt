package velkonost.telegram.sdk.listener.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import velkonost.telegram.sdk.listener.client.TelegramException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Abstract base class for all repositories that interact with TDLib.
 * Provides common functionality for handling TDLib client operations and coroutine management.
 *
 * @property dataSource The TDLib client instance used for API calls
 */
internal abstract class BaseRepository(private val dataSource: Client) {

    protected val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Extension function that filters a Flow of TDLib objects to only include objects of a specific type.
     * This is useful for subscribing to specific types of updates from TDLib.
     *
     * @return A Flow containing only objects of the specified type T
     */
    protected inline fun <reified T : TdApi.Object> Flow<TdApi.Object>.getUpdatesFlowOfType() = filterIsInstance<T>()

    /**
     * Sends a function to TDLib asynchronously and waits for the response.
     * This method handles the conversion of the response to the expected type and error handling.
     *
     * @param function The TDLib function to send
     * @return The result of the function call, cast to the expected type
     * @throws TelegramException.Error if the function call fails
     * @throws TelegramException.UnexpectedResult if the response type is unexpected
     */
    protected suspend inline fun <reified ExpectedResult : TdApi.Object, T : TdApi.Object> sendFunctionAsync(
        function: TdApi.Function<T>
    ): ExpectedResult = suspendCoroutine { continuation ->
        val resultHandler: (TdApi.Object) -> Unit = { result ->
            when (result) {
                is ExpectedResult -> continuation.resume(result)
                is TdApi.Error -> continuation.resumeWithException(
                    TelegramException.Error(result.message)
                )

                else -> continuation.resumeWithException(
                    TelegramException.UnexpectedResult(result)
                )
            }
        }

        dataSource.send(function, resultHandler) { throwable ->
            continuation.resumeWithException(TelegramException.Error(throwable?.message ?: "unknown"))
        }
    }

    /**
     * Sends a function to TDLib and expects an Ok response.
     * This is a convenience method for functions that don't return any specific data.
     *
     * @param function The TDLib function to send
     * @return TdApi.Ok if the function call was successful
     * @throws TelegramException.Error if the function call fails
     * @throws TelegramException.UnexpectedResult if the response is not Ok
     */
    protected suspend fun <T : TdApi.Object> sendFunctionLaunch(function: TdApi.Function<T>) =
        sendFunctionAsync<TdApi.Ok, T>(function)


}