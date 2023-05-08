package vg.h2o.dimibug

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player

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
            return text("아이템 디스폰까지 약 ")
                .append(text(remainSeconds, NamedTextColor.LIGHT_PURPLE))
                .append(text("초 "))
                .append(
                    text(
                        "(${lastDeathLocation.blockX}, ${lastDeathLocation.blockY}, ${lastDeathLocation.blockZ})",
                        worldColor
                    )
                )
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
            player.sendActionBar(text("아이템 수복 실패", NamedTextColor.RED))
            return
        }

        if (player.world != lastDeathLocation.world || player.isDead) return

        val direction = Utils.getDirectionArrow(player, lastDeathLocation)

        if (direction.distance <= 1) {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f)
            player.sendActionBar(text("아이템 수복 성공", NamedTextColor.GREEN))
            remainSeconds = 0
            return
        }

        val text = text("${"%.1f".format(direction.distance)}m ")
            .append(text(direction.arrow, if (direction.index == 0) NamedTextColor.GREEN else NamedTextColor.RED))
        player.sendActionBar(text)
    }
}