package com.d2d.challenge.data.socket

import com.d2d.challenge.common.Constant.NORMAL_CLOSURE_STATUS
import com.d2d.challenge.data.entity.Payload
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketCallback(private val gson: Gson): WebSocketListener(){

    val _socketChannel: Channel<SocketUpdate> = Channel()

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            _socketChannel.send(SocketUpdate(gson.fromJson(text, Payload::class.java)))
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            _socketChannel.send(SocketUpdate(exception = SocketAbortedException()))
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        _socketChannel.close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            _socketChannel.send(SocketUpdate(exception = t))
        }
    }

}

class SocketAbortedException : Exception()