package com.example.wtchat.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.models.SignInRequest
import com.example.wtchat.models.SignUpRequest
import com.example.wtchat.models.UserModel
import com.example.wtchat.utils.TokenManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val authService = RetrofitInstance.authService

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> = _authToken

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        val token = tokenManager.getAccessToken()
        if (token == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authToken.value = token
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, senha: String){

        if (email.isEmpty() || senha.isEmpty()) {
            _authState.value = AuthState.Error("Por favor, preencha todos os campos.")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val response = authService.signIn(SignInRequest(email, senha))
                val loggedInUser = UserModel(
                    uid = response.id,
                    email = response.email,
                    crm = response.username,
                    roles = response.roles,
                    segment = response.segment
                )
                tokenManager.saveToken(response.accessToken, loggedInUser)
                _authToken.value = response.accessToken
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Algo deu errado, tente novamente.")
            }
        }
    }

    fun signup(crm: String, nome: String, email: String, senha: String){

        if(email.isEmpty() || senha.isEmpty() || crm.isEmpty() || nome.isEmpty()){
            _authState.value = AuthState.Error("Por favor, preencha todos os campos.")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val response = authService.signUp(SignUpRequest(crm, nome, email, senha))
                val signedInuser = UserModel(
                    uid = response.id,
                    email = response.email,
                    crm = response.username,
                    roles = response.roles,
                    segment = response.segment
                )
                tokenManager.saveToken(response.accessToken, signedInuser)
                _authToken.value = response.accessToken
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Algo deu errado, tente novamente.")
            }
        }
    }

    fun signout(){
        tokenManager.clearToken()
        _authToken.value = null
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState(){
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}