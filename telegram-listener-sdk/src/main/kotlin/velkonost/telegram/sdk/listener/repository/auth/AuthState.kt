package velkonost.telegram.sdk.listener.repository.auth

/**
 * Sealed interface representing different states of the Telegram authentication process.
 * This is used to track and handle the various stages of user authentication with TDLib.
 */
internal sealed interface AuthState {
    /**
     * Represents the state when the user is successfully logged in.
     * This is the final state of the authentication process.
     */
    data object LoggedIn : AuthState

    /**
     * Represents the state when the user needs to enter their phone number.
     * This is typically the first state in the authentication process.
     */
    data object EnterPhone : AuthState

    /**
     * Represents the state when the user needs to enter the authentication code.
     * This state occurs after the phone number has been entered and verified.
     */
    data object EnterCode : AuthState

    /**
     * Represents the state when TDLib is waiting for its parameters to be set.
     * This is the initial state before any authentication can begin.
     */
    data object WaitTdLibs : AuthState
}