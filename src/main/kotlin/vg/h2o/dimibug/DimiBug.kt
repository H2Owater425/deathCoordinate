package vg.h2o.dimibug

import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import vg.h2o.dimibug.handlers.ItemDespawnTimerHandler
import vg.h2o.dimibug.handlers.PlayerNameHandler
import vg.h2o.dimibug.listeners.PlayerListener

class DimiBug : JavaPlugin() {

    companion object {
        lateinit var instance: DimiBug
    }

    init {
        instance = this
    }

    override fun onEnable() {
        logger.info("DimiBug is running")

        Bukkit.getPluginManager().registerEvents(PlayerListener, this)

        kommand {
            "removeItemDespawnTimer"("ridt") {

                requires { isPlayer }

                executes {
                    val sender = sender
                    val (message, color) = if (ItemDespawnTimerHandler.getTimer(player).end()) {
                        "아이템 디스폰 타이머가 제거되었습니다." to NamedTextColor.GREEN
                    } else {
                        "아이템 디스폰 타이머가 존재하지 않습니다." to NamedTextColor.RED
                    }

                    sender.sendMessage(Component.text(message, color))
                }
            }

            "rename" {
                requires { isOp }

                then("player" to player(), "name" to string(StringType.GREEDY_PHRASE)) {

                    executes {
                        val player: Player by it
                        val name: String by it

                        PlayerNameHandler.rename(player, name)

                        player.sendMessage(
                                Component.text("당신의 이름은 이제 ")
                                        .append(Component.text(name, NamedTextColor.GREEN))
                                        .append(Component.text("입니다"))
                        )
                    }
                }
            }

            "getrealname" {
                requires { isOp }

                then("name" to string(StringType.GREEDY_PHRASE).apply {
                    suggests {
                        suggest(Bukkit.getOnlinePlayers().map { p -> p.name })
                    }
                }) {
                    executes {
                        val name: String by it

                        sender.sendMessage(Component.text(PlayerNameHandler.getReverseName(name).toString()))
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