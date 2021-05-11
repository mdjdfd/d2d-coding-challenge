package com.d2d.challenge.data.socket

import com.d2d.challenge.common.Constant.NORMAL_CLOSURE_STATUS
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import javax.inject.Inject

class SocketControllerImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val socketCallback: SocketCallback,
    private val webSocket: WebSocket
) : ISocketController {

    override suspend fun startSocket(): Channel<SocketUpdate> =
        with(socketCallback) {
            okHttpClient.dispatcher.executorService.shutdown()
            this@with._socketChannel
        }


    override fun stopSocket() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, null)
        socketCallback._socketChannel?.close()
    }

}
