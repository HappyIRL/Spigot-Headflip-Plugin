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
	
	private static final String NO_PERMISSION = "headflip.noPermission";
	private static final String TO_MANY_PARAMETERS = "headflip.toManyParameters";
	private static final String NON_PLAYER = "headflip.nonPlayer";
	
	public CustomCommand(FileConfiguration config)
	{
		this.config = config;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{			
		if(!sender.hasPermission(permission))
		{
			sender.sendMessage("§e" + sender.getName() + config.get(NO_PERMISSION));
			return false;
		}
		if(args.length != parameters)
		{
			sender.sendMessage("§e" + sender.getName() + config.get(TO_MANY_PARAMETERS));
			return false;
		}
		if(playerOnly && !(sender instanceof Player))
		{
			sender.sendMessage("§e" + sender.getName() + config.get(NON_PLAYER));
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
