package com.maveri.figma.main.viewmodel

import com.maveri.figma.model.Location

interface MainViewState {

    data class State(
        val authStatus: AuthFirebaseStatus? = null,
        val streetName: String? = null,
        val locations: MutableList<Location>? = null
    )

    sealed class AuthFirebaseStatus {
        object Success : AuthFirebaseStatus()
        object Error : AuthFirebaseStatus()
    }

}