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

import java.util.List;

import me.angelofdev.DoOdy.config.Configuration;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class DoOdyBlockListener implements Listener {

	public DoOdyBlockListener() {
	}

	List<Integer> configBlocksPlaceDenied = Configuration.config.getIntegerList("Denied Blocks.Place");
	List<Integer> configBlocksBreakDenied = Configuration.config.getIntegerList("Denied Blocks.Break");
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		
		Block block = event.getBlock();
		String message = block.getType().name();
		String blockname = message.toLowerCase();
		int blockID = block.getTypeId();
		
		if (DoOdyPlayerListener.duty.containsKey(playername)) {
			if (!(player.isOp() || player.hasPermission("doody.allowplace"))) {
				if (configBlocksPlaceDenied.contains(blockID)) {
					if ((DoOdyPlayerListener.configLbTools.contains(blockID) && Configuration.config.getBoolean("Allow.LogBlock.enabled", true)) || (DoOdyPlayerListener.configWeTools.contains(blockID) && Configuration.config.getBoolean("Allow.WorldEdit.enabled", true))) {
						return;
					} else {
						event.setCancelled(true);
						if (Configuration.config.getBoolean("Denied Blocks.messages", true)) {
							player.sendMessage(ChatColor.RED + "There's no need to place " + ChatColor.YELLOW + blockname + ChatColor.RED + " while on Duty.");
						}
					}
				}
			}
		}		
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
		
		Block block = event.getBlock();
		String message = block.getType().name();
		String blockname = message.toLowerCase();
		int blockID = block.getTypeId();
		
		if (DoOdyPlayerListener.duty.containsKey(playername)) {
			if (!(player.isOp() || player.hasPermission("doody.allowbreak"))) {
				if (configBlocksBreakDenied.contains(blockID)) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Denied Blocks.messages", true)) {
						player.sendMessage(ChatColor.RED + "There's no need to break " + ChatColor.YELLOW + blockname + ChatColor.RED + " while on Duty.");
					}
				}
			}
		}		
	}	
}
