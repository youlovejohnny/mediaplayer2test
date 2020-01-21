package com.megastar.firebasetest.ui.main.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    companion object {
        const val LOGIN = 0
        const val SIGNUP = 1
    }

    var mode = SIGNUP
    private val auth = FirebaseAuth.getInstance()
    val liveData = MutableLiveData<AuthViewState>()

    fun onLoginOrSignUpClick(email: String, password: String) {
        if (mode == SIGNUP) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    liveData.value =
                        AuthViewState.OnSignUpSuccess
                    changeMode()
                } else {
                    liveData.value =
                        AuthViewState.OnSignUpError
                }
            }

        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    liveData.value =
                        AuthViewState.OnLoginSuccess
                } else {
                    liveData.value =
                        AuthViewState.OnLoginError
                }
            }
        }
    }




    fun changeMode() {
        if (mode == SIGNUP) {
            mode = LOGIN
            liveData.value = AuthViewState.OnModeLogin
        } else {
            mode = SIGNUP
            liveData.value = AuthViewState.OnModeSignUp
        }
    }
}
