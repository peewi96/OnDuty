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

import me.angelofdev.DoOdy.config.Configuration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DoOdyEntityListener implements Listener {

	public DoOdyEntityListener() {
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player attacker = (Player) event.getDamager();
			String attackername = attacker.getName();
			
			if(DoOdyPlayerListener.duty.containsKey(attackername)) {
				if ((Configuration.config.getBoolean("Duty Deny PVP.enabled", true) && !attacker.hasPermission("doody.pvp"))) {
					if (!(event.getEntity() instanceof Player)) {
						if (Configuration.config.getBoolean("Debug.enabled", true)) {
							Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Defender is not Player allowing.");
						}
						return;
					}
					event.setCancelled(true);
				}
			} else {
				return;
			}
		}
	}
}
