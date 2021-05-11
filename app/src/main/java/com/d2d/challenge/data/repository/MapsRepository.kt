package com.d2d.challenge.data.repository

import com.d2d.challenge.data.interactor.IServiceHelper
import com.d2d.challenge.data.socket.SocketUpdate
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject


open class MapsRepository @Inject constructor(private val iServiceHelper: IServiceHelper) {
    suspend fun getSocket(): Channel<SocketUpdate> = iServiceHelper.getSocket()
    fun disconnectSocket() = iServiceHelper.disconnectSocket()
}