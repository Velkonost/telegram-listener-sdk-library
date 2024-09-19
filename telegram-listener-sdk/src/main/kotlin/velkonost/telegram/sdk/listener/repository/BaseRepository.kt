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

internal abstract class BaseRepository(private val dataSource: Client) {

    protected val coroutineScope = CoroutineScope(Dispatchers.IO)

    protected inline fun <reified T : TdApi.Object> Flow<TdApi.Object>.getUpdatesFlowOfType() = filterIsInstance<T>()

    private suspend inline fun <reified ExpectedResult : TdApi.Object, T : TdApi.Object> sendFunctionAsync(
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

    protected suspend fun <T : TdApi.Object> sendFunctionLaunch(function: TdApi.Function<T>) =
        sendFunctionAsync<TdApi.Ok, T>(function)


}