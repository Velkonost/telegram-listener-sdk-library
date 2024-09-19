package velkonost.telegram.sdk.listener.client

import org.drinkless.tdlib.TdApi

internal sealed class TelegramException(message: String) : Throwable(message) {
    class Error(message: String) : TelegramException(message)
    class UnexpectedResult(result: TdApi.Object) : TelegramException("unexpected result: $result")
}