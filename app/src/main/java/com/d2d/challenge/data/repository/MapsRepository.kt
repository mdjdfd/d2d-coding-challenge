package com.d2d.challenge.data.repository

import com.d2d.challenge.data.socket.ISocketController
import com.d2d.challenge.data.socket.SocketUpdate
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class MapsRepository @Inject constructor(private val iSocketController: ISocketController) {
    fun startSocket(): Channel<SocketUpdate> = iSocketController.startSocket()
    fun stopSocket() = iSocketController.stopSocket()
}