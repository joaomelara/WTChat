package com.example.wtchat.websocket

import androidx.lifecycle.MutableLiveData
import com.example.wtchat.models.MessageModel
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class ChatWebSocketManager {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    val incomingMessages = MutableLiveData<MessageModel>()
    val connectionStatus = MutableLiveData<WebSocketStatus>()

    fun connect(token: String) {
        val url = "wss://wtchat-backend.jollyfield-5dc0fa80.brazilsouth.azurecontainerapps.io/chat"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, ChatWebSocketListener())
        connectionStatus.postValue(WebSocketStatus.Connecting)
    }

    fun sendMessage(message: MessageModel) {
        try {
            val json = json.encodeToString(MessageModel.serializer(), message)
            webSocket?.send(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        connectionStatus.postValue(WebSocketStatus.Disconnected)
    }

    private inner class ChatWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            println("WebSocket connected")
            connectionStatus.postValue(WebSocketStatus.Connected)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val message = json.decodeFromString(MessageModel.serializer(), text)
                incomingMessages.postValue(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            connectionStatus.postValue(WebSocketStatus.Error)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            println("WebSocket disconnected")
            connectionStatus.postValue(WebSocketStatus.Disconnected)
        }
    }
}

enum class WebSocketStatus {
    Connecting,
    Connected,
    Disconnected,
    Error
}