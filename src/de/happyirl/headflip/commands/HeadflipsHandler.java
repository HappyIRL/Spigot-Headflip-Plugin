package de.happyirl.headflip.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.happyirl.headflip.HeadflipPlugin;

public class HeadflipsHandler implements Listener
{
	private List<HeadflipRequestData> headflips;
	private Map<UUID, List<ItemStack>> headflipPlayerStorage;
	private HeadflipPlugin plugin;
	
	private final String noRequest;
	private final String notFound;
	private final String noSpace;
	private final String hasCollected;
	private final String requestTimeout;
	
	public HeadflipsHandler(HeadflipPlugin plugin)
	{
		this.plugin = plugin;
		FileConfiguration config = plugin.getConfig();
		this.noRequest = config.getString("headflip.noRequest");
		this.notFound = config.getString("headflip.notFound");
		this.noSpace = config.getString("headflip.noSpace");
		this.hasCollected = config.getString("headflip.hasCollected");
		this.requestTimeout = config.getString("headflip.requestTimeout");
		
		headflipPlayerStorage = new HashMap<UUID, List<ItemStack>>();
		headflips = new ArrayList<HeadflipRequestData>();
	}
	
	public boolean playerStorageContains(UUID id) 
	{
		return headflipPlayerStorage.containsKey(id);
	}
	
	public void storeItems(UUID user, List<ItemStack> cache)
	{
		headflipPlayerStorage.put(user, cache);
	}
	
	public void addHeadflipRequest(Player source, Player target) 
	{
		UUID targetUUID = target.getUniqueId();
		UUID sourceUUID = source.getUniqueId();
		
		headflips.add(new HeadflipRequestData(sourceUUID, targetUUID));
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				HeadflipRequestData currentHeadflip = findHeadflipCreatedBy(sourceUUID);
				
				if(currentHeadflip != null)
				{
					removeRequest(currentHeadflip);
					Bukkit.getPlayer(sourceUUID).sendMessage(requestTimeout);
				}
			}
			
		},600);
	}

	public void removeRequest(HeadflipRequestData headflipRequestData)
	{
		headflips.remove(headflipRequestData);
	}
	
	public HeadflipRequestData findHeadflipFor(UUID playerUUID)
	{
		for(HeadflipRequestData headflipRequestData : headflips)
		{
			if(headflipRequestData.target.equals(playerUUID))
			{
				return headflipRequestData;
			}
		}
		return null;
	}
	
	public HeadflipRequestData findHeadflipCreatedBy(UUID playerUUID)
	{
		for(HeadflipRequestData headflipRequestData : headflips)
		{
			if(headflipRequestData.source.equals(playerUUID))
			{
				return headflipRequestData;
			}
		}
		return null;
	}
	
	public void tryAcceptHeadflip(Player accepter)
	{
		UUID accepterUUID = accepter.getUniqueId();
		HeadflipRequestData currentHeadflip = findHeadflipFor(accepterUUID);
		
		if(currentHeadflip == null)
		{
			accepter.sendMessage("§e" + accepter.getName() + noRequest);
		}
		else if(accepterUUID.equals(currentHeadflip.source))
		{
			return;
		}
		else
		{
			Player source = Bukkit.getPlayer(currentHeadflip.source);
			if(source != null)
			{
				Headflip headflip = new Headflip(this,accepterUUID, currentHeadflip.source, plugin);
				plugin.addListener(headflip);
			}
			else
			{
				accepter.sendMessage(notFound);
			}
			removeRequest(currentHeadflip);
		}
		
	}
	
	public void tryCollectHeadflip(Player source)
	{
		UUID playerUUID = source.getUniqueId();
		int i = 0;
		for(ItemStack item : source.getInventory().getContents())
		{
			if(item == null)
			{
				i++;
			}
		}
		if(i >= headflipPlayerStorage.get(playerUUID).size())
		{
			for(ItemStack item : headflipPlayerStorage.get(playerUUID))
				source.getInventory().addItem(item);
			source.sendMessage(hasCollected);
			headflipPlayerStorage.remove(playerUUID);
		}
		else
		{
			source.sendMessage(noSpace);
		}
	}
}
