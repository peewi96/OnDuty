/*
 *  DoOdy: Separates Admin/Mod duties so everyone can enjoy the game.
 *  Copyright (C) 2012  M.Y.Azad
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package me.angelofdev.DoOdy.listeners;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.angelofdev.DoOdy.Log;
import me.angelofdev.DoOdy.command.DoOdyCommandExecutor;
import me.angelofdev.DoOdy.config.Configuration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DoOdyPlayerListener implements Listener {
	
	public DoOdyPlayerListener() {
	}

	public static HashMap<String, Integer> expOrb = new HashMap<String, Integer>();
	public static HashMap<String, ItemStack[]> armour = new HashMap<String, ItemStack[]>();
	public static HashMap<String, GameMode> duty = new HashMap<String, GameMode>();
	public static HashMap<String, ItemStack[]> inventory = new HashMap<String, ItemStack[]>();
	List<Integer> configDropList = Configuration.config.getIntegerList("Duty Deny Drops.whitelist");
	List<Integer> configStorageDenied = Configuration.config.getIntegerList("Deny Storage.storage");
	public static List<Integer> configLbTools = Configuration.config.getIntegerList("Allow.LogBlock.Tools");
	public static List<Integer> configWeTools = Configuration.config.getIntegerList("Allow.WorldEdit.Tools");

	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) {
			String playerName = player.getName();
			if (player.isOp() || duty.containsKey(playerName) || player.hasPermission("doody.failsafe.bypass")){
				return;
			}
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			if (DoOdyCommandExecutor.myArr.contains(playerName)) {
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playerName));
			}
			if (expOrb.containsKey(playerName)) {
				expOrb.remove(playerName);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (duty.containsKey(playerName)) {
			try {
				if (inventory.containsKey(playerName)) {
					player.getInventory().setContents(inventory.get(playerName));
				} else {
					player.getInventory().clear();
					try {
						Integer size = player.getInventory().getSize();
						Integer i = 0;
						for(i=0; i < size; i++) {
					        ItemStack item = new ItemStack(0, 0);
					        if(Configuration.inventory.getInt(playerName + "." + i.toString() + ".amount", 0) !=0) {
					        	Integer amount = Configuration.inventory.getInt(playerName + "." + i.toString() + ".amount", 0);
					        	Integer durability = Configuration.inventory.getInt(playerName + "." + i.toString() + ".durability", 0);
					        	Integer type = Configuration.inventory.getInt(playerName + "." + i.toString() + ".type", 0);
					        	item.setAmount(amount);
					        	item.setTypeId(type);
					        	item.setDurability(Short.parseShort(durability.toString()));
					        	player.getInventory().setItem(i, item);
							}
						}
					} catch(Exception e) {
					}
				}
				Configuration.inventory.set(playerName, null);
				Configuration.inventory.save();
				player.getInventory().setArmorContents(armour.get(playerName));
				armour.remove(playerName);
				World world = Bukkit.getServer().getWorld(Configuration.location.getString(playerName + ".world"));
				double x = Configuration.location.getDouble(playerName + ".x");
				double y = Configuration.location.getDouble(playerName + ".y");
				double z = Configuration.location.getDouble(playerName + ".z");
				double pit = Configuration.location.getDouble(playerName + ".pitch");
				double ya = Configuration.location.getDouble(playerName + ".yaw");
				float pitch = (float) pit;
				float yaw = (float) ya;
												
				Location local = new Location(world, x, y, z, yaw, pitch);
				player.teleport(local);

				Configuration.location.set(playerName, null);
				Configuration.location.save();
				player.setGameMode(GameMode.SURVIVAL);
				duty.remove(playerName);
				player.setLevel(DoOdyPlayerListener.expOrb.get(playerName));
				expOrb.remove(playerName);
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playerName));
			} catch (Exception e) {
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playerName));
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
			}
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (duty.containsKey(playerName)) {
			try {
				if (inventory.containsKey(playerName)) {
					player.getInventory().setContents(inventory.get(playerName));
				} else {
					player.getInventory().clear();
					try {
						Integer size = player.getInventory().getSize();
						Integer i = 0;
						for(i=0; i < size; i++) {
					        ItemStack item = new ItemStack(0, 0);
					        if(Configuration.inventory.getInt(playerName + "." + i.toString() + ".amount", 0) !=0) {
					        	Integer amount = Configuration.inventory.getInt(playerName + "." + i.toString() + ".amount", 0);
					        	Integer durability = Configuration.inventory.getInt(playerName + "." + i.toString() + ".durability", 0);
					        	Integer type = Configuration.inventory.getInt(playerName + "." + i.toString() + ".type", 0);
					        	item.setAmount(amount);
					        	item.setTypeId(type);
					        	item.setDurability(Short.parseShort(durability.toString()));
					        	player.getInventory().setItem(i, item);
							}
						}
					} catch(Exception e) {
					}
				}
				Configuration.inventory.set(playerName, null);
				Configuration.inventory.save();
				player.getInventory().setArmorContents(armour.get(playerName));
				armour.remove(playerName);
				World world = Bukkit.getServer().getWorld(Configuration.location.getString(playerName + ".world"));
				double x = Configuration.location.getDouble(playerName + ".x");
				double y = Configuration.location.getDouble(playerName + ".y");
				double z = Configuration.location.getDouble(playerName + ".z");
				double pit = Configuration.location.getDouble(playerName + ".pitch");
				double ya = Configuration.location.getDouble(playerName + ".yaw");
				float pitch = (float) pit;
				float yaw = (float) ya;
												
				Location local = new Location(world, x, y, z, yaw, pitch);
				player.teleport(local);

				Configuration.location.set(playerName, null);
				Configuration.location.save();
				player.setGameMode(GameMode.SURVIVAL);
				duty.remove(playerName);
				player.setLevel(DoOdyPlayerListener.expOrb.get(playerName));
				expOrb.remove(playerName);
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playerName));
			} catch (Exception e) {
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playerName));
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
			}
		}
	}
		
	@EventHandler(ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		String playerName = player.getName();
		
		if(duty.containsKey(playerName)) {
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		
		if(duty.containsKey(playerName) && armour.containsKey(playerName)) {
			player.getInventory().setArmorContents(armour.get(playerName));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		
		if (duty.containsKey(playerName) && Configuration.config.getBoolean("Duty Deny Drops.enabled")) {
			if (!(player.isOp() || player.hasPermission("doody.dropitems"))) {
				Item item = event.getItemDrop();
				int itemID = item.getItemStack().getTypeId();
				if (!(configDropList.contains(itemID))) {
					String message = item.getItemStack().getType().name();
					String itemname = message.toLowerCase();
			
					event.getItemDrop().remove();
			
					if (Configuration.config.getBoolean("Duty Deny Drops.messages")) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "There's no need to drop " + ChatColor.YELLOW + itemname + ChatColor.RED + " while on Duty.");
					}
					if (Configuration.config.getBoolean("Debug.enabled")) {
						Log.info("[DEBUG] Success! " + playerName + " got denied item drop.");
					}
				}
			} else {
				if (Configuration.config.getBoolean("Debug.enabled")) {
					Item item = event.getItemDrop();
					int itemID = item.getItemStack().getTypeId();
					String message = item.getItemStack().getType().name();
					String itemname = message.toLowerCase();
					if (configDropList.contains(itemID)) {
						Log.info("[DEBUG] Warning! " + itemname + " is whitelisted in config.");
						Log.info("[DEBUG] Warning! " + "Allowing " + playerName + " to drop " + itemname);
					} else {
						if (player.isOp()) {
							Log.info("[DEBUG] Warning! " + playerName + " is OP -Allowing item drop, " + itemname);
						} else if (player.hasPermission("doody.dropitems")) {
							Log.info("[DEBUG] Warning! " + playerName + " has doody.dropitems -Allowing item drop, " + itemname);
						} else {
							//It should not have reached here
							Log.severe("Another plugin may be causing a conflict. DoOdy Debug cannot make sense. Section onPlayerDropItem in DoOdyPlayerListener");
						}
					}
				}
				return;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			String playerName = player.getName();
			
			if (duty.containsKey(playerName) && Configuration.config.getBoolean("Deny Storage.enabled")) {
				Block block = event.getClickedBlock();
				int blockID = block.getType().getId();
				
				if (Configuration.config.getBoolean("Debug.enabled")) {
					Log.info("[DEBUG] " + playerName + " Right Clicked on " + blockID);
				}				
				if (configStorageDenied.contains(blockID)) {
					if (!(player.isOp() || player.hasPermission("doody.storage"))) {
						event.setCancelled(true);
						if (Configuration.config.getBoolean("Deny Storage.messages")) {
							player.sendMessage(ChatColor.RED + "There's no need to store things while on duty.");
							if (Configuration.config.getBoolean("Debug.enabled")) {
								Log.info("[DEBUG] Success! " + playerName + " got denied storage interact.");
							}
						}
					} else {
						if (Configuration.config.getBoolean("Debug.enabled")) {
							if (player.isOp()) {
								Log.info("[DEBUG] Warning! " + playerName + " is OP -Allowing storage interact");
							} else if (player.hasPermission("doody.storage")) {
								Log.info("[DEBUG] Warning! " + playerName + " has doody.storage -Allowing storage interact");
							} else if (!(configStorageDenied.contains(blockID))) {
								Log.info("[DEBUG] Warning! " + block.getType().name().toLowerCase() + " is not in 'Deny Storage.storage' list -Allowing storage interact");
							} else {
								//It should not have reached here
								Log.severe("Another plugin may be causing a conflict. DoOdy Debug cannot make sense. Section onPlayerInteract in DoOdyPlayerListener");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof StorageMinecart) {
			Player player = event.getPlayer();
			String playerName = player.getName();
			
			if (duty.containsKey(playerName) && (Configuration.config.getBoolean("Deny Storage.enabled"))) {
				if (!(player.isOp() || player.hasPermission("doody.storage"))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Deny Storage.messages")) {
						player.sendMessage(ChatColor.RED + "There's no need to store things while on Duty.");
						if (Configuration.config.getBoolean("Debug.enabled")) {
							Log.info("[DEBUG] Success! " + playerName + " got denied storage interact.");
						}
					}
				} else {
					if (Configuration.config.getBoolean("Debug.enabled")) {
						if (player.isOp()) {
							Log.info("[DEBUG] Warning! " + playerName + " is OP -Allowing storage interact");
						} else if (player.hasPermission("doody.storage")) {
							Log.info("[DEBUG] Warning! " + playerName + " has doody.storage -Allowing storage interact");
						} else {
							//It should not have reached here
							Log.severe("Another plugin may be causing a conflict. DoOdy Debug cannot make sense. Section onEntityInteract in DoOdyPlayerListener");
						}
					}
				}
			}
		}
	}	
	
	/** SLAPI = Saving/Loading API
	 * API for Saving and Loading Objects.
	 * @author Tomsik68
	 */
	public static class SLAPI {
		public static void save(Object obj,String path) throws Exception {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		public static Object load(String path) throws Exception	{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			Object result = ois.readObject();
			ois.close();
			return result;
		}
	}
}