package velkonost.telegram.sdk.listener.client

import org.drinkless.tdlib.TdApi

/**
 * Sealed class representing various exceptions that can occur during Telegram API operations.
 * This class is used to handle different types of errors that might occur when interacting with TDLib.
 */
internal sealed class TelegramException(message: String) : Throwable(message) {
    /**
     * Represents a general error that occurred during Telegram API operations.
     * This is used for errors that don't fit into other specific categories.
     *
     * @param message A description of the error that occurred
     */
    class Error(message: String) : TelegramException(message)

    /**
     * Represents an unexpected result received from TDLib.
     * This is thrown when the API returns a result type that wasn't expected in the current context.
     *
     * @param result The unexpected TDLib object that was received
     */
    class UnexpectedResult(result: TdApi.Object) : TelegramException("unexpected result: $result")
}