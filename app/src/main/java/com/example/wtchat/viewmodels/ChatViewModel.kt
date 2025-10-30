package com.example.wtchat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtchat.data.repository.LastAcessRepository
import com.google.firebase.firestore.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: LastAcessRepository
) : ViewModel() {

    fun listenForNewMessages(chatId: String, onNewMessage: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .addSnapshotListener { snapshot, e ->
                if (snapshot == null || e != null) return@addSnapshotListener

                viewModelScope.launch {
                    val lastSeen = repository.getLastSeen(chatId)

                    for (docChange in snapshot.documentChanges) {
                        if (docChange.type == DocumentChange.Type.ADDED) {
                            val messageTime = docChange.document.getLong("timestamp") ?: 0L
                            val content = docChange.document.getString("content") ?: ""

                            if (messageTime > lastSeen) {
                                onNewMessage(content)
                            }
                        }
                    }
                }
            }
    }

    fun updateLastSeen(chatId: String) {
        viewModelScope.launch {
            repository.updateLastSeen(chatId)
        }
    }
}
