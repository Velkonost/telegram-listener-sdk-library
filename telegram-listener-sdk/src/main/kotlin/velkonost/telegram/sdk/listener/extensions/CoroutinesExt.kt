package velkonost.telegram.sdk.listener.extensions

import kotlinx.coroutines.*

fun CoroutineScope.launchCatching(
    catch: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(
    context = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        catch?.invoke(throwable)
    },
    block = block
)