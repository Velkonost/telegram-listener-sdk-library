package velkonost.telegram.sdk.listener.repository.auth

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import velkonost.telegram.sdk.listener.config.TdLibParameters
import velkonost.telegram.sdk.listener.extensions.launchCatching
import velkonost.telegram.sdk.listener.repository.BaseRepository

internal class AuthRepository(
    dataSource: Client,
    private val parameters: TdLibParameters
) : BaseRepository(dataSource) {

    suspend fun subscribeAuthState(source: Flow<TdApi.Object>) {
        source.getUpdatesFlowOfType<TdApi.UpdateAuthorizationState>()
            .mapNotNull { it.authorizationState }
            .map {
                when (it) {
                    is TdApi.AuthorizationStateReady -> AuthState.LoggedIn
                    is TdApi.AuthorizationStateWaitCode -> AuthState.EnterCode
                    is TdApi.AuthorizationStateWaitPhoneNumber -> AuthState.EnterPhone
                    is TdApi.AuthorizationStateWaitTdlibParameters -> AuthState.WaitTdLibs
                    else -> null
                }
            }
            .onEach {
                when (it) {
                    is AuthState.WaitTdLibs -> setTdLibParameters()
                    is AuthState.EnterPhone -> setPhoneNumber(parameters.phoneNumber)
                    is AuthState.EnterCode -> checkAuthenticationCode()
                    else -> Unit
                }
            }.launchIn(coroutineScope)

    }

    private fun setTdLibParameters() {
        val result = CompletableDeferred<TdApi.Ok>()
        coroutineScope.launchCatching(catch = result::completeExceptionally) {
            sendFunctionLaunch(
                TdApi.SetTdlibParameters(
                    parameters.useTestDc,
                    parameters.databaseDirectory,
                    parameters.filesDirectory,
                    parameters.databaseEncryptionKey,
                    parameters.useFileDatabase,
                    parameters.useChatInfoDatabase,
                    parameters.useMessageDatabase,
                    parameters.useSecretChats,
                    parameters.apiId,
                    parameters.apiHash,
                    parameters.systemLanguageCode,
                    parameters.deviceModel,
                    parameters.systemVersion,
                    parameters.applicationVersion
                )
            ).apply { result.complete(this) }
        }
        runBlocking { result.await() }
    }

    private suspend fun setPhoneNumber(phoneNumber: String?) =
        sendFunctionLaunch(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null))

    private suspend fun checkAuthenticationCode() {
        println("Enter code: ")
        val code = readln()
        sendFunctionLaunch(TdApi.CheckAuthenticationCode(code))
    }

    private suspend fun checkAuthenticationPassword(password: String?) =
        sendFunctionLaunch(TdApi.CheckAuthenticationPassword(password))


}