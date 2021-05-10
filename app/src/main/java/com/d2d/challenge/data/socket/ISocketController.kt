package com.d2d.challenge.data.socket

import kotlinx.coroutines.channels.Channel

interface ISocketController {
    fun startSocket(): Channel<SocketUpdate>
    fun stopSocket()
}