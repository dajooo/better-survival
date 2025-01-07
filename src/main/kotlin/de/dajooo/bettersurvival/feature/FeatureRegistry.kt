package de.dajooo.bettersurvival.feature

import de.dajooo.bettersurvival.feature.features.*
import de.dajooo.kaper.extensions.timerTask
import org.bukkit.scheduler.BukkitTask

class FeatureRegistry : MutableIterable<Feature<*>> {
    private val features: MutableList<Feature<*>> = mutableListOf(
        TimberFeature(),
        HomesFeature(),
        CropRightClickFeature(),
        SaplingTwerkFeature(),
        CustomRecipesFeature(),
        BetterBedsFeature(),
        VeinMinerFeature(),
        GraveFeature()
    )

    lateinit var scheduler: BukkitTask
    lateinit var asyncScheduler: BukkitTask
    private var currentTick = 0

    fun init(): FeatureRegistry {
        features.forEach {
            if (it.enabled) {
                it.enable()
            }
        }
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
        return this
    }

    fun findFeature(name: String) = features.find { it.name.lowercase() == name.lowercase() }

    fun enable(name: String) = findFeature(name)?.apply { enable() }

    fun disable(name: String) = findFeature(name)?.apply { disable() }

    fun toggle(name: String): Feature<*>? {
        val feature = findFeature(name) ?: return null
        if (feature.enabled) {
            feature.disable()
        } else {
            feature.enable()
        }
        return feature
    }

    override fun iterator() = features.iterator()
}