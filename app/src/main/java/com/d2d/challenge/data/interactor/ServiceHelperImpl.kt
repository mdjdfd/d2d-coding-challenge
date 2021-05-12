package com.d2d.challenge.data.interactor

import com.d2d.challenge.data.socket.ISocketController
import com.d2d.challenge.data.socket.SocketUpdate
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

/**
 * Implements helper interface to retrieve data from service layer.
 * @param iSocketController ISocketController injected via constructor injection
 */
class ServiceHelperImpl @Inject constructor(private val iSocketController: ISocketController) :
    IServiceHelper {
    override suspend fun getSocket(): Channel<SocketUpdate> = iSocketController.startSocket()
    override fun disconnectSocket() = iSocketController.stopSocket()

}