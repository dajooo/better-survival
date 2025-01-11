package de.dajooo.bettersurvival.feature

import de.dajooo.bettersurvival.feature.features.*

class FeatureRegistry : MutableIterable<Feature<*>> {
    private val features: MutableList<Feature<*>> = mutableListOf(
        TimberFeature(),
        HomesFeature(),
        CropRightClickFeature(),
        SaplingTwerkFeature(),
        CustomRecipesFeature(),
        BetterBedsFeature(),
        VeinMinerFeature(),
        GraveFeature(),
        WarpsFeature(),
        QuickAccessCommandsFeature(),
    )

    private val featureTicker = FeatureTicker(features)

    fun init(): FeatureRegistry {
        features.forEach {
            if (it.enabled) {
                it.enable()
            }
        }
        featureTicker.start()
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