package vg.h2o.dimibug

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.roundToInt

class ItemDespawnTimer {

    private var remainSeconds = 0
        set(value) {
            field = value

            if (value > 0) {
                bossBar.progress(value.toFloat() / DESPAWN_SECONDS)
                bossBar.name(title)
            } else {
                player.hideBossBar(bossBar)
            }
        }

    private lateinit var player: Player

    private val bossBar = BossBar.bossBar(Component.empty(), 0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)

    private lateinit var lastDeathLocation: Location

    private val title: Component
        get() {
            val worldColor = when (lastDeathLocation.world.name) {
                "world_nether" -> NamedTextColor.RED
                "world_the_end" -> NamedTextColor.LIGHT_PURPLE
                else -> NamedTextColor.GREEN
            }
            return Component.text("아이템 디스폰까지 약 ")
                    .append(Component.text(remainSeconds, NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("초 "))
                    .append(Component.text("(${lastDeathLocation.blockX}, ${lastDeathLocation.blockY}, ${lastDeathLocation.blockZ})", worldColor))
        }

    fun onDeath() {
        lastDeathLocation = player.location
        remainSeconds = DESPAWN_SECONDS
        player.showBossBar(bossBar)
    }

    fun changePlayer(player: Player) {
        if (remainSeconds > 0) {
            this.player.hideBossBar(bossBar)
            player.showBossBar(bossBar)
        }

        this.player = player
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
            player.sendActionBar(Component.text("아이템 수복 실패", NamedTextColor.RED))
            return
        }

        if (player.world != lastDeathLocation.world || player.isDead) return

        if (player.location.distance(lastDeathLocation) <= 1) {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f)
            player.sendActionBar(Component.text("아이템 수복 성공", NamedTextColor.GREEN))
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
                .append(Component.text(ARROWS[index], if (index == 0) NamedTextColor.GREEN else NamedTextColor.RED))
        player.sendActionBar(text)
    }

    companion object {
        private val ARROWS = listOf("↑", "↗", "→", "↘", "↓", "↙", "←", "↖")
    }
}