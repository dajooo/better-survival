package de.dajooo.bettersurvival.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class ExpiringBuffer<T>(
    private val expirationMs: Duration = 10.minutes,
    private val maxSize: Int = 1000
) {
    private val cache = LinkedHashMap<T, Long>()

    fun add(key: T) {
        if (cache.size >= maxSize) {
            cache.entries.firstOrNull()?.let { cache.remove(it.key) }
        }
        cache[key] = System.currentTimeMillis() + expirationMs.inWholeMilliseconds
    }

    fun contains(key: T): Boolean {
        val expireTime = cache[key] ?: return false
        return if (System.currentTimeMillis() > expireTime) {
            cache.remove(key)
            false
        } else true
    }

    fun remove(key: T) {
        cache.remove(key)
    }
}

fun <T> expiringBuffer(expirationMs: Duration = 10.minutes, maxSize: Int = 1000) = ExpiringBuffer<T>(expirationMs, maxSize)