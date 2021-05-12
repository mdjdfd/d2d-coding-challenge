package com.d2d.challenge.data.socket

import com.d2d.challenge.data.entity.Payload
import okio.ByteString

/**
 * Setter and getter for all types of socket event.
 */
data class SocketUpdate(
    var payload: Payload? = null,
    val byteString: ByteString? = null,
    val exception: Throwable? = null
)