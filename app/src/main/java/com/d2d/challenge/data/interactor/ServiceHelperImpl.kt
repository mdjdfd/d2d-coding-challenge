package com.d2d.challenge.data.interactor

import com.d2d.challenge.data.socket.ISocketController
import com.d2d.challenge.data.socket.SocketUpdate
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class ServiceHelperImpl @Inject constructor(private val iSocketController: ISocketController) :
    IServiceHelper {
    override suspend fun getSocket(): Channel<SocketUpdate> = iSocketController.startSocket()
    override fun disconnectSocket() = iSocketController.stopSocket()

}