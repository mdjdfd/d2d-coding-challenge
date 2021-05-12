package com.d2d.challenge.data.socket

import com.d2d.challenge.common.Constant.NORMAL_CLOSURE_STATUS
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import javax.inject.Inject

/**
 * Class is responsible for implementing ISocketController interface and how the start and end socket will respond.
 * @param okHttpClient Inject OkHttpClient object using constructor injection
 * @param socketCallback Inject SocketCallback object using constructor injection
 * @param webSocket Inject WebSocket object using constructor injection
 */

class SocketControllerImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val socketCallback: SocketCallback,
    private val webSocket: WebSocket
) : ISocketController {


    /**
     * suspend fun to start the socket and update channel observable. This also initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * @return emits socket update event through channel
     */
    override suspend fun startSocket(): Channel<SocketUpdate> =
        with(socketCallback) {
            okHttpClient.dispatcher.executorService.shutdown()
            this@with._socketChannel
        }


    /**
     * Stop the socket as a result of closing remote connection.
     */
    override fun stopSocket() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, null)
        socketCallback._socketChannel?.close()
    }

}
