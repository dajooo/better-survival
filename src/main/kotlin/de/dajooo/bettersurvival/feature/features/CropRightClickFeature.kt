package de.dajooo.bettersurvival.feature.features

import com.destroystokyo.paper.MaterialSetTag
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.kaper.extensions.isTagged
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.tagKeyFor
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class CropRightClickFeature: AbstractFeature<CropRightClickFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var allowedCropSeeds: List<Material> = listOf(
            Material.BEETROOT_SEEDS,
            Material.WHEAT_SEEDS,
            Material.POTATO,
            Material.CARROT,
            Material.NETHER_WART
        ),
    ): FeatureConfig

    override val name = "crop-right-click"
    override val displayName = !"<gold>Crop Right Click</gold>"
    override val description = !"<gray>Farm and automatically replant a crop by right-clicking it.</gray>"
    override val typedConfig = config(Config())

    private val allowedSeeds = MaterialSetTag(tagKeyFor("crop_right_click_seeds")).add(config.allowedCropSeeds)

    @EventHandler
    fun handleRightClick(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return
        if (!MaterialSetTag.CROPS.isTagged(block)) return
        val crop = block.blockData as? Ageable ?: return
        if (crop.age < crop.maximumAge) return
        if (block.drops.none { allowedSeeds.isTagged(it) }) return
        crop.age = 0
        val drops = block.drops
        drops.forEach { itemStack ->
            if (allowedSeeds.isTagged(itemStack)) itemStack.amount -= 1
            val leftOver = event.player.inventory.addItem(itemStack)
            if (leftOver.isNotEmpty()) leftOver.values.forEach {
                event.player.world.dropItemNaturally(block.location, it)
            }
        }

        val loc = block.location
        event.player.spawnParticle(
            Particle.BLOCK,
            loc.clone().add(0.5, 0.25, 0.5),
            30,
            0.4, 0.4, 0.4,
            0.0,
            block.blockData
        )

        event.player.playSound(
            Sound.sound(
                Key.key("block.crop.break"),
                Sound.Source.BLOCK,
                1.0f,
                1.0f
            )
        )

        block.blockData = crop
        event.isCancelled = true
    }


}