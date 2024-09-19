package velkonost.telegram.sdk.listener.repository.auth

internal sealed interface AuthState {
    data object LoggedIn : AuthState
    data object EnterPhone : AuthState
    data object EnterCode : AuthState
    data object WaitTdLibs : AuthState
}