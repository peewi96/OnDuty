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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {			
		if (args.length == 0) {
			if (cmd.getName().equalsIgnoreCase("doody")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playername = player.getName();
					player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "DoOdy Commands" + ChatColor.GREEN + " ]____________");
					player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "Short: /dm, /duty" + ChatColor.GREEN + " ]____________");
					
					if (player.isOp() || player.hasPermission("doody.duty")) {
						player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "on " + ChatColor.WHITE + "Turns on Duty Mode.");
						player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "off " + ChatColor.WHITE + "Turns off Duty Mode.");
					}
					if (player.isOp() || player.hasPermission("doody.others")) {
						player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "<player> <on/off> " + ChatColor.WHITE + "Put <player> <on/off> Duty Mode.");
					}
					player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "list " + ChatColor.WHITE + "Shows players on DoOdy Duty.");
					if (player.isOp() || player.hasPermission("doody.reload")) {
						player.sendMessage(ChatColor.GOLD + "/doody " + ChatColor.AQUA + "reload " + ChatColor.WHITE + "Reload the config.yml changes ingame.");
					}
					if  (!myArr.isEmpty()) {
						player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "Players on Duty" + ChatColor.GREEN + " ]____________");
						player.sendMessage(ChatColor.GOLD + "" + myArr);												
					}
					return true;
				} else {
					Log.info("____________[ DoOdy Commands ]____________");
					Log.info("____________[ Short: /dm, /duty ]____________");
					Log.info("/doody <player> <on/off> [Put <player> <on/off> Duty Mode.]");
					Log.info("/doody list [Shows players on DoOdy Duty.]");
					Log.info("/doody reload [Reload the config.yml changes ingame.]");					
				}
			}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("on")) {
					if ((sender instanceof Player)) {
						Player player = (Player) sender;
						String playername = player.getName();
						if (player.isOp() || player.hasPermission("doody.duty")) {
							if (!DoOdyPlayerListener.duty.containsKey(playername)) {
								if (!(player.getGameMode() == GameMode.CREATIVE)) {
									if (Configuration.config.getBoolean("Debug.enabled")) {
										Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playername + "used /doody on");
									}
									Inventory playerInv = player.getInventory();
									try {
										//add player to duty list.
										addPlayer(playername);
										//save player's xp level.
										DoOdyPlayerListener.expOrb.put(playername, player.getLevel());

										//save player's inventory & clear it.
										DoOdyPlayerListener.inventory.put(playername, playerInv.getContents());
										try {
											Integer size = playerInv.getSize();
											Integer i = 0;
											for(i=0; i < size; i++) {
												ItemStack item = playerInv.getItem(i);
												if (item.getAmount() != 0) {
													Configuration.inventory.set(playername + "." + i.toString() + ".amount", item.getAmount());
													Short durab = item.getDurability();
													Configuration.inventory.set(playername + "." + i.toString() + ".durability", durab.intValue());
													Configuration.inventory.set(playername + "." + i.toString() + ".type", item.getTypeId());
													Configuration.inventory.save();
												}
											}
										} catch(Exception e) {
										}

										//save player's armour content.
										DoOdyPlayerListener.armour.put(playername, player.getInventory().getArmorContents());

										playerInv.clear(); //Clear player inventory

										//Save player location to file.
										String worldname = player.getLocation().getWorld().getName();
										Configuration.location.set(playername + ".world", worldname);
										Configuration.location.set(playername + ".x", player.getLocation().getX());
										Configuration.location.set(playername + ".y", player.getLocation().getY());
										Configuration.location.set(playername + ".z", player.getLocation().getZ());
										Configuration.location.set(playername + ".pitch", player.getLocation().getPitch());
										Configuration.location.set(playername + ".yaw", player.getLocation().getYaw());
										Configuration.location.save();

										//put player on creative mode.
										player.setGameMode(GameMode.CREATIVE);
										DoOdyPlayerListener.duty.put(playername, player.getGameMode());
										player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "You're now on Duty.");

										//Give Duty Tools?
										if (Configuration.config.getBoolean("Duty Tools.enabled")) {
											playerInv.setItem(0, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 1"), 1, (short) 0));
											playerInv.setItem(1, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 2"), 1, (short) 0));
											playerInv.setItem(2, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 3"), 1, (short) 0));
											playerInv.setItem(3, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 4"), 1, (short) 0));
											playerInv.setItem(4, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 5"), 1, (short) 0));
											playerInv.setItem(5, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 6"), 1, (short) 0));
											playerInv.setItem(6, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 7"), 1, (short) 0));
											playerInv.setItem(7, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 8"), 1, (short) 0));
											playerInv.setItem(8, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 9"), 1, (short) 0));
										}
										if (Configuration.config.getBoolean("Debug.enabled")) {
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
				}

				if (args[0].equalsIgnoreCase("off")) {
					if ((sender instanceof Player)) {
						Player player = (Player) sender;
						String playername = player.getName();
						if (player.isOp() || player.hasPermission("doody.duty")) {
							if (DoOdyPlayerListener.duty.containsKey(playername)) {
								if (Configuration.config.getBoolean("Debug.enabled")) {
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

									//restore player's location & remove data from #map.
									World world = Bukkit.getServer().getWorld(Configuration.location.getString(playername + ".world"));
									double x = Configuration.location.getDouble(playername + ".x");
									double y = Configuration.location.getDouble(playername + ".y");
									double z = Configuration.location.getDouble(playername + ".z");
									double pit = Configuration.location.getDouble(playername + ".pitch");
									double ya = Configuration.location.getDouble(playername + ".yaw");
									float pitch = (float) pit;
									float yaw = (float) ya;

									Location local = new Location(world, x, y, z, yaw, pitch);
									player.teleport(local);

									Configuration.location.set(playername, null);
									Configuration.location.save();

									if (DoOdyPlayerListener.inventory.containsKey(playername)) {
										player.getInventory().setContents(DoOdyPlayerListener.inventory.get(playername));
									} else {
										player.getInventory().clear();
										try {
											Integer size = player.getInventory().getSize();
											Integer i = 0;
											for(i=0; i < size; i++) {
												ItemStack item = new ItemStack(0, 0);
												if(Configuration.inventory.getInt(playername + "." + i.toString() + ".amount", 0) !=0) {
													Integer amount = Configuration.inventory.getInt(playername + "." + i.toString() + ".amount", 0);
													Integer durability = Configuration.inventory.getInt(playername + "." + i.toString() + ".durability", 0);
													Integer type = Configuration.inventory.getInt(playername + "." + i.toString() + ".type", 0);
													item.setAmount(amount);
													item.setTypeId(type);
													item.setDurability(Short.parseShort(durability.toString()));
													player.getInventory().setItem(i, item);
												}
											}
										} catch(Exception e) {
											Log.severe("Failed Loading Player Inventory from file on /doody off");
										}
									}
									Configuration.inventory.set(playername, null);
									Configuration.inventory.save();


									//respore player's armour contents & remove data from #map.
									if (DoOdyPlayerListener.armour.containsKey(playername)) {
										player.getInventory().setArmorContents(DoOdyPlayerListener.armour.get(playername));
										DoOdyPlayerListener.armour.remove(playername);
									}

									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "You're no longer on Duty.");	
									if (Configuration.config.getBoolean("Debug.enabled")) {
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
				}
				
				if (args[0].equalsIgnoreCase("list")) {
					if ((sender instanceof Player)) {
						Player player = (Player) sender;
						String playername = player.getName();
						player.sendMessage(ChatColor.GREEN + "____________[ " + ChatColor.GOLD + "Players on Duty" + ChatColor.GREEN + " ]____________");
						if  (!myArr.isEmpty()) {
							player.sendMessage(ChatColor.GOLD + "" + myArr);												
						} else {
							player.sendMessage(ChatColor.GOLD + "No players are on duty.");
						}
					} else {
						Log.info("____________[ Players on Duty ]____________");
						if  (!myArr.isEmpty()) {
							Log.info("" + myArr);												
						} else {
							Log.info("No players are on duty.");
						}						
					}
				}

				if (args[0].equalsIgnoreCase("reload")) {
					if ((sender instanceof Player)) {
						Player player = (Player) sender;
						String playername = player.getName();
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
					} else {
						try {
							Configuration.config.reload();
							Log.info("Config Reloaded.");
						} catch (FileNotFoundException e) {
							Log.info("Config Not Found!");
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidConfigurationException e) {
							Log.info("Config Not Valid Format!");
							e.printStackTrace();
						}						
					}
				}
			} else if (args.length == 2) {
				
				// dm <player> on
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playername = player.getName();

					if ((player.getServer().getPlayer(args[0]) != null) && (args[1].equalsIgnoreCase("on"))) {
						if (player.isOp() || player.hasPermission("doody.others")) {
							Player targetPlayer = player.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();

							if (!DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
								if (!(targetPlayer.getGameMode() == GameMode.CREATIVE)) {
									if (Configuration.config.getBoolean("Debug.enabled")) {
										Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playername + "used /doody " + targetPlayer + " on");
									}
									Inventory targetPlayerInv = targetPlayer.getInventory();
									try {
										//add player to duty list.
										addPlayer(targetPlayerName);
										//save player's xp level.
										DoOdyPlayerListener.expOrb.put(targetPlayerName, targetPlayer.getLevel());

										//save player's inventory & clear it.
										DoOdyPlayerListener.inventory.put(targetPlayerName, targetPlayerInv.getContents());
										try {
											Integer size = targetPlayerInv.getSize();
											Integer i = 0;
											for(i=0; i < size; i++) {
												ItemStack item = targetPlayerInv.getItem(i);
												if (item.getAmount() != 0) {
													Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".amount", item.getAmount());
													Short durab = item.getDurability();
													Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".durability", durab.intValue());
													Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".type", item.getTypeId());
													Configuration.inventory.save();
												}
											}
										} catch(Exception e) {
										}

										targetPlayerInv.clear();

										String worldname = targetPlayer.getLocation().getWorld().getName();
										Configuration.location.set(targetPlayerName + ".world", worldname);
										Configuration.location.set(targetPlayerName + ".x", targetPlayer.getLocation().getX());
										Configuration.location.set(targetPlayerName + ".y", targetPlayer.getLocation().getY());
										Configuration.location.set(targetPlayerName + ".z", targetPlayer.getLocation().getZ());
										Configuration.location.set(targetPlayerName + ".pitch", targetPlayer.getLocation().getPitch());
										Configuration.location.set(targetPlayerName + ".yaw", targetPlayer.getLocation().getYaw());
										Configuration.location.save();

										//save player's armour content.
										DoOdyPlayerListener.armour.put(targetPlayerName, targetPlayer.getInventory().getArmorContents());

										//put player on creative mode.
										targetPlayer.setGameMode(GameMode.CREATIVE);
										DoOdyPlayerListener.duty.put(targetPlayerName, targetPlayer.getGameMode());
										player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + targetPlayerName + " is now on Duty.");
										targetPlayer.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + playername + " put you on Duty.");

										//Give Duty Tools?
										if (Configuration.config.getBoolean("Duty Tools.enabled")) {
											targetPlayerInv.setItem(0, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 1"), 1, (short) 0));
											targetPlayerInv.setItem(1, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 2"), 1, (short) 0));
											targetPlayerInv.setItem(2, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 3"), 1, (short) 0));
											targetPlayerInv.setItem(3, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 4"), 1, (short) 0));
											targetPlayerInv.setItem(4, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 5"), 1, (short) 0));
											targetPlayerInv.setItem(5, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 6"), 1, (short) 0));
											targetPlayerInv.setItem(6, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 7"), 1, (short) 0));
											targetPlayerInv.setItem(7, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 8"), 1, (short) 0));
											targetPlayerInv.setItem(8, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 9"), 1, (short) 0));
										}
										if (Configuration.config.getBoolean("Debug.enabled")) {
											Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + targetPlayerName + "'s data added to #Maps.");
											Log.info("Debug mode. " + targetPlayerName + "'s data saved to #Maps.");
											player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + targetPlayerName + "'s data saved in #Maps.");		
										}
									} catch (Exception e) {
										targetPlayer.setGameMode(GameMode.CREATIVE);
										targetPlayer.getInventory().clear();
										Log.severe("Failed Storing #Map on /doody " + targetPlayerName + " on");
										player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Failed storing " + targetPlayerName + "'s data in #Maps.");
									}
									Log.info(playername + " put " + targetPlayerName + " on Duty.");
									return true;
								} else {
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + targetPlayerName + " must be in Survival mode first!");
								}
							} else if (DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
								player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + targetPlayerName + " is already on Duty!");
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("on")) {
					Player targetPlayer = null;
					if ((Bukkit.getServer().getPlayer(args[0]) != null)) {
						targetPlayer = Bukkit.getServer().getPlayer(args[0]);
						String targetPlayerName = targetPlayer.getName();

						if (!DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
							if (!(targetPlayer.getGameMode() == GameMode.CREATIVE)) {
								Inventory targetPlayerInv = targetPlayer.getInventory();
								try {
									//add player to duty list.
									addPlayer(targetPlayerName);
									//save player's xp level.
									DoOdyPlayerListener.expOrb.put(targetPlayerName, targetPlayer.getLevel());

									//save player's inventory & clear it.
									DoOdyPlayerListener.inventory.put(targetPlayerName, targetPlayerInv.getContents());
									try {
										Integer size = targetPlayerInv.getSize();
										Integer i = 0;
										for(i=0; i < size; i++) {
											ItemStack item = targetPlayerInv.getItem(i);
											if (item.getAmount() != 0) {
												Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".amount", item.getAmount());
												Short durab = item.getDurability();
												Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".durability", durab.intValue());
												Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".type", item.getTypeId());
												Configuration.inventory.save();
											}
										}
									} catch(Exception e) {
									}

									targetPlayerInv.clear();

									String worldname = targetPlayer.getLocation().getWorld().getName();
									Configuration.location.set(targetPlayerName + ".world", worldname);
									Configuration.location.set(targetPlayerName + ".x", targetPlayer.getLocation().getX());
									Configuration.location.set(targetPlayerName + ".y", targetPlayer.getLocation().getY());
									Configuration.location.set(targetPlayerName + ".z", targetPlayer.getLocation().getZ());
									Configuration.location.set(targetPlayerName + ".pitch", targetPlayer.getLocation().getPitch());
									Configuration.location.set(targetPlayerName + ".yaw", targetPlayer.getLocation().getYaw());
									Configuration.location.save();

									//save player's armour content.
									DoOdyPlayerListener.armour.put(targetPlayerName, targetPlayer.getInventory().getArmorContents());

									//put player on creative mode.
									targetPlayer.setGameMode(GameMode.CREATIVE);
									DoOdyPlayerListener.duty.put(targetPlayerName, targetPlayer.getGameMode());
									Log.info(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + targetPlayerName + " is now on Duty.");
									targetPlayer.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Console put you on Duty.");

									//Give Duty Tools?
									if (Configuration.config.getBoolean("Duty Tools.enabled")) {
										targetPlayerInv.setItem(0, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 1"), 1, (short) 0));
										targetPlayerInv.setItem(1, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 2"), 1, (short) 0));
										targetPlayerInv.setItem(2, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 3"), 1, (short) 0));
										targetPlayerInv.setItem(3, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 4"), 1, (short) 0));
										targetPlayerInv.setItem(4, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 5"), 1, (short) 0));
										targetPlayerInv.setItem(5, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 6"), 1, (short) 0));
										targetPlayerInv.setItem(6, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 7"), 1, (short) 0));
										targetPlayerInv.setItem(7, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 8"), 1, (short) 0));
										targetPlayerInv.setItem(8, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 9"), 1, (short) 0));
									}
								} catch (Exception e) {
									targetPlayer.setGameMode(GameMode.CREATIVE);
									targetPlayer.getInventory().clear();
									Log.severe("Failed Storing #Map on /doody " + targetPlayerName + " on");
									Log.severe("[DoOdy] Failed storing " + targetPlayerName + "'s data in #Maps.");
								}
								Log.info("Console put " + targetPlayerName + " on Duty.");
								return true;
							} else {
								Log.info("[DoOdy] " + targetPlayerName + " must be in Survival mode first!");
							}
						} else if (DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
							Log.info("[DoOdy] " + targetPlayerName + " is already on Duty!");
						}
					} else {
						Log.info("Player is not Online!");
					}
					return true;
				}
				
				// dm <player> off
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playername = player.getName();
					if ((player.getServer().getPlayer(args[0]) != null) && (args[1].equalsIgnoreCase("off"))) {
						if (player.isOp() || player.hasPermission("doody.others")) {
							Player targetPlayer = player.getServer().getPlayer(args[0]);
							String targetPlayerName = targetPlayer.getName();

							if (DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
								if (Configuration.config.getBoolean("Debug.enabled")) {
									Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playername + "used /doody " + targetPlayerName + " off");
								}
								try {
									//remove player from list of players on duty.
									myArr.removeAll(Arrays.asList(targetPlayerName));
									//resore player's gamemode & remove data from #map.
									targetPlayer.setGameMode(GameMode.SURVIVAL);
									DoOdyPlayerListener.duty.remove(targetPlayerName);
									//restore player's xp & remove data from #map.
									targetPlayer.setLevel(DoOdyPlayerListener.expOrb.get(targetPlayerName));
									DoOdyPlayerListener.expOrb.remove(targetPlayerName);

									//restore player's location & remove data from #map.
									World world = Bukkit.getServer().getWorld(Configuration.location.getString(targetPlayerName + ".world"));
									double x = Configuration.location.getDouble(targetPlayerName + ".x");
									double y = Configuration.location.getDouble(targetPlayerName + ".y");
									double z = Configuration.location.getDouble(targetPlayerName + ".z");
									double pit = Configuration.location.getDouble(targetPlayerName + ".pitch");
									double ya = Configuration.location.getDouble(targetPlayerName + ".yaw");
									float pitch = (float) pit;
									float yaw = (float) ya;

									Location local = new Location(world, x, y, z, yaw, pitch);
									targetPlayer.teleport(local);

									Configuration.location.set(targetPlayerName, null);
									Configuration.location.save();

									if (DoOdyPlayerListener.inventory.containsKey(targetPlayerName)) {
										targetPlayer.getInventory().setContents(DoOdyPlayerListener.inventory.get(targetPlayerName));
									} else {
										player.getInventory().clear();
										try {
											Integer size = targetPlayer.getInventory().getSize();
											Integer i = 0;
											for(i=0; i < size; i++) {
												ItemStack item = new ItemStack(0, 0);
												if(Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".amount", 0) !=0) {
													Integer amount = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".amount", 0);
													Integer durability = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".durability", 0);
													Integer type = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".type", 0);
													item.setAmount(amount);
													item.setTypeId(type);
													item.setDurability(Short.parseShort(durability.toString()));
													targetPlayer.getInventory().setItem(i, item);
												}
											}
										} catch(Exception e) {
											Log.severe("Failed Loading Target Player Inventory from file on /doody <targetplayer> off");
										}
									}
									Configuration.inventory.set(targetPlayerName, null);
									Configuration.inventory.save();


									//respore player's armour contents & remove data from #map.
									if (DoOdyPlayerListener.armour.containsKey(targetPlayerName)) {
										targetPlayer.getInventory().setArmorContents(DoOdyPlayerListener.armour.get(targetPlayerName));
										DoOdyPlayerListener.armour.remove(targetPlayerName);
									}
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + targetPlayerName + " is no longer on Duty.");
									targetPlayer.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + playername + " removed you from your Duties.");
									if (Configuration.config.getBoolean("Debug.enabled")) {
										Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + targetPlayerName + "'s data restored and #Maps Removed.");
										Log.info("Debug mode. " + playername + "'s data restored & #maps cleared.");
										player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + targetPlayerName + "'s data is restored & #Maps Removed.");		
									}
								} catch (Exception e) {
									myArr.removeAll(Arrays.asList(targetPlayerName));
									DoOdyPlayerListener.duty.remove(targetPlayerName);
									targetPlayer.setGameMode(GameMode.SURVIVAL);
									targetPlayer.getInventory().clear();
									Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring inventory.");
									Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring location.");
									targetPlayer.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Failed restoring Inventory. Plugin was disabled while you were on Duty.");
								}
								Log.info(playername + " removed " + targetPlayerName + " from Duty.");
								return true;
							} else if (!DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
								player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + targetPlayerName + " is not on Duty!");
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("off")){
					Player targetPlayer = null;
					if ((Bukkit.getServer().getPlayer(args[0]) != null)) {
						targetPlayer = Bukkit.getServer().getPlayer(args[0]);
						String targetPlayerName = targetPlayer.getName();

						if (DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
							try {
								//remove player from list of players on duty.
								myArr.removeAll(Arrays.asList(targetPlayerName));
								//resore player's gamemode & remove data from #map.
								targetPlayer.setGameMode(GameMode.SURVIVAL);
								DoOdyPlayerListener.duty.remove(targetPlayerName);
								//restore player's xp & remove data from #map.
								targetPlayer.setLevel(DoOdyPlayerListener.expOrb.get(targetPlayerName));
								DoOdyPlayerListener.expOrb.remove(targetPlayerName);

								//restore player's location & remove data from #map.
								World world = Bukkit.getServer().getWorld(Configuration.location.getString(targetPlayerName + ".world"));
								double x = Configuration.location.getDouble(targetPlayerName + ".x");
								double y = Configuration.location.getDouble(targetPlayerName + ".y");
								double z = Configuration.location.getDouble(targetPlayerName + ".z");
								double pit = Configuration.location.getDouble(targetPlayerName + ".pitch");
								double ya = Configuration.location.getDouble(targetPlayerName + ".yaw");
								float pitch = (float) pit;
								float yaw = (float) ya;

								Location local = new Location(world, x, y, z, yaw, pitch);
								targetPlayer.teleport(local);

								Configuration.location.set(targetPlayerName, null);
								Configuration.location.save();

								if (DoOdyPlayerListener.inventory.containsKey(targetPlayerName)) {
									targetPlayer.getInventory().setContents(DoOdyPlayerListener.inventory.get(targetPlayerName));
								} else {
									targetPlayer.getInventory().clear();
									try {
										Integer size = targetPlayer.getInventory().getSize();
										Integer i = 0;
										for(i=0; i < size; i++) {
											ItemStack item = new ItemStack(0, 0);
											if(Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".amount", 0) !=0) {
												Integer amount = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".amount", 0);
												Integer durability = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".durability", 0);
												Integer type = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".type", 0);
												item.setAmount(amount);
												item.setTypeId(type);
												item.setDurability(Short.parseShort(durability.toString()));
												targetPlayer.getInventory().setItem(i, item);
											}
										}
									} catch(Exception e) {
										Log.severe("Failed Loading Target Player Inventory from file on /doody <targetplayer> off");
									}
								}
								Configuration.inventory.set(targetPlayerName, null);
								Configuration.inventory.save();


								//respore player's armour contents & remove data from #map.
								if (DoOdyPlayerListener.armour.containsKey(targetPlayerName)) {
									targetPlayer.getInventory().setArmorContents(DoOdyPlayerListener.armour.get(targetPlayerName));
									DoOdyPlayerListener.armour.remove(targetPlayerName);
								}
								Log.info("[DoOdy] " + targetPlayerName + " is no longer on Duty.");
								targetPlayer.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Console removed you from your Duties.");
							} catch (Exception e) {
								myArr.removeAll(Arrays.asList(targetPlayerName));
								DoOdyPlayerListener.duty.remove(targetPlayerName);
								targetPlayer.setGameMode(GameMode.SURVIVAL);
								targetPlayer.getInventory().clear();
								Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring inventory.");
								Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring location.");
								targetPlayer.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Failed restoring Inventory. Plugin was disabled while you were on Duty.");
							}
							Log.info("[DoOdy] Console removed " + targetPlayerName + " from Duty.");
							return true;
						} else if (!DoOdyPlayerListener.duty.containsKey(targetPlayerName)) {
							Log.info("[DoOdy] " + targetPlayerName + " is not on Duty!");
						}
					}						
					return true;
				}

				if ((args[0].equalsIgnoreCase("debug")) && (args[1].equalsIgnoreCase("on"))) {
					if ((sender instanceof Player)) {
						Player player = (Player) sender;
						String playername = player.getName();
						if (Configuration.config.getBoolean("Debug.enabled") == false) {
							if (player.isOp() || player.hasPermission("doody.debug")) {
								try {
									Configuration.config.set("Debug.enabled", true);
									Configuration.config.save();
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Debug Mode Enabled!");
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Debug messages are output to Server Console/Log.");
									Configuration.config.reload();
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Disable Debug Mode with /doody debug off");
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
						} else {
							player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Debug Mode is already on!");						
						}
						return true;
					} else {
						Log.info("This command hasn't been ported yet.");
						return true;
					}
				}
				if ((args[0].equalsIgnoreCase("debug")) && (args[1].equalsIgnoreCase("off"))) {
					if ((sender instanceof Player)) {
						Player player = (Player) sender;
						String playername = player.getName();
						if (Configuration.config.getBoolean("Debug.enabled") == true) {
							if (player.isOp() || player.hasPermission("doody.debug")) {
								try {
									Configuration.config.set("Debug.enabled", false);
									Configuration.config.save();
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Debug Mode Disabled!.");
									Configuration.config.reload();
									player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.GREEN + "Hope debugging shed some light on any issues with DoOdy.");
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
						} else {
							player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "Debug Mode is already off!");						
						}
						return true;
					} else {
						Log.info("This command hasn't been ported yet.");
						return true;
					}
				}
			}
		return false;
	}
}
