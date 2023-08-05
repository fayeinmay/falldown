package de.jandev.falldown.task;

import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageTask extends BukkitRunnable {

    private final Player player;
    private double time;

    public DamageTask(Player player, double time) {
        this.time = time;
        this.player = player;
    }

    @Override
    public void run() {
        if (time <= 0) {
            this.cancel();
            return;
        }
        player.damage(0.001);
        player.playEffect(EntityEffect.HURT);
        time--;
    }
}
