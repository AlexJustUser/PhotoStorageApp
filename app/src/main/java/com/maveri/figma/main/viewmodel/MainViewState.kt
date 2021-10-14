package com.maveri.figma.main.viewmodel

interface MainViewState {

    data class State(
        val authStatus: AuthFirebaseStatus? = null,
        val token: String? = null
    )

    sealed class AuthFirebaseStatus {
        object Success : AuthFirebaseStatus()
        object Error : AuthFirebaseStatus()
    }

}