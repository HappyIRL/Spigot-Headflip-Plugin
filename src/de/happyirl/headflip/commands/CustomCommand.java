package de.happyirl.headflip.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;


public class CustomCommand implements CommandExecutor
{
	protected String permission = "";
	protected boolean playerOnly;
	protected int parameters;
	protected FileConfiguration config;
	
	private final String noPermission = "headflip.noPermission";
	private final String toManyParameters = "headflip.toManyParameters";
	private final String nonPlayer = "headflip.nonPlayer";
	
	public CustomCommand(FileConfiguration config)
	{
		this.config = config;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{			
		if(!sender.hasPermission(permission))
		{
			sender.sendMessage("§e" + sender.getName() + config.get(noPermission));
			return false;
		}
		if(args.length != parameters)
		{
			sender.sendMessage("§e" + sender.getName() + config.get(toManyParameters));
			return false;
		}
		if(playerOnly && !(sender instanceof Player))
		{
			sender.sendMessage("§e" + sender.getName() + config.get(nonPlayer));
			return false;
		}
		if(playerOnly)
			execute((Player)sender, args);
		else
			execute(sender, args);
		return true;
		
	}
	public void execute(CommandSender sender, String[] args)
	{
		
	}
	public void execute(Player player, String[] args)
	{
		
	}
}
