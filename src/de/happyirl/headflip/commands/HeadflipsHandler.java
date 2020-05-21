package de.happyirl.headflip.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.happyirl.headflip.Main;

public class HeadflipsHandler implements Listener
{
	private List<HeadflipRequestData> headflips;
	public Map<UUID, List<ItemStack>> headflipPlayerStorage;
	private Main main;
	
	private final String noRequest;
	private final String notFound;
	private final String noSpace;
	private final String hasCollected;
	
	public HeadflipsHandler(Main main)
	{
		this.main = main;
		this.noRequest = main.config.getString("headflip.noRequest");
		this.notFound = main.config.getString("headflip.notFound");
		this.noSpace = main.config.getString("headflip.noSpace");
		this.hasCollected = main.config.getString("headflip.hasCollected");
		headflipPlayerStorage = new HashMap<UUID, List<ItemStack>>();
		headflips = new ArrayList<HeadflipRequestData>();
	}
	public void addCollectionItems(UUID user, List<ItemStack> cache)
	{
		headflipPlayerStorage.put(user, cache);
	}
	public void newHeadflipRequest(Player source, Player target) 
	{
		UUID targetUUID = target.getUniqueId();
		UUID sourceUUID = source.getUniqueId();
		
		headflips.add(new HeadflipRequestData(sourceUUID, targetUUID, this, main));
	}
	public void RemoveAllHeadflipData(HeadflipRequestData headflipRequestData)
	{
		headflips.remove(headflipRequestData);
	}
	
	public HeadflipRequestData findHeadflip(UUID playerUUID)
	{
		for(HeadflipRequestData headflipRequestData : headflips)
		{
			if(headflipRequestData.target.equals(playerUUID) || headflipRequestData.source.equals(playerUUID))
			{
				return headflipRequestData;
			}
		}
		return null;
	}
	public void tryAcceptHeadflip(Player accepter)
	{
		UUID accepterUUID = accepter.getUniqueId();
		HeadflipRequestData currentHeadflip = findHeadflip(accepterUUID);
		
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
				Headflip headflip = new Headflip(this,accepterUUID, currentHeadflip.source, main);
				main.addListener(headflip);
			}
			else
			{
				accepter.sendMessage(notFound);
			}
			RemoveAllHeadflipData(currentHeadflip);
		}
		
	}
	public void collectHeadflip(Player source)
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
