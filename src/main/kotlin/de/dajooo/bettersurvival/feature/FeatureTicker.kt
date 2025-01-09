package de.dajooo.bettersurvival.feature

import de.dajooo.kaper.extensions.timerTask
import org.bukkit.scheduler.BukkitTask

class FeatureTicker(private val features: MutableList<Feature<*>>) {

    lateinit var scheduler: BukkitTask
    lateinit var asyncScheduler: BukkitTask
    private var currentTick = 0

    fun start() {
        scheduler = timerTask(0L, 1L) {
            features.forEach{ feature ->
                feature.tick(currentTick)
            }
            currentTick++
        }
        asyncScheduler = timerTask(0L, 1L, true) {
            features.forEach{ feature ->
                feature.tickAsync(currentTick)
            }
        }
    }

    fun stop() {
        scheduler.cancel()
        asyncScheduler.cancel()
    }
}