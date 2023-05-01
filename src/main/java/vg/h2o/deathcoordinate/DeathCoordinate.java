package vg.h2o.deathcoordinate;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import vg.h2o.deathcoordinate.commands.RemoveItemDespawnTimer;
import vg.h2o.deathcoordinate.events.OnPlayerDeath;
import vg.h2o.deathcoordinate.events.OnPlayerJoin;
import vg.h2o.deathcoordinate.events.OnPlayerRespawn;

import java.util.UUID;

public final class DeathCoordinate extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getLogger().info("DeathCoordinate is running");

        Bukkit.getPluginManager().registerEvents(new OnPlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerRespawn(), this);

        this.getCommand("removeItemDespawnTimer").setExecutor(new RemoveItemDespawnTimer());
        this.getCommand("ridt").setExecutor(new RemoveItemDespawnTimer());

        this.getServer().getScheduler().runTaskTimer(this, () -> {
            for(Player player : this.getServer().getOnlinePlayers()) {
                UUID playerUniqueId = player.getUniqueId();

                if(OnPlayerDeath.isPlayerItemDespawnTimerExists(playerUniqueId)) {
                    if(OnPlayerDeath.isItemDespawnTimerIsPlayerRespawned(playerUniqueId) && OnPlayerDeath.isPlayerNearLastDeathLocation(playerUniqueId, 1)) {
                        OnPlayerDeath.endItemDespawnTimer(playerUniqueId);

                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                        player.sendActionBar("§a아이템 수복 성공");
                    } else {
                        OnPlayerDeath.reduceItemDespawnTimerProgress(playerUniqueId);
                    }
                }
            }
        }, 0, 20);

        return;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DeathCoordinate is stopped");

        return;
    }
}
