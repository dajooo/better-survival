package de.dajooo.bettersurvival.feature.features

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.util.TeleportConfigurable
import de.dajooo.kaper.extensions.not
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.koin.core.component.inject

class MoreGrowFeature : AbstractFeature<MoreGrowFeature.Config>() {
    private val messages by inject<MessageConfig>()

    @Serializable
    data class Config(
        override var enabled: Boolean = true,
    ) : FeatureConfig

    override val name = "more-grow"
    override val displayName = !"<gold>More Grow</gold>"
    override val description = !"<gray>Be able to make everything grow with bone meal.</gray>"
    override val typedConfig = config(Config())

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.player.inventory.itemInMainHand.type != Material.BONE_MEAL) return
        if (event.player.inventory.itemInMainHand.amount < 1) return
        if (event.clickedBlock?.type != Material.SUGAR_CANE && growSugarCane(event.clickedBlock)) {
            event.player.inventory.itemInMainHand.amount -= 1
            event.isCancelled = true
            return
        }
    }

    private fun growSugarCane(block: Block?) : Boolean {
        if (block == null) return false
        var lowestSugarCane = block!!
        var highestSugarCane = block!!
        while (true) {
            val nextSugarCane = lowestSugarCane.getRelative(BlockFace.DOWN)
            if (nextSugarCane.type != Material.SUGAR_CANE) break
            lowestSugarCane = nextSugarCane
        }
        while (true) {
            val nextSugarCane = highestSugarCane.getRelative(BlockFace.UP)
            if (nextSugarCane.type != Material.SUGAR_CANE) break
            highestSugarCane = nextSugarCane
        }
        val sugarCaneHeight = highestSugarCane.y - lowestSugarCane.y + 1
        if (sugarCaneHeight >= 3) return false
        val newSugarCane = highestSugarCane.getRelative(BlockFace.UP)
        newSugarCane.type = Material.SUGAR_CANE
        block.apply {
            world.spawnParticle(Particle.HAPPY_VILLAGER, location.toCenterLocation(), 10, 0.5, 0.5, 0.5)
        }
        return true
    }
}
