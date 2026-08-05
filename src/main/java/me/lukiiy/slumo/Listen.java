package me.lukiiy.slumo;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Listen implements Listener {
    private final Game game;

    public Listen(Game game) {
        this.game = game;
    }

    @EventHandler
    public void dmg(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) e.setCancelled(true);
    }

    @EventHandler
    public void entityAttack(EntityDamageByEntityEvent e) {
        if (!game.active.get()) {
            e.setCancelled(true);
            return;
        }

        if (!(e.getDamager() instanceof Player && e.getEntity() instanceof Player)) return;

        e.setDamage(0.0);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (!game.active.get()) {
            e.setCancelled(true);
            return;
        }

        e.getItemInHand().setAmount(Math.min(e.getItemInHand().getAmount() + 2, 64));
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (!game.active.get()) {
            e.setCancelled(true);
        } else {
            if (MaterialSetTag.WOOL.isTagged(e.getBlock().getType())) e.setCancelled(true);

            e.setDropItems(true);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (!game.active.get()) return;

        if (e.getTo().y() <= 32) game.kill(e.getPlayer());
    }

    @EventHandler
    public void foodLevel(FoodLevelChangeEvent e) {
        if (!game.active.get()) return;

        e.setCancelled(true);
    }
}
