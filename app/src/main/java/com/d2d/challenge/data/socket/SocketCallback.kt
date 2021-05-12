@file:Suppress("PropertyName")

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


/**
 * Class is responsible for implementing WebSocketListener. All web socket events is received in this class.
 * @param gson gson object used for serializing json payload into provided object class.
 */
class SocketCallback(private val gson: Gson): WebSocketListener(){

    val _socketChannel: Channel<SocketUpdate> = Channel()


    /**
     *  Receive successful socket event and launch it to coroutine global scope.
     *  @param webSocket a non-blocking interface to a web socket create OkHttpClient instance from socket factory
     *  @param text actual response upon successful event
     */
    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            _socketChannel.send(SocketUpdate(gson.fromJson(text, Payload::class.java)))
        }
    }


    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     * @param webSocket a non-blocking interface to a web socket create OkHttpClient instance from socket factory
     * @param code integer code to indicate socket closure status
     * @param reason string response to indicate reason for socket closing
     */
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            _socketChannel.send(SocketUpdate(exception = SocketAbortedException()))
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        _socketChannel.close()
    }

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network.
     * @param webSocket a non-blocking interface to a web socket create OkHttpClient instance from socket factory
     * @param t Throwable object to retrieve exception type
     * @param response nullable response as a result of socket failure
     */
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            _socketChannel.send(SocketUpdate(exception = t))
        }
    }

}

class SocketAbortedException : Exception()