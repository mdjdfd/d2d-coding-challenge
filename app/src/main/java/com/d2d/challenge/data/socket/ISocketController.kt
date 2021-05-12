package com.d2d.challenge.data.socket

import kotlinx.coroutines.channels.Channel

/**
 * Interface which is responsible for start and stop the socket.
 */
interface ISocketController {
    suspend fun startSocket(): Channel<SocketUpdate>
    fun stopSocket()
}