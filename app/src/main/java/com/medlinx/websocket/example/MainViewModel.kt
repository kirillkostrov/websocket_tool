package com.medlinx.websocket.example

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLSocketFactory

class MainViewModel : ViewModel() {
    val url = MutableLiveData(WEB_SOCKET_URL_DEFAULT)
    val request = MutableLiveData(REQUEST_DEFAULT)
    val response = MutableLiveData("")
    val isConnected = MutableLiveData(false)
    val connectedButtonText = MutableLiveData("connected")

    val errorMessage = MutableLiveData("")

    private lateinit var webSocketClient: WebSocketClient

    fun init (lifecycleOwner : LifecycleOwner) {
        this.isConnected.observe(lifecycleOwner) {
            connectedButtonText.value =
            when (it) {
                true -> "disconnect"
                false -> "connect"
            }
        }
    }

    fun connectWebSocket() {
        initWebSocket()
    }

    fun closeWebsSocket() {
        webSocketClient.close()
    }

    fun sendToWebSocket() {
        if (isConnected.value == true) webSocketClient.send(request.value)
    }

    private fun initWebSocket() {
        createWebSocketClient(URI(url.value))
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        webSocketClient.setSocketFactory(socketFactory)
        webSocketClient.connect()
    }

    private fun createWebSocketClient(webSocketUri: URI?) {
        webSocketClient = object : WebSocketClient(webSocketUri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")

                isConnected.postValue(true)
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage: $message")
                response.postValue(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
                isConnected.postValue(false)
            }

            override fun onError(ex: Exception?) {
                errorMessage.postValue("Error: ${ex?.message}")
                Log.e(TAG, "onError: ${ex?.message}")
            }
        }
    }

    companion object {
        const val WEB_SOCKET_URL_DEFAULT = "wss://ws-feed.pro.coinbase.com"
        const val TAG = "WEBSOCKET_TEST"
        const val REQUEST_DEFAULT = "{\n" +
                "    \"type\": \"subscribe\",\n" +
                "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
                "}"
    }

}