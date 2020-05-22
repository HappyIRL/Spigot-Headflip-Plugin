package de.happyirl.headflip;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.happyirl.headflip.commands.HeadflipCommand;
import de.happyirl.headflip.commands.HeadflipsHandler;

public class HeadflipPlugin extends JavaPlugin
{
	private final String Headflip_Command = "headflip";
	private FileConfiguration config;
	
	public void onEnable()
	{
		saveDefaultConfig();
		config = getConfig();
		
		HeadflipsHandler headflipHandler = new HeadflipsHandler(this);
		
		getCommand(Headflip_Command).setExecutor(new HeadflipCommand(headflipHandler, config));

		addListener(headflipHandler);
		
	}
	
	public void addListener(Listener listener)
	{
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
}
