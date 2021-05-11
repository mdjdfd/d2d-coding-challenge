package com.d2d.challenge.data.socket

import kotlinx.coroutines.channels.Channel

interface ISocketController {
    suspend fun startSocket(): Channel<SocketUpdate>
    fun stopSocket()
}