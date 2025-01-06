package de.dajooo.bettersurvival.feature.features

import com.destroystokyo.paper.MaterialSetTag
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.kaper.extensions.*
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice

class CustomRecipesFeature : AbstractFeature<CustomRecipesFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var slabs: Boolean = true,
        var horseArmor: Boolean = true,
        var logsToChest: Boolean = true,
        var logsToSticks: Boolean = true,
        var rottenFleshToLeather: Boolean = true,
        var magmaBlockToMagmaCream: Boolean = true,
        var nameTag: Boolean = true,
    ) : FeatureConfig

    override val name = "custom-recipes"
    override val displayName = !"<gold>Custom Recipes</gold>"
    override val description = !"<gray>Add custom recipes to the game.</gray>"
    override val typedConfig = config(Config())

    private val allRecipes: List<Recipe> = Recipes.slabRecipes +
            Recipes.horseArmorRecipes +
            Recipes.logsToChestRecipe +
            Recipes.logsToSticksRecipe +
            Recipes.rottenFleshToLeatherRecipes +
            Recipes.magmaBlockToMagmaCreamRecipe +
            Recipes.nameTagRecipe

    override fun onEnable() {
        if (config.slabs) {
            Recipes.slabRecipes.forEach(Bukkit::addRecipe)
        }
        if (config.horseArmor) {
            Recipes.horseArmorRecipes.forEach(Bukkit::addRecipe)
        }
        if (config.logsToChest) {
            Bukkit.addRecipe(Recipes.logsToChestRecipe)
        }
        if (config.logsToSticks) {
            Bukkit.addRecipe(Recipes.logsToSticksRecipe)
        }
        if (config.rottenFleshToLeather) {
            Recipes.rottenFleshToLeatherRecipes.forEach(Bukkit::addRecipe)
        }
        if (config.magmaBlockToMagmaCream) {
            Bukkit.addRecipe(Recipes.magmaBlockToMagmaCreamRecipe)
        }
        if (config.nameTag) {
            Bukkit.addRecipe(Recipes.nameTagRecipe)
        }
        onlinePlayers.forEach { player ->
            allRecipes.filterIsInstance<Keyed>().forEach { player.discoverRecipe(it.key) }
        }
    }

    override fun onDisable() {
        allRecipes.forEach { if (it is Keyed) Bukkit.removeRecipe(it.key) }
    }

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        allRecipes.filterIsInstance<Keyed>().forEach { event.player.discoverRecipe(it.key) }
    }

    object Recipes {
        private fun slabsToFullBlock(blockType: Material, slabType: Material) =
            shapedRecipe("${slabType.name.lowercase()}_to_block", blockType) {
                shape("S", "S")
                setIngredient('S', slabType)
            }

        val slabRecipes = listOf(
            slabsToFullBlock(Material.CUT_COPPER, Material.CUT_COPPER_SLAB),
            slabsToFullBlock(Material.EXPOSED_CUT_COPPER, Material.EXPOSED_CUT_COPPER_SLAB),
            slabsToFullBlock(Material.WEATHERED_CUT_COPPER, Material.WEATHERED_CUT_COPPER_SLAB),
            slabsToFullBlock(Material.OXIDIZED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER_SLAB),
            slabsToFullBlock(Material.WAXED_CUT_COPPER, Material.WAXED_CUT_COPPER_SLAB),
            slabsToFullBlock(Material.WAXED_EXPOSED_CUT_COPPER, Material.WAXED_EXPOSED_CUT_COPPER_SLAB),
            slabsToFullBlock(Material.WAXED_WEATHERED_CUT_COPPER, Material.WAXED_WEATHERED_CUT_COPPER_SLAB),
            slabsToFullBlock(Material.WAXED_OXIDIZED_CUT_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER_SLAB),
            slabsToFullBlock(Material.OAK_PLANKS, Material.OAK_SLAB),
            slabsToFullBlock(Material.SPRUCE_PLANKS, Material.SPRUCE_SLAB),
            slabsToFullBlock(Material.BIRCH_PLANKS, Material.BIRCH_SLAB),
            slabsToFullBlock(Material.JUNGLE_PLANKS, Material.JUNGLE_SLAB),
            slabsToFullBlock(Material.ACACIA_PLANKS, Material.ACACIA_SLAB),
            slabsToFullBlock(Material.DARK_OAK_PLANKS, Material.DARK_OAK_SLAB),
            slabsToFullBlock(Material.MANGROVE_PLANKS, Material.MANGROVE_SLAB),
            slabsToFullBlock(Material.CRIMSON_PLANKS, Material.CRIMSON_SLAB),
            slabsToFullBlock(Material.WARPED_PLANKS, Material.WARPED_SLAB),
            slabsToFullBlock(Material.STONE, Material.STONE_SLAB),
            slabsToFullBlock(Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB),
            slabsToFullBlock(Material.SANDSTONE, Material.SANDSTONE_SLAB),
            slabsToFullBlock(Material.CUT_SANDSTONE, Material.CUT_SANDSTONE_SLAB),
            slabsToFullBlock(Material.COBBLESTONE, Material.COBBLESTONE_SLAB),
            slabsToFullBlock(Material.BRICKS, Material.BRICK_SLAB),
            slabsToFullBlock(Material.STONE_BRICKS, Material.STONE_BRICK_SLAB),
            slabsToFullBlock(Material.MUD_BRICKS, Material.MUD_BRICK_SLAB),
            slabsToFullBlock(Material.NETHER_BRICKS, Material.NETHER_BRICK_SLAB),
            slabsToFullBlock(Material.QUARTZ_BLOCK, Material.QUARTZ_SLAB),
            slabsToFullBlock(Material.RED_SANDSTONE, Material.RED_SANDSTONE_SLAB),
            slabsToFullBlock(Material.CUT_RED_SANDSTONE, Material.CUT_RED_SANDSTONE_SLAB),
            slabsToFullBlock(Material.PURPUR_BLOCK, Material.PURPUR_SLAB),
            slabsToFullBlock(Material.PRISMARINE, Material.PRISMARINE_SLAB),
            slabsToFullBlock(Material.PRISMARINE_BRICKS, Material.PRISMARINE_BRICK_SLAB),
            slabsToFullBlock(Material.DARK_PRISMARINE, Material.DARK_PRISMARINE_SLAB),
            slabsToFullBlock(Material.POLISHED_GRANITE, Material.POLISHED_GRANITE_SLAB),
            slabsToFullBlock(Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_RED_SANDSTONE_SLAB),
            slabsToFullBlock(Material.MOSSY_STONE_BRICKS, Material.MOSSY_STONE_BRICK_SLAB),
            slabsToFullBlock(Material.POLISHED_DIORITE, Material.POLISHED_DIORITE_SLAB),
            slabsToFullBlock(Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_SLAB),
            slabsToFullBlock(Material.END_STONE_BRICKS, Material.END_STONE_BRICK_SLAB),
            slabsToFullBlock(Material.SMOOTH_SANDSTONE, Material.SMOOTH_SANDSTONE_SLAB),
            slabsToFullBlock(Material.SMOOTH_QUARTZ, Material.SMOOTH_QUARTZ_SLAB),
            slabsToFullBlock(Material.GRANITE, Material.GRANITE_SLAB),
            slabsToFullBlock(Material.ANDESITE, Material.ANDESITE_SLAB),
            slabsToFullBlock(Material.RED_NETHER_BRICKS, Material.RED_NETHER_BRICK_SLAB),
            slabsToFullBlock(Material.POLISHED_ANDESITE, Material.POLISHED_ANDESITE_SLAB),
            slabsToFullBlock(Material.DIORITE, Material.DIORITE_SLAB),
            slabsToFullBlock(Material.COBBLED_DEEPSLATE, Material.COBBLED_DEEPSLATE_SLAB),
            slabsToFullBlock(Material.POLISHED_DEEPSLATE, Material.POLISHED_DEEPSLATE_SLAB),
            slabsToFullBlock(Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_BRICK_SLAB),
            slabsToFullBlock(Material.DEEPSLATE_TILES, Material.DEEPSLATE_TILE_SLAB),
            slabsToFullBlock(Material.BLACKSTONE, Material.BLACKSTONE_SLAB),
            slabsToFullBlock(Material.POLISHED_BLACKSTONE, Material.POLISHED_BLACKSTONE_SLAB),
            slabsToFullBlock(Material.POLISHED_BLACKSTONE_BRICKS, Material.POLISHED_BLACKSTONE_BRICK_SLAB),
        )

        val horseArmorRecipes = listOf(
            shapedRecipe("golden_horse_armor", Material.GOLDEN_HORSE_ARMOR) {
                shape("  G", "GWG", "GGG")
                setIngredient('G', Material.GOLD_INGOT)
                setIngredient('W', MaterialSetTag.WOOL)
            },
            shapedRecipe("iron_horse_armor", Material.IRON_HORSE_ARMOR) {
                shape("  I", "IWI", "III")
                setIngredient('I', Material.IRON_INGOT)
                setIngredient('W', MaterialSetTag.WOOL)
            },
            shapedRecipe("diamond_horse_armor", Material.DIAMOND_HORSE_ARMOR) {
                shape("  D", "DWD", "DDD")
                setIngredient('D', Material.DIAMOND)
                setIngredient('W', MaterialSetTag.WOOL)
            }
        )

        val rottenFleshToLeatherRecipes = listOf(
            furnaceRecipe("leather", Material.LEATHER, Material.ROTTEN_FLESH, cookingTime = 400),
            smokerRecipe("leather_smoker", Material.LEATHER, Material.ROTTEN_FLESH, cookingTime = 400)
        )

        val nameTagRecipe = shapedRecipe("name_tag", Material.NAME_TAG) {
            shape(" SI", " L ", " L ")
            setIngredient('I', Material.IRON_INGOT)
            setIngredient('S', Material.STRING)
            setIngredient('L', Material.LEATHER)
        }

        val magmaBlockToMagmaCreamRecipe =
            shapelessRecipe("magma_block_to_cream", ItemStack(Material.MAGMA_CREAM, 4), Material.MAGMA_BLOCK)

        val logsToSticksRecipe = shapedRecipe("logs_to_sticks", ItemStack(Material.STICK, 8)) {
            shape("L", "L")
            setIngredient('L', MaterialSetTag.LOGS)
        }

        val logsToChestRecipe = shapedRecipe("logs_to_chests", ItemStack(Material.CHEST, 4)) {
            shape("LLL", "L L", "LLL")
            setIngredient('L', RecipeChoice.MaterialChoice(MaterialSetTag.LOGS))
        }
    }

}