package vg.h2o.dimibug

import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.monun.kommand.wrapper.Position2D
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import vg.h2o.dimibug.handlers.CompassHandler
import vg.h2o.dimibug.handlers.ItemDespawnTimerHandler
import vg.h2o.dimibug.handlers.PlayerNameHandler
import vg.h2o.dimibug.listeners.PlayerListener

class DimiBug : JavaPlugin() {

    companion object {
        lateinit var instance: DimiBug

        @JvmStatic
        fun findPlayer(name: String?): List<ServerPlayer>? {
            name ?: return null

            val originalName = PlayerNameHandler.getReverseName(name) ?: return null

            val player = Bukkit.getPlayer(originalName) ?: return null

            return listOf((player as CraftPlayer).handle)
        }
    }

    init {
        instance = this

        val method = javaClass.getDeclaredMethod("findPlayer", String::class.java)

        EntitySelector::class.java.getDeclaredField("dimibug").apply {
            set(null, method)
        }
    }

    override fun onEnable() {
        logger.info("DimiBug is running")

        Bukkit.getPluginManager().registerEvents(PlayerListener, this)

        kommand {
            "removeitemdespawntimer"("ridt") {

                requires { isPlayer }

                executes {
                    val sender = sender
                    val (message, color) = if (ItemDespawnTimerHandler.getTimer(player).end()) {
                        "아이템 디스폰 타이머가 제거되었습니다." to NamedTextColor.GREEN
                    } else {
                        "아이템 디스폰 타이머가 존재하지 않습니다." to NamedTextColor.RED
                    }

                    sender.sendMessage(text(message, color))
                }
            }

            "rename" {
                requires { isOp }

                then("player" to player(), "name" to string(StringType.GREEDY_PHRASE)) {

                    executes {
                        val player: Player by it
                        val name: String by it

                        sender.sendMessage(
                            empty().append(text(player.name, NamedTextColor.GOLD))
                                .append(text("의 이름을 "))
                                .append(text(name, NamedTextColor.GREEN))
                                .append(text("(으)로 바꿨습니다"))
                        )

                        player.sendMessage(
                            text("당신의 이름은 이제 ")
                                .append(text(name, NamedTextColor.GREEN))
                                .append(text("입니다"))
                        )

                        PlayerNameHandler.rename(player, name)
                    }
                }
            }

            "compass" {

                requires { isPlayer }

                "delete" {
                    executes {
                        CompassHandler.delete(player)
                    }
                }

                "player"("player" to player()) {
                    executes {
                        val player: Player by it

                        CompassHandler.create(this.player, player)
                    }
                }

                "position"("position" to position2D()) {
                    executes {
                        val position: Position2D by it

                        CompassHandler.create(player, position)
                    }
                }
            }
        }

        server.scheduler.runTaskTimer(this, Runnable {
            ItemDespawnTimerHandler.tick()
        }, 0, 1)
    }

    override fun onDisable() {
        logger.info("DimiBug is stopped")
    }
}