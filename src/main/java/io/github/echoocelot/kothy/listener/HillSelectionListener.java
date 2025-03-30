package io.github.echoocelot.kothy.listener;

import io.github.echoocelot.kothy.api.ConfigManager;
import io.github.echoocelot.kothy.api.KothyMessaging;
import io.github.echoocelot.kothy.runnable.ParticleTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import io.github.echoocelot.kothy.api.PositionManager;
import io.github.echoocelot.kothy.object.Hill;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HillSelectionListener implements Listener {

    static Map<String, Boolean> areParticlesShowingMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        if (inv.getItemInMainHand().getType().equals(Material.getMaterial(ConfigManager.getWandItem()))) {
            if (player.hasPermission("kothy.command.kothy.wand")) {
                PositionManager pm = PositionManager.getPlayerInstance(player);

                EquipmentSlot hand = Objects.requireNonNull(event.getHand());
                if (hand.equals(EquipmentSlot.HAND)) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        event.setCancelled(true);
                        Block position = Objects.requireNonNull(event.getClickedBlock());
                        Location pos = new Location(player.getWorld(), position.getX(), position.getY(), position.getZ());
                        pm.setPos1(pos);
                        pm.setPos1Entered(true);
                        pm.setWorld(player.getWorld());
                        KothyMessaging.sendMessage(player, "Set first position to " + pos.getBlockX() + ", " + pos.getBlockY() + ", " + pos.getBlockZ());

                        if (areParticlesShowingMap.containsKey(player.getName()))
                            ParticleTimer.stopParticleTaskForSelection(player);

                        if (pm.getPos2() == null) {
                            ParticleTimer.startParticleTaskForSelection(pm.getPos1(), pm.getPos1(), player);
                            areParticlesShowingMap.put(player.getName(), true);
                        } else {
                            Hill hill = new Hill(pm.getPos1(), pm.getPos2(), player.getWorld());
                            if (hill.getLargestDimensionValue() <= ConfigManager.getMaxHillSizeToShowParticles()) {
                                ParticleTimer.startParticleTaskForSelection(pm.getPos1(), pm.getPos2(), player);
                                areParticlesShowingMap.put(player.getName(), true);
                            }
                        }
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        event.setCancelled(true);
                        Block position = Objects.requireNonNull(event.getClickedBlock());
                        Location pos = new Location(player.getWorld(), position.getX(), position.getY(), position.getZ());
                        pm.setPos2(pos);
                        pm.setPos2Entered(true);
                        pm.setWorld(player.getWorld());
                        KothyMessaging.sendMessage(player, "Set second position to " + pos.getBlockX() + ", " + pos.getBlockY() + ", " + pos.getBlockZ());

                        if (areParticlesShowingMap.containsKey(player.getName()))
                            ParticleTimer.stopParticleTaskForSelection(player);

                        if (pm.getPos1() == null) {
                            ParticleTimer.startParticleTaskForSelection(pm.getPos2(), pm.getPos2(), player);
                            areParticlesShowingMap.put(player.getName(), true);
                        } else {
                            Hill hill = new Hill(pm.getPos1(), pm.getPos2(), player.getWorld());
                            if (hill.getLargestDimensionValue() <= ConfigManager.getMaxHillSizeToShowParticles()) {
                                ParticleTimer.startParticleTaskForSelection(pm.getPos1(), pm.getPos2(), player);
                                areParticlesShowingMap.put(player.getName(), true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        ItemStack newItem = inv.getItem(event.getNewSlot());
        if (player.hasPermission("kothy.command.kothy.wand")) {
            if (newItem == null || !newItem.getType().equals(Material.getMaterial(ConfigManager.getWandItem()))) {
                if (areParticlesShowingMap.containsKey(player.getName())) {
                    ParticleTimer.stopParticleTaskForSelection(player);
                    areParticlesShowingMap.remove(player.getName());
                }
            } else {
                PositionManager pm = PositionManager.getPlayerInstance(player);
                if (areParticlesShowingMap.containsKey(player.getName()))
                    ParticleTimer.stopParticleTaskForSelection(player);
                if (pm.getPos1() != null && pm.getPos2() != null) {
                    ParticleTimer.startParticleTaskForSelection(pm.getPos1(), pm.getPos2(), player);
                    areParticlesShowingMap.put(player.getName(), true);
                } else if (pm.getPos2() != null) {
                    ParticleTimer.startParticleTaskForSelection(pm.getPos2(), pm.getPos2(), player);
                    areParticlesShowingMap.put(player.getName(), true);
                } else if (pm.getPos1() != null) {
                    ParticleTimer.startParticleTaskForSelection(pm.getPos1(), pm.getPos1(), player);
                    areParticlesShowingMap.put(player.getName(), true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        ItemStack mainItem = event.getMainHandItem();
        if (!mainItem.getType().equals(Material.getMaterial(ConfigManager.getWandItem()))) {
            if (areParticlesShowingMap.containsKey(player.getName())) {
                ParticleTimer.stopParticleTaskForSelection(player);
                areParticlesShowingMap.remove(player.getName());
            }
        } else {
            PositionManager pm = PositionManager.getPlayerInstance(player);
            if (areParticlesShowingMap.containsKey(player.getName()))
                ParticleTimer.stopParticleTaskForSelection(player);
            if (pm.getPos1() != null && pm.getPos2() != null) {
                ParticleTimer.startParticleTaskForSelection(pm.getPos1(), pm.getPos2(), player);
                areParticlesShowingMap.put(player.getName(), true);
            } else if (pm.getPos2() != null) {
                ParticleTimer.startParticleTaskForSelection(pm.getPos2(), pm.getPos2(), player);
                areParticlesShowingMap.put(player.getName(), true);
            } else if (pm.getPos1() != null) {
                ParticleTimer.startParticleTaskForSelection(pm.getPos1(), pm.getPos1(), player);
                areParticlesShowingMap.put(player.getName(), true);
            }
        }
    }
}
