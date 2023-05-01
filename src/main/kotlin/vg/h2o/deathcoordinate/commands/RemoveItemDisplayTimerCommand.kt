package vg.h2o.deathcoordinate.commands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import vg.h2o.deathcoordinate.handlers.ItemDespawnTimerHandler

object RemoveItemDisplayTimerCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        val message = if (sender is Player) {

            if (ItemDespawnTimerHandler.getTimer(sender).end()) {
                "§a아이템 디스폰 타이머가 제거되었습니다."
            } else {
                "§c아이템 디스폰 타이머가 존재하지 않습니다."
            }
        } else {
            "§c플레이어만 이 명령어를 사용할 수 있습니다."
        }

        sender.sendMessage(Component.text(message))

        return true
    }
}