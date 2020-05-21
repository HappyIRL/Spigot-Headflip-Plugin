package de.happyirl.headflip;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.happyirl.headflip.commands.CustomCommand;
import de.happyirl.headflip.commands.HeadflipCommand;
import de.happyirl.headflip.commands.HeadflipsHandler;

public class Main extends JavaPlugin
{
	private final String Headflip_Command = "headflip";
	public FileConfiguration config;
	
	public void onEnable()
	{
		saveDefaultConfig();
		config = getConfig();
		
		HeadflipsHandler headflips = new HeadflipsHandler(this);
		new CustomCommand(config);
		
		getCommand(Headflip_Command).setExecutor(new HeadflipCommand(headflips, config));

		addListener(headflips);
		
	}
	public void addListener(Listener listener)
	{
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
}
