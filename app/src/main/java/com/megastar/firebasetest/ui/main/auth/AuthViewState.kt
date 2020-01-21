package com.megastar.firebasetest.ui.main.auth

sealed class AuthViewState {
    object OnSignUpError: AuthViewState()
    object OnSignUpSuccess: AuthViewState()
    object OnLoginError: AuthViewState()
    object OnLoginSuccess: AuthViewState()
    object OnModeLogin: AuthViewState()
    object OnModeSignUp: AuthViewState()
}