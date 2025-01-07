package de.dajooo.bettersurvival.util

import java.nio.ByteBuffer
import java.util.UUID

fun UUID.toByteArray(): ByteArray = ByteBuffer.wrap(ByteArray(16)).apply {
    putLong(this@toByteArray.mostSignificantBits)
    putLong(this@toByteArray.leastSignificantBits)
}.array()

fun uuidFromBytes(bytes: ByteArray) = ByteBuffer.wrap(bytes).let { buf ->
    UUID(buf.getLong(), buf.getLong())
}