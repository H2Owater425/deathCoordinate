package vg.h2o.dimibug.handlers

import com.mojang.authlib.GameProfile
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import vg.h2o.dimibug.DimiBug
import java.io.File

object PlayerNameHandler {

    private val plugin = DimiBug.instance

    private val configFile = File(plugin.dataFolder, "names.yml")
    private val config = YamlConfiguration.loadConfiguration(configFile)


    private val nameField by lazy {
        GameProfile::class.java.getDeclaredField("name").apply {
            isAccessible = true
        }
    }

    private fun saveName(player: Player, name: String) {

        val uuid = player.uniqueId

        config.set("$uuid.name", name)
        config.set("$uuid.origin", config.get("$uuid.origin") ?: player.name)

        config.save(configFile)
    }

    private fun getName(player: Player): String? {
        return config.getString("${player.uniqueId}.name")
    }

    fun getReverseName(name: String): String? {
        config.getKeys(false).forEach {
            if (config.getString("$it.name") == name) {
                return config.getString("$it.origin")
            }
        }

        return null
    }

    fun restore(player: Player) {
        val name = getName(player) ?: return

        rename(player, name)
    }

    fun rename(player: Player, name: String) {
        saveName(player, name)

        val gameProfile = (player as CraftPlayer).profile
        nameField.set(gameProfile, name)

        player.displayName(Component.text(name))

        Bukkit.getOnlinePlayers().forEach {
            it.hidePlayer(plugin, player)
            it.showPlayer(plugin, player)
        }
    }

}