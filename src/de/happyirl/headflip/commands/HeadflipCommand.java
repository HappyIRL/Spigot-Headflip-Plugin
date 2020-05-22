package de.happyirl.headflip.commands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class HeadflipCommand extends CustomCommand
{
	private HeadflipsHandler headflips;
	
	private final String mustCollect;
	private final String noRequest;
	private final String headflipDenied;
	private final String collectAir;
	private final String headflipSelf;
	private final String headflipSent;
	private final String headflipRequest;
	private final String notFound;
	
	public HeadflipCommand(HeadflipsHandler headflips, FileConfiguration config) 
    {
        super(config);
        this.headflips = headflips;
        this.mustCollect = config.getString("headflip.mustCollect");
        this.noRequest = config.getString("headflip.noRequest");
        this.headflipDenied = config.getString("headflip.headflipDenied");
        this.collectAir = config.getString("headflip.collectAir");
        this.headflipSelf = config.getString("headflip.headflipSelf");
        this.headflipSent = config.getString("headflip.headflipSent");
        this.headflipRequest = config.getString("headflip.headflipRequest");
        this.notFound = config.getString("headflip.notFound");
        
        permission = config.getString("headflip.permission");
        playerOnly = true;
        parameters = 1;
    }
	
	@Override
	public void execute(Player source, String[] args)
	{
		String arg = args[0];
		
		switch(arg) 
		{
			case "accept":
				executeAccept(source);
				break;
				
			case "deny":
				executeDeny(source);
				break;
			
			case "collect":
				executeCollect(source);
				break;
			
			default:
				executePlayer(source, arg);
				break;
		}

	}

	private void executePlayer(Player source, String arg) 
	{
		Player target = Bukkit.getPlayer(arg);
		if(source.equals(target))
		{
			source.sendMessage(headflipSelf);
			return;
		}
		
		if(headflips.playerStorageContains(source.getUniqueId()))
		{
			source.sendMessage(mustCollect);
			return;
		}
		
		if (target != null)
		{
			HeadflipRequestData currentHeadflip = headflips.findHeadflipCreatedBy(source.getUniqueId());
			if(currentHeadflip == null)
			{
				headflips.addHeadflipRequest(source, target);
				source.sendMessage(headflipSent + "§e" + target.getName());
				target.sendMessage("§e" + source.getName() + headflipRequest);
			}
			//Tell player he can only do 1 headflip request at the time
		}
		else
		{
			source.sendMessage(notFound);
		}
	}

	private void executeCollect(Player source) 
	{
		if(headflips.playerStorageContains(source.getUniqueId()))
		{
			headflips.tryCollectHeadflip(source);
		}
		else
		{
			source.sendMessage(collectAir);
		}
	}

	private void executeDeny(Player source) 
	{
		HeadflipRequestData currentHeadflip = headflips.findHeadflipFor(source.getUniqueId());
		if(currentHeadflip != null)
		{
			headflips.removeRequest(currentHeadflip);
			source.sendMessage("§e" + source.getName() + headflipDenied);
		}
		else
		{
			source.sendMessage("§e" + source.getName() + noRequest);
		}
	}

	private void executeAccept(Player source) 
	{
		if(!headflips.playerStorageContains(source.getUniqueId()))
		{
			headflips.tryAcceptHeadflip(source);
		}
		else
		{
			source.sendMessage(mustCollect);
		}
	}
}
