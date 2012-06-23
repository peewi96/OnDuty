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

package me.angelofdev.DoOdy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.angelofdev.DoOdy.command.DoOdyCommandExecutor;
import me.angelofdev.DoOdy.config.Configuration;
import me.angelofdev.DoOdy.listeners.DoOdyBlockListener;
import me.angelofdev.DoOdy.listeners.DoOdyEntityListener;
import me.angelofdev.DoOdy.listeners.DoOdyPlayerListener;
import me.angelofdev.DoOdy.listeners.DoOdyPlayerListener.SLAPI;

import org.bukkit.GameMode;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DoOdy extends JavaPlugin {
	private DoOdyPlayerListener playerListener;
	private DoOdyBlockListener blockListener;
	private DoOdyEntityListener entityListener;
	private DoOdyCommandExecutor DoOdyCommandExecutor;
	private static String version;
	private static final String PLUGIN_NAME = "DoOdy";	

	public static DoOdy instance;
	
	@Override
	public void onDisable() {
		try {
			SLAPI.save(me.angelofdev.DoOdy.command.DoOdyCommandExecutor.myArr, "plugins/DoOdy/myArr.bin");
			Log.info("Saved list of players on duty.");
			SLAPI.save(DoOdyPlayerListener.duty, "plugins/DoOdy/duty.bin");
			Log.info("Saved GameMode of players on duty.");
			SLAPI.save(DoOdyPlayerListener.expOrb, "plugins/DoOdy/exp.bin");
			Log.info("Saved Exeprience Data of players on duty.");
		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
		Log.info(PLUGIN_NAME + "disabled!");
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		version = pdfFile.getVersion();
		initialise();
		initMetrics();
		
		//Load saved info.
		try {
			me.angelofdev.DoOdy.command.DoOdyCommandExecutor.myArr = (ArrayList<String>)SLAPI.load("plugins/DoOdy/myArr.bin");
			Log.info("Loaded list of players on duty.");
			DoOdyPlayerListener.duty = (HashMap<String, GameMode>)SLAPI.load("plugins/DoOdy/duty.bin");
			Log.info("Loaded GameModes of players on duty.");
			DoOdyPlayerListener.expOrb = (HashMap<String, Integer>)SLAPI.load("plugins/DoOdy/exp.bin");
			Log.info("Loaded Exeprience Data of players on duty.");
		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
		Log.info("Loading configs...");
		Configuration.start();
		Log.info("loaded configs!");
		Log.info(PLUGIN_NAME + " v" + version + " enabled");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.entityListener, this);
		DoOdyCommandExecutor = new DoOdyCommandExecutor(this);
		getCommand("doody").setExecutor(DoOdyCommandExecutor);
	}	

	public static String getPluginName() {
		return PLUGIN_NAME;
	}
	
	@Override
	public String toString() {
		return getPluginName();
	}
	
	private void initialise() {
		playerListener = new DoOdyPlayerListener();
		blockListener = new DoOdyBlockListener();
		entityListener = new DoOdyEntityListener();
		instance = this;
		
	}
	
	private void initMetrics() {
		try {
		    MetricsLite metrics = new MetricsLite(instance);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
	}	
}
