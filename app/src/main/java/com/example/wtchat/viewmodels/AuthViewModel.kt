package com.example.wtchat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtchat.api.RetrofitInstance
import com.example.wtchat.models.SignInRequest
import com.example.wtchat.models.SignUpRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authService = RetrofitInstance.authService

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> = _authToken

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        // Check if token exists (implement token persistence with DataStore/SharedPreferences)
        _authState.value = if(_authToken.value != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun login(email: String, senha: String){

        if(email.isEmpty() || senha.isEmpty()){
            _authState.value = AuthState.Error("Por favor, preencha todos os campos.")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val response = authService.signIn(SignInRequest(email, senha))
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
                _authToken.value = response.accessToken
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Algo deu errado, tente novamente.")
            }
        }
    }

    fun signout(){
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