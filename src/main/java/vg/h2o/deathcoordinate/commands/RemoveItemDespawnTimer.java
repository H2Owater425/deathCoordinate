package vg.h2o.deathcoordinate.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vg.h2o.deathcoordinate.events.OnPlayerDeath;

import java.util.UUID;

public class RemoveItemDespawnTimer implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] arguments) {
        if(commandSender instanceof Player) {
            UUID playerUniqueId = ((Player)commandSender).getUniqueId();
            if(OnPlayerDeath.isPlayerItemDespawnTimerExists(playerUniqueId)) {
                OnPlayerDeath.endItemDespawnTimer(playerUniqueId);
                commandSender.sendMessage("§a아이템 디스폰 타이머가 제거되었습니다.");
            } else {
                commandSender.sendMessage("§c아이템 디스폰 타이머가 존재하지 않습니다.");
            }
        } else {
            commandSender.sendMessage("§c플레이어만 이 명령어를 사용할 수 있습니다.");
        }

        return true;
    }
}
