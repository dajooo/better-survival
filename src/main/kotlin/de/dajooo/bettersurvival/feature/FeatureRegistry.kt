package de.dajooo.bettersurvival.feature

import de.dajooo.bettersurvival.feature.features.CropRightClickFeature
import de.dajooo.bettersurvival.feature.features.HomesFeature
import de.dajooo.bettersurvival.feature.features.SaplingTwerkFeature
import de.dajooo.bettersurvival.feature.features.TimberFeature

class FeatureRegistry : MutableIterable<Feature<*>> {
    private val features: MutableList<Feature<*>> = mutableListOf(
        TimberFeature(),
        HomesFeature(),
        CropRightClickFeature(),
        SaplingTwerkFeature(),
    )

    fun init(): FeatureRegistry {
        features.forEach {
            if (it.enabled) {
                it.enable()
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