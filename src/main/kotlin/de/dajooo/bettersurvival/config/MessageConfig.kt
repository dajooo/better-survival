package de.dajooo.bettersurvival.config

import de.dajooo.kommons.loadYamlConfig
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class MessageConfig(
    val prefix: String = "<gradient:dark_green:green><bold>Survival</bold></gradient> <dark_gray>|</dark_gray> <gray>",
    val noPermission: String = "<prefix><red>You don't have permission to do that!",
    val featureNotFound: String = "<prefix><red><yellow><feature></yellow> has not been found!</red>",
    val featureEnabled: String = "<prefix><feature> <gray>has been <green>enabled</green>!</gray>",
    val featureDisabled: String = "<prefix><feature> <gray>has been <red>disabled</red>!</gray>",
    val playersOnlyCommand: String = "Only players can execute this command.",
    val listFeaturesHeader: String = "<gray>--- <white>Features</white> <gray>---",
    val listFeaturesEntry: String = "<gray>-</gray> <status_name> <gray>-</gray> <white><description></white>",
    val listFeaturesFooter: String = "<gray>--- <white>Features</white> <gray>---",
    val reloadConfig: String = "<prefix><green>Config has been reloaded!</green>",
    val homeSet: String = "<prefix><green>Home <yellow><name></yellow> has been set!</green>",
    val homeRemoved: String = "<prefix><green>Home <yellow><name></yellow> has been removed!</green>",
    val homeTeleport: String = "<prefix>Teleporting to home <yellow><name></yellow>...",
    val homeNotFound: String = "<prefix><red>Home <yellow><name></yellow> has not been found!</red>",
    val warpSet: String = "<prefix><green>Warp <yellow><name></yellow> has been set!</green>",
    val warpRemoved: String = "<prefix><green>Warp <yellow><name></yellow> has been removed!</green>",
    val warpTeleport: String = "<prefix>Teleporting to warp <yellow><name></yellow>...",
    val warpListHeader: String = "<gray>--- <white>Warps</white> <gray>---",
    val warpListEntry: String = "<gray>-</gray> <white><name></white> <gray>(<x>, <y>, <z>) <white><world></white>",
    val warpListFooter: String = "<gray>--- <white>Warps</white> <gray>---",
    val warpNotFound: String = "<prefix><red>Warp <yellow><name></yellow> has not been found!</red>",
    val joinMessage: String = "<dark_gray>[<green>+</green>]</dark_gray> <name>",
    val leaveMessage: String = "<dark_gray>[<green>+</green>]</dark_gray> <name>",
    val chatFormat: String = "<display_name><dark_gray>:</dark_gray> <gray><message></gray>",
    val motd: String = "<gradient:dark_green:green><bold>Better Survival</bold></gradient><gray> Minecraft Server<reset>",
    val homeListHeader: String = "<gray>--- <white>Homes</white> <gray>---",
    val homeListEntry: String = "<gray>-</gray> <white><name></white> <gray>(<x>, <y>, <z>) <white><world></white>",
    val homeListFooter: String = "<gray>--- <white>Homes</white> <gray>---",
    val playersSleeping: String = "<prefix><gray><sleeping>/<max_sleeping> [<percentage>%] players are sleeping in <world>.",
    val morning: String = "<prefix>Good morning!",
    val backTeleport: String = "<prefix>Teleporting to your last location...",
    val displayNameFormat: String = "<player_prefix><player_color><player_name></player_color><player_suffix>",
    val playerListNameFormat: String = "<player_prefix><player_color><player_name></player_color><player_suffix>",
    val backDisabled: String = "<prefix><red>Teleporting to your last location is disabled.",
)

fun loadMessageConfig(basePath: Path) = loadYamlConfig(basePath.resolve("messages.yaml"), MessageConfig())