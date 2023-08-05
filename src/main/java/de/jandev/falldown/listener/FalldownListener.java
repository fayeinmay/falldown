package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.player.PlayerType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class FalldownListener implements Listener {

    private final Falldown plugin;

    public FalldownListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if (e.getEntity() instanceof Zombie) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent e) {
        if (plugin.getPlayers().get(e.getPlayer()) != PlayerType.ACTIVE) {
            e.setFlyAtPlayer(false); // Bug in Paper, should be set to false by e.setCancelled(true)
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (!(plugin.getState() == GameState.GRACE_PERIOD
                || plugin.getState() == GameState.IN_GAME)
                || plugin.getPlayers().get(e.getPlayer()) == PlayerType.SPECTATOR) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.Mob)
                || e.getEntity().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (plugin.getPlayers().get(e.getPlayer()) == PlayerType.SPECTATOR) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        // We could show premium kicks here...
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        e.setMotd("Falldown - " + plugin.getState());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent e) {
        e.setAmount(0);
    }

    @EventHandler
    public void onEntityBreakDoor(EntityBreakDoorEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBedEnterEvent(PlayerBedEnterEvent e) {
        e.setCancelled(true);
    }
}
