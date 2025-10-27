package com.example.wtchat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wtchat.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val db = Firebase.firestore

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, senha: String){

        if(email.isEmpty() || senha.isEmpty()){
            _authState.value = AuthState.Error("Por favor, preencha todos os campos.")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message?:"Algo deu errado, tente novamente.")
                    }
                }
    }

    fun signup(crm: String, nome: String, email: String, senha: String){

        if(email.isEmpty() || senha.isEmpty()){
            _authState.value = AuthState.Error("Por favor, preencha todos os campos.")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val userId = task.result?.user?.uid

                    val userModel = UserModel( userId!!,crm, nome, email)
                    db.collection("users").document(userId).set(userModel)
                        .addOnCompleteListener { taskdb ->
                            if(taskdb.isSuccessful) {
                                _authState.value = AuthState.Authenticated
                            } else {
                                _authState.value = AuthState.Error(task.exception?.message?:"Algo deu errado, tente novamente.")
                            }
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Algo deu errado, tente novamente.")
                }
            }
    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState(){
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}