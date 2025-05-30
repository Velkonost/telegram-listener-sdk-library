package velkonost.telegram.sdk.listener.extensions

import kotlinx.coroutines.*

/**
 * Extension function for CoroutineScope that launches a coroutine with error handling.
 * This function provides a convenient way to launch coroutines with custom error handling logic.
 *
 * @param catch Optional callback function that will be invoked when an exception occurs
 * @param block The coroutine code block to execute
 * @return Job representing the launched coroutine
 */
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