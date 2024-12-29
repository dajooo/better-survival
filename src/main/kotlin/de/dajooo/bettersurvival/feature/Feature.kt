package de.dajooo.bettersurvival.feature

import de.dajooo.kommons.TypedConfiguration
import net.kyori.adventure.text.Component

interface Feature<C: FeatureConfig> {
    val name: String
    val displayName: Component
    val description: Component
    fun enable()
    fun disable()
    val enabled: Boolean
    val typedConfig : TypedConfiguration<C>
    val config: C
}