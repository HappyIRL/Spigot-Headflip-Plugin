package de.happyirl.headflip.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
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
		Date date = new Date();
		UUID targetUUID = target.getUniqueId();
		UUID sourceUUID = source.getUniqueId();
		
		RemoveAllHeadflipData(sourceUUID);
		
		
		headflips.add(new HeadflipRequestData(sourceUUID, targetUUID, date));
		
		
	}
	private void RemoveAllHeadflipData(UUID sourceUUID)
	{
		for(int i = 0; i < headflips.size(); i++)
		{
			HeadflipRequestData current = headflips.get(i);
			if(current.source.equals(sourceUUID)) 
			{
				headflips.remove(i);
				i--;
			}
		}
	}
	
	private HeadflipRequestData findHeadflip(UUID accepterUUID)
	{
		for(int i = 0; i < headflips.size(); i++)
		{
			HeadflipRequestData current = headflips.get(i);
			Date ticketExpired = DateUtils.addMinutes(new Date(), -30);
			if(current.date.after(ticketExpired))
			{
				if(current.target.equals(accepterUUID))
				{
					headflips.remove(i);
					return current;
				}
			}
			else
			{
				headflips.remove(i);
				i--;
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
		else
		{
			Player source = Bukkit.getPlayer(currentHeadflip.source);
			if(accepter != null && source != null)
			{
				Headflip headflip = new Headflip(this,accepterUUID, currentHeadflip.source, main);
				main.addListener(headflip);
			}
			else
			{
				accepter.sendMessage(notFound);
			}
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
