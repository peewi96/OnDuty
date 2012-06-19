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
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import me.angelofdev.DoOdy.command.DoOdyCommandExecutor;
import me.angelofdev.DoOdy.config.Configuration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import org.bukkit.inventory.ItemStack;

public class DoOdyPlayerListener implements Listener {
	
	public DoOdyPlayerListener() {
	}

	public static HashMap<String, ItemStack[]> inventory = new HashMap<String, ItemStack[]>();
	public static HashMap<String, GameMode> duty = new HashMap<String, GameMode>();
	public static HashMap<String, Location> location = new HashMap<String, Location>();
	List<Integer> configDropList = Configuration.config.getIntegerList("Duty Deny Drops.whitelist");
	List<Integer> configStorageDenied = Configuration.config.getIntegerList("Deny Storage.storage");
	public static List<Integer> configLbTools = Configuration.config.getIntegerList("Allow.LogBlock.Tools");
	public static List<Integer> configWeTools = Configuration.config.getIntegerList("Allow.WorldEdit.Tools");

	public static HashMap<String, Integer> expOrb = new HashMap<String, Integer>();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		if ((!duty.containsKey(playername) && !player.isOp() && player.getGameMode() == GameMode.CREATIVE)) {
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		if (duty.containsKey(playername)) {
			try {
				player.getInventory().setContents(inventory.get(playername));
				inventory.remove(playername);
				player.teleport(location.get(playername));
				location.remove(playername);
				player.setGameMode(GameMode.SURVIVAL);
				duty.remove(playername);
				player.setLevel(DoOdyPlayerListener.expOrb.get(playername));
				expOrb.remove(playername);
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playername));
			} catch (Exception e) {
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playername));
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
			}
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		if (duty.containsKey(playername)) {
			try {
				player.getInventory().setContents(inventory.get(playername));
				inventory.remove(playername);
				player.teleport(location.get(playername));
				location.remove(playername);
				player.setGameMode(GameMode.SURVIVAL);
				duty.remove(playername);
				player.setLevel(DoOdyPlayerListener.expOrb.get(playername));
				expOrb.remove(playername);
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playername));
			} catch (Exception e) {
				DoOdyCommandExecutor.myArr.removeAll(Arrays.asList(playername));
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
			}
		}
	}
		
	@EventHandler(ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		String playername = player.getName();
		
		if(duty.containsKey(playername)) {
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		
		if (duty.containsKey(playername) && Configuration.config.getBoolean("Duty Deny Drops.enabled", true)) {
			if (!(player.isOp() || player.hasPermission("doody.dropitems"))) {
				Item item = event.getItemDrop();
				int itemID = item.getItemStack().getTypeId();
				if (!(configDropList.contains(itemID))) {
					String message = item.getItemStack().getType().name();
					String itemname = message.toLowerCase();
			
					event.getItemDrop().remove();
			
					if (Configuration.config.getBoolean("Duty Deny Drops.messages", true)) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "There's no need to drop " + ChatColor.YELLOW + itemname + ChatColor.RED + " while on Duty.");
					}
				}
			} else {
				return;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		int itemID = player.getItemInHand().getTypeId();
		
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;			
		}
		if (duty.containsKey(playername) && Configuration.config.getBoolean("Deny Storage.enabled", true)) {
			if (!(player.isOp() || player.hasPermission("doody.storage"))) {
				Block block = event.getClickedBlock();
				int blockID = block.getType().getId();
				if (configStorageDenied.contains(blockID)) {
					if ((configLbTools.contains(itemID) && Configuration.config.getBoolean("Allow.LogBlock.enabled", true)) || (configWeTools.contains(itemID) && Configuration.config.getBoolean("Allow.WorldEdit.enabled", true))) {
						return;
					} else {
						event.setCancelled(true);
						if (Configuration.config.getBoolean("Deny Storage.messages", true)) {
							player.sendMessage(ChatColor.RED + "There's no need to store things while on Duty.");
							if (Configuration.config.getBoolean("Debug.enabled", true)) {
								Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Block ID " + blockID);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
				
		if (!(event.getRightClicked() instanceof StorageMinecart)) {
			return;			
		} else {		
			if (duty.containsKey(playername) && (Configuration.config.getBoolean("Deny Storage.enabled", true) && Configuration.config.getBoolean("Deny Storage.Storage Minecart", true))) {
				if (!(player.isOp() || player.hasPermission("doody.storage"))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Deny Storage.messages", true)) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "There's no need to store things while on Duty.");
						if (Configuration.config.getBoolean("Debug.enabled", true)) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Success it was a Storage Minecart");
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
	
	
	public class LocationPack implements Serializable  {
	    private static final long serialVersionUID = -8100514952085724461L;
	 
	    private final String  worldname;
	    private final double x;
	    private final double y;
	    private final double z;
	 
	    public LocationPack(Location location) {
	        this.worldname = location.getWorld().getName();
	        this.x = location.getX();
	        this.y = location.getY();
	        this.z = location.getZ();
	    }
	 
	    public Location unpack() {
	        Location location = new Location(Bukkit.getWorld(this.worldname), this.x, this.y, this.z);
	 
	        return location;
	    }
	}
	
}
