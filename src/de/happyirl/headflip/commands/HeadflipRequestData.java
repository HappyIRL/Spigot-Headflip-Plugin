package de.happyirl.headflip.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import de.happyirl.headflip.Main;

public class HeadflipRequestData 
{
	public UUID source;
	public UUID target;
	BukkitTask removeHeadflipDataTask;
	private final String requestTimeout = "headflip.requestTimeout";
	
	public HeadflipRequestData(UUID source, UUID target, HeadflipsHandler headflipsHandler, Main main)
	{
		this.source = source;
		this.target = target;
		
		removeHeadflipDataTask = Bukkit.getScheduler().runTaskLater(main, new Runnable()
		{
			@Override
			public void run()
			{
				HeadflipRequestData currentHeadflip = headflipsHandler.findHeadflip(source);
				
				if(currentHeadflip != null)
				{
					headflipsHandler.RemoveAllHeadflipData(currentHeadflip);
					Bukkit.getPlayer(source).sendMessage(main.config.getString(requestTimeout));
				}
			}
			
		},600);
	}
	
}
