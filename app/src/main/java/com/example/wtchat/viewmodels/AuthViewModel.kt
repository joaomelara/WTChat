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
    private val authService = RetrofitInstance.getInstance().authService

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

    fun isAdmin(): Boolean {
        return tokenManager.getUser()?.roles?.contains("ROLE_ADMIN") == true
    }

    fun getUsername(): String? {
        return tokenManager.getUser()?.username
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
                    id = response.id,
                    username = response.username,
                    name = response.name,
                    email = response.email,
                    roles = response.roles,
                    segment = response.segment
                )
                tokenManager.saveToken(response.accessToken, loggedInUser)
                _authToken.value = response.accessToken
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                println("Login error: ${e.message}")
                _authState.value = AuthState.Error(e.message ?: "Algo deu errado, tente novamente.")
            }
        }
    }

    fun signup(username: String, name: String, email: String, senha: String, segment: String, roles: Set<String>? = null){

        if(email.isEmpty() || senha.isEmpty() || username.isEmpty() || name.isEmpty() || segment.isEmpty()){
            _authState.value = AuthState.Error("Por favor, preencha todos os campos.")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                // Send signup request
                authService.signUp(SignUpRequest(username, name, email, senha, segment, roles))

                // If signup is successful, auto-login the user
                val response = authService.signIn(SignInRequest(email, senha))
                val signedInUser = UserModel(
                    id = response.id,
                    username = response.username,
                    name = response.name,
                    email = response.email,
                    roles = response.roles,
                    segment = response.segment
                )
                tokenManager.saveToken(response.accessToken, signedInUser)
                _authToken.value = response.accessToken
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                println("Signup error: ${e.message}")
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