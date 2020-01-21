package com.megastar.firebasetest.ui.main.auth

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.megastar.firebasetest.R
import com.megastar.firebasetest.Router
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment(R.layout.fragment_auth) {

    companion object {
        fun newInstance() = AuthFragment()
    }

    private lateinit var viewModel: AuthViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        viewModel.liveData.observe(this, Observer {
            onUpdate(it)
        })

        loginOrSignUpButton.setOnClickListener {
            viewModel.onLoginOrSignUpClick(emailEditText.text.toString(),passwordEditText.text.toString())
        }

        changeModeTextView.setOnClickListener { viewModel.changeMode() }
    }


    private fun showMessage(text: String) {
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
    }


    private fun onUpdate(state: AuthViewState) {
        when(state) {
            AuthViewState.OnModeSignUp -> {
                changeModeTextView.text = "Войти"
                loginOrSignUpButton.text = "Зарегистрироваться"
            }

            AuthViewState.OnModeLogin -> {
                loginOrSignUpButton.text = "Войти"
                changeModeTextView.text = "Зарегистрироваться"
            }

            AuthViewState.OnLoginError -> {
                showMessage("Не удалось войти")
            }

            AuthViewState.OnSignUpError -> {
                showMessage("Не удалось зарегистрироваться")
            }

            AuthViewState.OnSignUpSuccess -> {
                showMessage("Вы успешно зарегистрировались")
            }

            AuthViewState.OnLoginSuccess -> {
                (activity as Router).navigateToList()
            }
        }
    }
}
