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

package me.angelofdev.DoOdy.config;

import java.util.Arrays;

public class Config {

	public static void set(){
		if(!Configuration.config.contains("Duty Deny Drops.enabled")) {
			Configuration.config.set("Duty Deny Drops.enabled", true);
		}
		if(!Configuration.config.contains("Duty Deny Drops.whitelist")) {
			Configuration.config.set("Duty Deny Drops.whitelist", Arrays.asList(
					"#1",
					"#3"));
		}
		if(!Configuration.config.contains("Duty Deny Drops.messages")) {
			Configuration.config.set("Duty Deny Drops.messages", true);
		}
		if(!Configuration.config.contains("Deny Storage.enabled")) {
			Configuration.config.set("Deny Storage.enabled", true);
		}
		if(!Configuration.config.contains("Deny Storage.messages")) {
			Configuration.config.set("Deny Storage.messages", true);
		}
		if(!Configuration.config.contains("Allow.LogBlock.enabled")) {
			Configuration.config.set("Allow.LogBlock.enabled", true);
		}
		if(!Configuration.config.contains("Allow.LogBlock.Tools")) {
			Configuration.config.set("Allow.LogBlock.Pickaxe", Arrays.asList(
					"270",
					"7"));
		}
		if(!Configuration.config.contains("Allow.WorldEdit.enabled")) {
			Configuration.config.set("Allow.WorldEdit.enabled", true);
		}
		if(!Configuration.config.contains("Allow.WorldEdit.Tools")) {
			Configuration.config.set("Allow.WorldEdit.Tools", Arrays.asList(
					"271"));
		}
		if(!Configuration.config.contains("Deny Storage.storage")) {
			Configuration.config.set("Deny Storage.storage", Arrays.asList(
					"23",
					"54",
					"61",
					"62",
					"95"));
		}
		if(!Configuration.config.contains("Denied Blocks.messages")) {
			Configuration.config.set("Denied Blocks.messages", true);
		}
		if(!Configuration.config.contains("Denied Blocks.Place")) {
			Configuration.config.set("Denied Blocks.Place", Arrays.asList(
					"46",
					"7"));
		}
		if(!Configuration.config.contains("Denied Blocks.Break")) {
			Configuration.config.set("Denied Blocks.Break", Arrays.asList(
					"7"));
		}
		if(!Configuration.config.contains("Debug.enabled")) {
			Configuration.config.set("Debug.enabled", false);
		}
	}
}

