package vg.h2o.dimibug

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import kotlin.math.roundToInt

class ItemDespawnTimer {

    private var remainSeconds = 0
        set(value) {
            field = value

            if (value > 0) {
                timer.progress = value.toDouble() / DESPAWN_SECONDS
                timer.setTitle(title)
            } else {
                timer.removeAll()
            }
        }

    private lateinit var player: Player

    private val timer = Bukkit.getServer().createBossBar(null, BarColor.RED, BarStyle.SOLID).apply {
        progress = 1.0
    }

    private lateinit var lastDeathLocation: Location

    private val title: String
        get() {
            val worldColor = when (lastDeathLocation.world.name) {
                "world_nether" -> "§c"
                "world_the_end" -> "§d"
                else -> "§a"
            }

            return "아이템 디스폰까지 약 §b$remainSeconds§f초 $worldColor(${lastDeathLocation.blockX}, ${lastDeathLocation.blockY}, ${lastDeathLocation.blockZ})"
        }

    fun onDeath() {
        lastDeathLocation = player.location
        remainSeconds = DESPAWN_SECONDS
        timer.addPlayer(player)
    }

    fun changePlayer(player: Player) {
        this.player = player

        if (remainSeconds > 0) {
            timer.removeAll()
            timer.addPlayer(player)
        }
    }

    fun end() = if (remainSeconds > 0) {
        remainSeconds = 0
        true
    } else {
        false
    }

    fun tick(tick: Int) {
        if (remainSeconds == 0) return

        if (tick == 0 && --remainSeconds == 0) {
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 10f, 1f)
            player.sendActionBar(Component.text("아이템 수복 실패", TextColors.RED))
            return
        }

        if (player.world != lastDeathLocation.world || player.isDead) return

        if (player.location.distance(lastDeathLocation) <= 1) {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f)
            player.sendActionBar(Component.text("아이템 수복 성공", TextColors.GREEN))
            remainSeconds = 0
            return
        }

        val direction = lastDeathLocation.clone().subtract(player.location).toVector()
        var angle = player.eyeLocation.clone().setDirection(direction).yaw

        if (angle < 0) {
            angle += 360
        }

        angle -= player.location.yaw

        if (angle < 0) {
            angle += 360
        }

        var index = ((angle / 45) % 8).roundToInt()
        if (index == 8) index = 0

        val text = Component.text("${"%.1f".format(lastDeathLocation.distance(player.location))}m ")
                .append(Component.text(ARROWS[index], if (index == 0) TextColors.GREEN else TextColors.RED))
        player.sendActionBar(text)
    }

    companion object {
        private val ARROWS = listOf("↑", "↗", "→", "↘", "↓", "↙", "←", "↖")
    }
}