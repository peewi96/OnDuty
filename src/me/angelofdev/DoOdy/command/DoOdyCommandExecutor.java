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

package me.angelofdev.DoOdy.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import me.angelofdev.DoOdy.DoOdy;
import me.angelofdev.DoOdy.Log;
import me.angelofdev.DoOdy.config.Configuration;
import me.angelofdev.DoOdy.listeners.DoOdyPlayerListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public class DoOdyCommandExecutor implements CommandExecutor {

	@SuppressWarnings("unused")
	private DoOdy plugin;
	
	public DoOdyCommandExecutor(DoOdy plugin) {
		this.plugin = plugin;
	}
		
	public static ArrayList<String> myArr = new ArrayList<String>();

	public void addPlayer(String playername) {
		myArr.add(playername);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			String playername = player.getName();
			if (args.length == 0) {
				if (cmd.getName().equalsIgnoreCase("doody")) {
					player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "DoOdy Commands" + ChatColor.GREEN + " ]____________");
					player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "Short: /dm, /duty" + ChatColor.GREEN + " ]____________");
					if (player.isOp() || player.hasPermission("doody.duty")) {
						player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "on " + ChatColor.WHITE + "Turns on Duty Mode.");
						player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "off " + ChatColor.WHITE + "Turns off Duty Mode.");
					}
					player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "list " + ChatColor.WHITE + "Shows players on DoOdy Duty.");
					if (player.hasPermission("doody.reload")) {
						player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "reload " + ChatColor.WHITE + "Reload the config.yml changes ingame.");
					}
					if  (!myArr.isEmpty()) {
						player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "Players on Duty" + ChatColor.GREEN + " ]____________");
						player.sendMessage(ChatColor.GOLD + "" + myArr);												
					}
					return true;
				}
			}
			if ((cmd.getName().equalsIgnoreCase("doody")) && (args[0].equalsIgnoreCase("on"))) {
				if (player.isOp() || player.hasPermission("doody.duty")) {
					if (!DoOdyPlayerListener.duty.containsKey(playername)) {
						if (!(player.getGameMode() == GameMode.CREATIVE)) {
							if (Configuration.config.getBoolean("Debug.enabled", true)) {
								Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playername + "used /doody on");
							}
							try {
								//add player to duty list.
								addPlayer(playername);
								//save player's xp level.
								DoOdyPlayerListener.expOrb.put(playername, player.getLevel());
								//save player's inventory & clear it.
								DoOdyPlayerListener.inventory.put(playername, player.getInventory().getContents());
								player.getInventory().clear();
								//save player's location.
								DoOdyPlayerListener.location.put(playername, player.getLocation());
								//put player on creative mode.
								player.setGameMode(GameMode.CREATIVE);
								DoOdyPlayerListener.duty.put(playername, player.getGameMode());
								player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "You're now on Duty.");	
								if (Configuration.config.getBoolean("Debug.enabled", true)) {
									Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playername + "'s data added to #Maps.");
									Log.info("Debug mode. " + playername + "'s data saved to #Maps.");
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Your data has been saved in #Maps.");		
								}
							} catch (Exception e) {
								player.setGameMode(GameMode.CREATIVE);
								player.getInventory().clear();
								Log.severe("Failed Storing #Map on /doody on");
								player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Failed storing data in #Maps.");
							}
							return true;
						} else {
							player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "You must be in Survival mode first!");
						}
					} else if (DoOdyPlayerListener.duty.containsKey(playername)) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "You're already on Duty!");
					}
				}
			}
			if ((cmd.getName().equalsIgnoreCase("doody")) && (args[0].equalsIgnoreCase("off"))) {
				if (player.isOp() || player.hasPermission("doody.duty")) {
					if (DoOdyPlayerListener.duty.containsKey(playername)) {
						if (Configuration.config.getBoolean("Debug.enabled", true)) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playername + "used /doody off");
						}
						try {
							//remove player from list of players on duty.
							myArr.removeAll(Arrays.asList(playername));
							//resore player's gamemode & remove data from #map.
							player.setGameMode(GameMode.SURVIVAL);
							DoOdyPlayerListener.duty.remove(playername);
							//restore player's xp & remove data from #map.
							player.setLevel(DoOdyPlayerListener.expOrb.get(playername));
							DoOdyPlayerListener.expOrb.remove(playername);
							//resotre player's inventory & remove data from #map.
							player.getInventory().setContents(DoOdyPlayerListener.inventory.get(playername));
							DoOdyPlayerListener.inventory.remove(playername);
							//restore player's location & remove data from #map.
							player.teleport(DoOdyPlayerListener.location.get(playername));
							DoOdyPlayerListener.location.remove(playername);
							player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "You're no longer on Duty.");	
							if (Configuration.config.getBoolean("Debug.enabled", true)) {
								Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playername + "'s data restored and #Maps Removed.");
								Log.info("Debug mode. " + playername + "'s data restored & #maps cleared.");
								player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Your data is restored & #Maps Removed.");		
							}
						} catch (Exception e) {
							myArr.removeAll(Arrays.asList(playername));
							DoOdyPlayerListener.duty.remove(playername);
							player.setGameMode(GameMode.SURVIVAL);
							player.getInventory().clear();
							Log.warnings(playername + " was on duty when plugin was disabled. Failed restoring inventory.");
							Log.warnings(playername + " was on duty when plugin was disabled. Failed restoring location.");
							player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Failed restoring Inventory. Plugin was disabled while you were on Duty.");
						}
						return true;
					} else if (!DoOdyPlayerListener.duty.containsKey(playername)) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "You're not on Duty!");
					}
				}
			}
			if ((cmd.getName().equalsIgnoreCase("doody")) && (args[0].equalsIgnoreCase("list"))) {
				player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "Players on Duty" + ChatColor.GREEN + " ]____________");
				if  (!myArr.isEmpty()) {
					player.sendMessage(ChatColor.GOLD + "" + myArr);												
				} else {
					player.sendMessage(ChatColor.GOLD + "No players are on duty.");
				}
			}
			if ((cmd.getName().equalsIgnoreCase("doody")) && (args[0].equalsIgnoreCase("reload"))) {
				if (player.isOp() || player.hasPermission("doody.reload")) {
					try {
						Configuration.config.reload();
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Config Reloaded.");
					} catch (FileNotFoundException e) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Config Not Found!");
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidConfigurationException e) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Config Not Valid Format!");
						e.printStackTrace();
					}					
				} else {
					player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Need permission node doody.reload");
				}
			}
		}
		return false;
	}
}
