package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.player.PlayerType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class QuitAndDeathListener implements Listener {

    private final Falldown plugin;

    public QuitAndDeathListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.getState() == GameState.LOBBY) {
            e.setQuitMessage(plugin.getConfigString("message.event.quitmessage")
                    .replace("%player%", p.getDisplayName()));
        } else {
            if (plugin.getPlayers().get(p) == PlayerType.ACTIVE) {
                e.setQuitMessage("");
                plugin.broadcast(plugin.getConfigString("message.event.playerdeathsolo")
                        .replace("%player%", p.getDisplayName())
                        .replace("%number%", String.valueOf(plugin.getActivePlayers().size() - 1)));
            }
        }

        if (plugin.getPlayers().get(p) == PlayerType.ACTIVE) {
            if (plugin.getState() == GameState.IN_GAME || plugin.getState() == GameState.GRACE_PERIOD) {
                playerDies(p, true);
            } else if (plugin.getState() == GameState.DROP) {
                playerDies(p, false);
            } else {
                plugin.getPlayers().remove(p);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        e.setDeathMessage("");
        e.setShouldDropExperience(false);

        if (plugin.getPlayers().get(p) == PlayerType.SPECTATOR) {
            return;
        }

        if (p.getKiller() == null) {
            plugin.broadcast(plugin.getConfigString("message.event.playerdeathsolo")
                    .replace("%player%", p.getDisplayName())
                    .replace("%number%", String.valueOf(plugin.getActivePlayers().size() - 1)));
        } else {
            p.getKiller().giveExpLevels((int) Math.ceil(((double) p.getLevel()) / 100 * plugin.getConfig().getDouble("setting.levelgainonkillpercentage")));

            plugin.broadcast(plugin.getConfigString("message.event.playerdeath")
                    .replace("%player%", p.getDisplayName())
                    .replace("%byplayer%", p.getKiller().getDisplayName())
                    .replace("%number%", String.valueOf(plugin.getActivePlayers().size() - 1)));
        }

        p.setLevel(p.getLevel() - (int) Math.ceil(((double) p.getLevel()) / 100 * plugin.getConfig().getDouble("setting.levelloseondeathpercentage")));

        if (plugin.isSQL) {
            plugin.saveExperience(p.getUniqueId().toString(), p.getLevel());
        }

        playerDies(p, true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> p.spigot().respawn(), 20L);
    }

    private void playerDies(Player p, boolean spawnZombie) {
        if (spawnZombie) {
            Location playerLocation = p.getLocation();
            playerLocation.setYaw(0.0F);
            playerLocation.setPitch(0.0F);
            Zombie zombie = (Zombie) p.getLocation().getWorld().spawnEntity(playerLocation, EntityType.ZOMBIE);
            zombie.setCanPickupItems(false);
            zombie.setAdult();
            zombie.setFireTicks(0);
            zombie.setSilent(true);
            if (zombie.getEquipment() != null) {
                zombie.getEquipment().clear();
            }
            zombie.setAI(false);

            ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) is.getItemMeta();
            meta.setPlayerProfile(p.getPlayerProfile());
            is.setItemMeta(meta);
            zombie.getEquipment().setHelmet(is);
        }

        plugin.getPlayers().remove(p);
        plugin.getPlayers().put(p, PlayerType.SPECTATOR);
        List<Player> activePlayers = plugin.getActivePlayers();

        if (activePlayers.size() <= 1) {
            if (activePlayers.size() == 1) {
                Player winner = activePlayers.get(0);
                if (winner != null) {
                    plugin.getServer().broadcastMessage(plugin.getConfigString("message.event.winner")
                            .replace("%player%", winner.getDisplayName()));

                    if (plugin.isSQL) {
                        plugin.saveExperience(winner.getUniqueId().toString(), winner.getLevel());
                    }

                    for (Player online : plugin.getServer().getOnlinePlayers()) {
                        online.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                    }
                }
            }

            plugin.startEnding();
        }
    }

}
