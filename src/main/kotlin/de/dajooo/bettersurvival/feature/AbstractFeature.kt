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
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.command.ExecutableCommand
import kotlin.reflect.full.createType

abstract class AbstractFeature<C : FeatureConfig>: Feature<C>, KoinComponent, Listener {
    protected val _plugin by inject<JavaPlugin>()
    private val lamp by inject<Lamp<BukkitCommandActor>>()

    override val enabled get() = config.enabled
    override val config: C get() = typedConfig.get()

    open val commands: Array<Any> = arrayOf()
    private val lampCommands = mutableListOf<ExecutableCommand<BukkitCommandActor>>()

    protected inline fun <reified T: FeatureConfig> config(value: T) = loadYamlConfig(_plugin.dataPath.resolve("features", this.name+".yaml"), value)

    override fun enable() {
        this.config.enabled = true
        this.typedConfig.set(this.config)
        this.typedConfig.save()
        pluginManager.registerEvents(this, _plugin)
        lampCommands.addAll(lamp.register(*commands))
        onEnable()
    }


    override fun disable() {
        this.config.enabled = false
        this.typedConfig.set(this.config)
        this.typedConfig.save()
        HandlerList.unregisterAll(this)
        lampCommands.forEach(lamp::unregister)
        lampCommands.clear()
        onDisable()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open fun onEnable() {}

    @Suppress("MemberVisibilityCanBePrivate")
    open fun onDisable() {}
}