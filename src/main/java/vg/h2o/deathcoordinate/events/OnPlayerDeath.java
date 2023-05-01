package vg.h2o.deathcoordinate.events;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public class OnPlayerDeath implements Listener {


    private static final class ItemDespawnTimer {

        private final BossBar timer;

        private int currentTime;

        private Player player;

        private final Location lastDeathLocation;

        private boolean isPlayerRespawned = false;

        private ItemDespawnTimer(Player player) {
            this.currentTime = 300;
            this.player = player;
            this.lastDeathLocation = player.getLocation();
            this.timer = player.getServer().createBossBar(getTimerTitle(300, this.lastDeathLocation), BarColor.RED, BarStyle.SOLID);

            this.timer.setProgress(1);
            this.timer.addPlayer(player);

            return;
        }

        public boolean isPlayerNearLastDeathLocation(int range) {
            int lastDeathLocationX = (int)this.lastDeathLocation.getX();
            int lastDeathLocationY = (int)this.lastDeathLocation.getY();
            int lastDeathLocationZ = (int)this.lastDeathLocation.getZ();

            Location currentPlayerLocation = player.getLocation();
            int currentPlayerLocationX = (int)currentPlayerLocation.getX();
            int currentPlayerLocationY = (int)currentPlayerLocation.getY();
            int currentPlayerLocationZ = (int)currentPlayerLocation.getZ();

            return lastDeathLocation.getWorld().equals(currentPlayerLocation.getWorld()) && lastDeathLocationX - range <= currentPlayerLocationX && currentPlayerLocationX <= lastDeathLocationX + range &&
            lastDeathLocationY - range <= currentPlayerLocationY && currentPlayerLocationY <= lastDeathLocationY + range &&
            lastDeathLocationZ - range <= currentPlayerLocationZ && currentPlayerLocationZ <= lastDeathLocationZ + range;
        }

        public Player getPlayer() {
            return this.player;
        }

        public void setPlayer(Player player) {
            this.player = player;

            return;
        }

        public boolean isPlayerRespawned() {
            return this.isPlayerRespawned;
        }

        public void setPlayerRespawned() {
            this.isPlayerRespawned = true;

            return;
        }


        public void addPlayer(Player player) {
            removeAll();
            this.timer.addPlayer(player);

            return;
        }

        public void removeAll() {
            this.timer.removeAll();

            return;
        }

        public long getCurrentTime() {
            return this.currentTime;
        }

        public String getTimerTitle(int time, Location lastDeathLocation) {
            String worldColor = "§";

            switch(lastDeathLocation.getWorld().getName()) {
                case "world_nether": {
                    worldColor += "c";

                    break;
                }

                case "world_the_end": {
                    worldColor += "d";

                    break;
                }

                default: {
                    worldColor += "a";

                    break;
                }
            }

            return "아이템 디스폰까지 약 §b" + time +  "§f초 " + worldColor + "(" + (int)lastDeathLocation.getX() + ", " + (int)lastDeathLocation.getY() + ", " + (int)lastDeathLocation.getZ() + ")";
        }

        public void reduceCurrentTime() {
            this.timer.setProgress((double)(--this.currentTime) / 300);
            this.timer.setTitle(getTimerTitle(this.currentTime, this.lastDeathLocation));

            return;
        }
    }
    private static final HashMap<UUID, ItemDespawnTimer> itemDespawnTimers = new HashMap<>();

    public static void addItemDespawnTimerPlayer(UUID playerUniqueId, Player player) {
        itemDespawnTimers.get(playerUniqueId).addPlayer(player);

        return;
    }

    public static boolean isPlayerItemDespawnTimerExists(UUID playerUniqueId) {
        return itemDespawnTimers.containsKey(playerUniqueId);
    }

    public static boolean isPlayerNearLastDeathLocation(UUID playerUniqueId, int range) {
        return itemDespawnTimers.get(playerUniqueId).isPlayerNearLastDeathLocation(range);
    }

    public static void setItemDespawnTimerPlayer(UUID playerUniqueId, Player player) {
        itemDespawnTimers.get(playerUniqueId).setPlayer(player);

        return;
    }

    public static void reduceItemDespawnTimerProgress(UUID playerUniqueId) {
        ItemDespawnTimer itemDespawnTimer = itemDespawnTimers.get(playerUniqueId);

        if(itemDespawnTimer.getCurrentTime() != 0) {
            itemDespawnTimer.reduceCurrentTime();
        } else {
            endItemDespawnTimer(playerUniqueId);
            Player player = itemDespawnTimer.getPlayer();

            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 10, 1);
            player.sendActionBar("§c아이템 수복 실패");
        }

        return;
    }


    public static boolean isItemDespawnTimerIsPlayerRespawned(UUID playerUniqueId) {
        return itemDespawnTimers.get(playerUniqueId).isPlayerRespawned();
    }

    public static void setItemDespawnTimerIsPlayerRespawned(UUID playerUniqueId) {
        itemDespawnTimers.get(playerUniqueId).setPlayerRespawned();

        return;
    }

    public static void endItemDespawnTimer(UUID playerUniqueId) {
        itemDespawnTimers.get(playerUniqueId).removeAll();
        itemDespawnTimers.remove(playerUniqueId);

        return;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        Player player = playerDeathEvent.getPlayer();

        UUID playerUniqueId = player.getUniqueId();

        if(itemDespawnTimers.containsKey(playerUniqueId)) {
            endItemDespawnTimer(playerUniqueId);
        }

        itemDespawnTimers.put(player.getUniqueId(), new ItemDespawnTimer(player));

        return;
    }
}
