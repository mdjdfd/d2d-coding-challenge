package com.d2d.challenge.data.interactor

import com.d2d.challenge.data.socket.SocketUpdate
import kotlinx.coroutines.channels.Channel

interface IServiceHelper {
    suspend fun getSocket(): Channel<SocketUpdate>
    fun disconnectSocket()
}