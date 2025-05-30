package velkonost.telegram.sdk.listener.repository.auth

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import velkonost.telegram.sdk.listener.config.TdLibParameters
import velkonost.telegram.sdk.listener.extensions.launchCatching
import velkonost.telegram.sdk.listener.repository.BaseRepository

/**
 * Repository class responsible for handling authentication operations with TDLib.
 * This class manages the authentication process, including setting up TDLib parameters,
 * handling phone number verification, and authentication code verification.
 *
 * @property dataSource The TDLib client instance used for API calls
 * @property parameters The TDLib parameters used for initialization
 */
internal class AuthRepository(
    dataSource: Client,
    private val parameters: TdLibParameters
) : BaseRepository(dataSource) {

    /**
     * Subscribes to authentication state updates and handles the authentication process.
     * This method sets up a flow that processes authentication state changes and
     * automatically handles the necessary steps for each state.
     *
     * @param source The source Flow of TDLib updates
     */
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

    /**
     * Sets the TDLib parameters required for initialization.
     * This method is called when the authentication state is WaitTdLibs.
     * It configures various aspects of TDLib's behavior using the provided parameters.
     *
     * @throws TelegramException.Error if the parameters cannot be set
     */
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

    /**
     * Sets the phone number for authentication.
     * This method is called when the authentication state is EnterPhone.
     *
     * @param phoneNumber The phone number to use for authentication
     * @throws TelegramException.Error if the phone number cannot be set
     */
    private suspend fun setPhoneNumber(phoneNumber: String?) =
        sendFunctionLaunch(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null))

    /**
     * Handles the authentication code verification process.
     * This method is called when the authentication state is EnterCode.
     * It prompts the user for the authentication code and sends it to TDLib.
     *
     * @throws TelegramException.Error if the code verification fails
     */
    private suspend fun checkAuthenticationCode() {
        println("Enter code: ")
        val code = readln()
        sendFunctionLaunch(TdApi.CheckAuthenticationCode(code))
    }

    /**
     * Verifies the authentication password if required.
     * This method is used when two-factor authentication is enabled.
     *
     * @param password The authentication password
     * @throws TelegramException.Error if the password verification fails
     */
    private suspend fun checkAuthenticationPassword(password: String?) =
        sendFunctionLaunch(TdApi.CheckAuthenticationPassword(password))


}