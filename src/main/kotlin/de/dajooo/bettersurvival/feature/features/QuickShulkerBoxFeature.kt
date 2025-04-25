package de.dajooo.bettersurvival.feature.features

import com.destroystokyo.paper.MaterialSetTag
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.feature.FeatureMeta
import de.dajooo.bettersurvival.feature.features.QuickAccessCommandsFeature.Config
import de.dajooo.kaper.extensions.not
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

class QuickShulkerBoxFeature : AbstractFeature<QuickShulkerBoxFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
    ) : FeatureConfig

    override val meta = FeatureMeta(
        "quick-shulker-box",
        !"<gold>Quick Shulker Box</gold>",
        !"<gray>Quickly access your shulker box by <green>shift</green> + <green>right-clicking</green>.</gray>",
    )
    override val typedConfig = config(Config())

    private val shulkerBoxes = mutableMapOf<Player, ShulkerBoxData>()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR) return
        if (!event.player.isSneaking) return
        openShulkerBox(event, event.player, event.item)
    }

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        if (event.click != ClickType.RIGHT) return
        openShulkerBox(event, event.whoClicked as Player, event.currentItem)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (!shulkerBoxes.containsKey(event.player)) return
        val (shulkerBox, meta, itemStack) = shulkerBoxes[event.player]!!
        shulkerBox.inventory.contents = event.inventory.contents
        meta.blockState = shulkerBox
        itemStack.itemMeta = meta
        shulkerBoxes.remove(event.player)
    }

    private fun openShulkerBox(event: Cancellable, player: Player, item: ItemStack?): Boolean {
        if (item == null) return false
        if (!MaterialSetTag.SHULKER_BOXES.isTagged(item.type)) return false
        event.isCancelled = true
        val blockStateMeta = item.itemMeta as? BlockStateMeta ?: return false
        val shulkerBox = blockStateMeta.blockState as? ShulkerBox ?: return false
        player.openInventory(shulkerBox.inventory)
        shulkerBoxes[player] = ShulkerBoxData(shulkerBox, blockStateMeta, item)
        return true
    }

    data class ShulkerBoxData(val shulkerBox: ShulkerBox, val meta: BlockStateMeta, val itemStack: ItemStack)
}