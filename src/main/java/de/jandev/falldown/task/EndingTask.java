package de.jandev.falldown.task;

import de.jandev.falldown.Falldown;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingTask extends BukkitRunnable {

    private final Falldown plugin;
    private int time = 10;

    public EndingTask(Falldown plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (time <= 0) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                p.kickPlayer("Thank you for playing, server restarts now!");
            }

            plugin.getServer().shutdown();
        } else if (time == 10 || time <= 3) {
            plugin.broadcast(plugin.getConfigString("message.task.serverrestartsin")
                    .replace("%number%", Integer.toString(time)));
        }
        time--;
    }
}
