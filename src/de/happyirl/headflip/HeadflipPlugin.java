package de.happyirl.headflip;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.happyirl.headflip.commands.HeadflipCommand;
import de.happyirl.headflip.commands.HeadflipsHandler;

public class HeadflipPlugin extends JavaPlugin
{
	private final String Headflip_Command = "headflip";
	private FileConfiguration headflipRatios;
	private File file;
	
	public void onLoad()
	{
		file = new File(getDataFolder(), "headflipRatios.yml");
		if (!file.exists()) 
		{
		    try 
		    {
				file.createNewFile();
			} 
		    catch (IOException e) 
		    {
				e.printStackTrace();
			}
		}
		headflipRatios = YamlConfiguration.loadConfiguration(file);
	}
	
	public void onEnable()
	{
		saveDefaultConfig();
		
		HeadflipsHandler headflipHandler = new HeadflipsHandler(this);
		
		getCommand(Headflip_Command).setExecutor(new HeadflipCommand(headflipHandler, this));

		addListener(headflipHandler);
	}
	
	public FileConfiguration getHeadflipRatio()
	{
		return headflipRatios;
	}
	public void onDisable()
	{
		saveHeadflipRatio();
	}
	public void saveHeadflipRatio()
	{
		try 
		{
			headflipRatios.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void addListener(Listener listener)
	{
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
}
