package de.dajooo.bettersurvival.gui.feature

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.feature.FeatureRegistry
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.withoutItalic
import de.dajooo.kaper.item.item
import de.dajooo.kaper.item.meta
import de.dajooo.kaper.item.name
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.RenderContext
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeatureOverview : View(), KoinComponent {
    private val featureRegistry by inject<FeatureRegistry>()
    private val messages by inject<MessageConfig>()

    override fun onInit(config: ViewConfigBuilder) {
        config.title(!"<gold><bold>Features</bold></gold>")
        config.size(featureRegistry.count() / 9 + 1)
        config.cancelOnClick()
    }

    override fun onFirstRender(render: RenderContext) {
        featureRegistry.forEachIndexed { index, feature ->
            render.slot(
                index,
                if (feature.enabled) {
                    item(Material.GREEN_WOOL) {
                        name(feature.displayName.withoutItalic().color(NamedTextColor.GREEN))
                        meta {
                            lore(listOf(feature.description.withoutItalic().color(NamedTextColor.GRAY)))
                        }
                    }
                } else {
                    item(Material.RED_WOOL) {
                        name(feature.displayName.withoutItalic().color(NamedTextColor.RED))
                        meta {
                            lore(listOf(feature.description.withoutItalic().color(NamedTextColor.GRAY)))
                        }
                    }
                }
            ).onClick { context ->
                if (!context.player.hasPermission("bettersurvival.feature.toggle.$feature")) return@onClick context.player.sendMessage(
                    !messages.noPermission
                )
                if (feature.enabled) {
                    feature.disable()
                    context.clickOrigin.currentItem = context.item.withType(Material.RED_WOOL).apply {
                        name(feature.displayName.withoutItalic().color(NamedTextColor.RED))
                        meta {
                            lore(listOf(feature.description.withoutItalic().color(NamedTextColor.GRAY)))
                        }
                    }
                } else {
                    feature.enable()
                    context.clickOrigin.currentItem = context.item.withType(Material.GREEN_WOOL).apply {
                        name(feature.displayName.withoutItalic().color(NamedTextColor.GREEN))
                        meta {
                            lore(listOf(feature.description.withoutItalic().color(NamedTextColor.GRAY)))
                        }
                    }
                }
            }
        }
    }
}