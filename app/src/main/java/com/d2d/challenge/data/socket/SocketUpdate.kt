package com.d2d.challenge.data.socket

import com.d2d.challenge.data.entity.Payload
import okio.ByteString

data class SocketUpdate(
    var payload: Payload? = null,
    val byteString: ByteString? = null,
    val exception: Throwable? = null
)