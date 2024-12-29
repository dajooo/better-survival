package de.dajooo.bettersurvival.feature

import de.dajooo.kaper.extensions.pluginManager
import de.dajooo.kommons.TypedConfiguration
import de.dajooo.kommons.TypedYamlConfiguration
import de.dajooo.kommons.loadYamlConfig
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.reflect.full.createType

abstract class AbstractFeature<C : FeatureConfig>: Feature<C>, KoinComponent, Listener {
    protected val _plugin by inject<JavaPlugin>()

    override val enabled get() = config.enabled
    override val config: C get() = typedConfig.get()
    override fun enable() {
        this.config.enabled = true
        this.typedConfig.set(this.config)
        this.typedConfig.save()
        pluginManager.registerEvents(this, _plugin)
        onEnable()
    }

    protected inline fun <reified T: FeatureConfig> config(value: T) = loadYamlConfig(_plugin.dataPath.resolve("features", this.name+".yaml"), value)

    override fun disable() {
        this.config.enabled = false
        this.typedConfig.set(this.config)
        this.typedConfig.save()
        HandlerList.unregisterAll(this)
        onDisable()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun onEnable() {}

    @Suppress("MemberVisibilityCanBePrivate")
    fun onDisable() {}
}