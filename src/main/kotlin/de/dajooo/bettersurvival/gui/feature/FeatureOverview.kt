package de.dajooo.bettersurvival.gui.feature

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.feature.FeatureRegistry
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.item.item
import de.dajooo.kaper.item.name
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeatureOverview: View(), KoinComponent {
    private val featureRegistry by inject<FeatureRegistry>()
    private val messages by inject<MessageConfig>()

    override fun onInit(config: ViewConfigBuilder) {
        config.title(!"<gold><bold>Features</bold></gold>")
        config.size(featureRegistry.count()/9+1)
        config.cancelOnClick()
    }

    override fun onClick(click: SlotClickContext) {
        featureRegistry.forEachIndexed { index, feature ->
            val statusName = if (feature.enabled) feature.displayName.color(NamedTextColor.GREEN) else feature.displayName.color(NamedTextColor.RED)
            click.item.name(statusName)
            click.item
        }
    }

    override fun onFirstRender(render: RenderContext) {
        featureRegistry.forEachIndexed { index, feature ->
            val statusName = if (feature.enabled) feature.displayName.color(NamedTextColor.GREEN) else feature.displayName.color(NamedTextColor.RED)
            render.slot(index,
                if (feature.enabled) {
                    item(Material.GREEN_WOOL) {
                        name(statusName)
                    }
                } else {
                    item(Material.RED_WOOL) {
                        name(statusName)
                    }
                }
            ).onClick { event ->
                if (!event.player.hasPermission("bettersurvival.feature.toggle.$feature")) return@onClick event.player.sendMessage(!messages.noPermission)
                if (feature.enabled) {
                    feature.disable()
                    event.item.name(feature.displayName.color(NamedTextColor.RED))
                    event.item.type = Material.RED_WOOL
                } else {
                    feature.enable()
                    event.item.name(feature.displayName.color(NamedTextColor.GREEN))
                    event.item.type = Material.GREEN_WOOL
                }
            }
        }
    }
}