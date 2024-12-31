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
    val homeSet: String = "<prefix><green>Home <yellow><home></yellow> has been set!</green>",
    val homeRemoved: String = "<prefix><green>Home <yellow><home></yellow> has been removed!</green>",
    val homeTeleport: String = "<prefix>Teleporting to home <yellow><home></yellow>...",
    val homeNotFound: String = "<prefix><red>Home <yellow><home></yellow> has not been found!</red>",
    val warpSet: String = "<prefix><green>Warp <yellow><warp></yellow> has been set!</green>",
    val warpRemoved: String = "<prefix><green>Warp <yellow><warp></yellow> has been removed!</green>",
    val warpTeleport: String = "<prefix>Teleporting to warp <yellow><warp></yellow>...",
    val joinMessage: String = "<dark_gray>[<green>+</green>]</dark_gray> <gold><name>",
    val leaveMessage: String = "<dark_gray>[<green>+</green>]</dark_gray> <gold><name>",
    val chatFormat: String = "<display_name><dark_gray>:</dark_gray> <gray><message></gray>",
    val motd: String = "<prefix>Minecraft Server<reset>",
)

fun loadMessageConfig(basePath: Path) = loadYamlConfig(basePath.resolve("messages.yaml"), MessageConfig())