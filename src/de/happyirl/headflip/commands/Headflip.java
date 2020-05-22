package de.happyirl.headflip.commands;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.happyirl.headflip.HeadflipPlugin;
import de.happyirl.headflip.HeadflipAnimationRunnable;
import net.minecraft.server.v1_8_R3.MojangsonParseException;


public class Headflip implements Listener
{
	private ItemStack redGlass;
	private ItemStack greenGlass;
	private ItemStack greyGlass;
	private ItemStack lightBlueGlass;
	private ItemStack blueGlass;
	private ItemStack playerSkull1;
	private ItemStack playerSkull2;
	private ItemStack[] inventoryContent;
	ItemStack[] animation1Content;
	ItemStack[] animation2Content;
	
	private UUID player1UUID;
	private UUID player2UUID;
	private Player player1;
	private Player player2;
	
	private Inventory headflipInventory;
	private Inventory headflipAnimationInventory;
	
	private boolean ReadyPlayer1 = false;
	private boolean ReadyPlayer2 = false;
	private boolean canBeCancelled = true;
	private boolean startedHeadflip = false;
	private boolean finalClose = false;
	
	private final int[] player1Slots = {9,10,11,12,18,19,20,21};
	private final int[] player2Slots = {14,15,16,17,23,24,25,26};
	private final String URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private final String noWager;
	private final String headflipInventoryName;
	private final String headflipAnimationName;
	private final String cancelHeadflip;
	
	
	private HeadflipsHandler headflipsHandler;
	private HeadflipPlugin main;
	private Headflip headflip;
	
	private List<BukkitTask> openInventoryTasks = new ArrayList<BukkitTask>();
	
	private final String interactionInventoryMap =
			  "400000005"
			+ "111102222"
			+ "111102222"
			+ "000000000";
	
	private enum InventoryInteraction
	{
		CANCEL,
		GRAB_P1,
		GRAB_P2,
		INPUT,
		CONFIRM_P1,
		CONFIRM_P2
	}
	
	public Headflip(HeadflipsHandler headflipsHandler, UUID player1, UUID player2, HeadflipPlugin main)
	{
		FileConfiguration config = main.getConfig();
		
		this.cancelHeadflip = config.getString("headflip.cancelledHeadflip");
		this.headflipInventoryName = config.getString("headflip.headflipInventoryName");
		this.headflipAnimationName = config.getString("headflip.headflipAnimationName");
		this.noWager = config.getString("headflip.noWager");
		this.main = main;
		
		this.headflipsHandler = headflipsHandler;
		this.player1UUID = player1;
		this.player2UUID = player2;
		headflip = this;
		this.player1 = Bukkit.getPlayer(this.player1UUID);
		this.player2 = Bukkit.getPlayer(this.player2UUID);
		
		//Get right player skulls
		String player1Texture = getPlayerSkinTexture(player1);
		String player2Texture = getPlayerSkinTexture(player2);
		playerSkull1 = setDisplayName(createPlayerSkull(player1, player1Texture), this.player1);
		playerSkull2 = setDisplayName(createPlayerSkull(player2, player2Texture), this.player2);
		
		//Setup Inventory
		createInventoryContent();
		headflipInventory = createInventory(36, headflipInventoryName, inventoryContent);
		this.player1.openInventory(headflipInventory);
		this.player2.openInventory(headflipInventory);
	}
	
	private void createInventoryContent() 
	{
		greenGlass = createColoredItem(Material.STAINED_GLASS_PANE,1,5);
		greyGlass = createColoredItem(Material.STAINED_GLASS_PANE,1,7);
		redGlass = createColoredItem(Material.STAINED_GLASS_PANE,1,14);
		lightBlueGlass = createColoredItem(Material.STAINED_GLASS_PANE,1,3);
		blueGlass = createColoredItem(Material.STAINED_GLASS_PANE,1,11);
		
		inventoryContent = new ItemStack[]
				{redGlass,playerSkull1,greyGlass,greyGlass,greyGlass,greyGlass,greyGlass,playerSkull2,redGlass,
				null,null,null,null, greyGlass, null, null, null,null,
				null,null,null,null, greyGlass, null, null, null,null,
				greyGlass,greyGlass,greyGlass,greyGlass,greyGlass,greyGlass,greyGlass,greyGlass,greyGlass};
	}
	
	private ItemStack setDisplayName(ItemStack item, Player player)
	{
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName("§a" + player.getName().toString());
		item.setItemMeta(itemMeta);
		return item;
	}
	
	private String getPlayerSkinTexture(UUID uuid) 
	{
		InputStreamReader inputReader = null;
		  try 
		  {
			  inputReader = new InputStreamReader(new URL(URL + uuid.toString().replace("-","") + "?unsigned=false").openStream());
		  } 
		  catch (Exception e) 
		  {
		    e.printStackTrace();
		  }
		  
		  if (inputReader == null) 
			  return null;
		  
		  JsonObject texturePropertyPlayer = new JsonParser().parse(inputReader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
		  try 
		  {
			inputReader.close();
		  } 
		  catch (IOException e) 
		  {
			e.printStackTrace();
		  }
		  return texturePropertyPlayer.get("value").getAsString();
	}
	
	private ItemStack createColoredItem(Material material, int amount, int colorCode)
	{
		ItemStack item = new ItemStack(material,amount,(byte)colorCode);
		return item;
	}
	
	private ItemStack createPlayerSkull(UUID player, String texture)
	{
		ItemStack itemStack = new ItemStack(Material.SKULL_ITEM,1,(short)SkullType.PLAYER.ordinal());
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy(itemStack);
		try 
		{
			nmsItem.setTag(net.minecraft.server.v1_8_R3.MojangsonParser.parse("{SkullOwner:{Id:\"" + player.toString() + "\",Properties:{textures:[{Value:\"" + texture + "\"}]}}}"));
	
		} catch (MojangsonParseException e) {
			e.printStackTrace();
		}
		return org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asBukkitCopy(nmsItem);
	}
	
	private Inventory createInventory(int inventorySize,String inventoryName,ItemStack[] inventoryContent)
	{
		Inventory inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
		inventory.setContents(inventoryContent);
		return inventory;
	}
	
	private InventoryInteraction getInteractionFromSlot(int i)
	{
		if(i < 0)
			return InventoryInteraction.CANCEL;
		
		if(i < 36)
		{
			char x = interactionInventoryMap.charAt(i);
			return (InventoryInteraction.values()[Integer.parseInt(x + "")]);
		}
		else
			return InventoryInteraction.INPUT;
	}
	
	public int getEmptyHfSlot(UUID player)
	{
		if(player.equals(player1UUID))
		{
			for(int i : player1Slots) 
			{
				if(headflipInventory.getItem(i) == null)
				{
					return i;
				}
			}
		}
		else
		{
			for(int i : player2Slots) 
			{
				if(headflipInventory.getItem(i) == null)
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	public void finishHeadflip()
	{
		startedHeadflip = false;
		Bukkit.getScheduler().runTaskLater(main, new Runnable()
		{
			@Override
			public void run()
			{
				player1.closeInventory();
				player2.closeInventory();
			}
				
		},20);
	}
	
	private void handleInteraction(InventoryInteraction interaction, InventoryClickEvent event)
	{
		HumanEntity clicker = event.getWhoClicked();
		UUID clickerUUID = clicker.getUniqueId();
		switch(interaction)
		{
			
			case GRAB_P1:
				if(clickerUUID.equals(player1UUID))
				{
					grabInteraction(event, clicker);
				}
				break;
				
			case GRAB_P2:
				if(clickerUUID.equals(player2UUID))
				{
					grabInteraction(event, clicker);
				}
				break;
				
			case INPUT:
				inputInteraction(event, clicker, clickerUUID);
				break;
				
			case CONFIRM_P1:
				confirmP1Interaction(clickerUUID);
				break;
				
			case CONFIRM_P2:
				confirmP2Interaction(clickerUUID);
				break;
				
			default:
				break;
		}	
	}

	private void confirmP1Interaction(UUID clickerUUID) {
		if(clickerUUID.equals(player1UUID))
		{
			ReadyPlayer1 = !ReadyPlayer1;
			if(ReadyPlayer1)
			{
				headflipInventory.setItem(0, greenGlass);
			}
			else 
			{
				headflipInventory.setItem(0, redGlass);
			}
			player1.updateInventory();
			player2.updateInventory();
			tryStartHeadflip();

		}
	}

	private void confirmP2Interaction(UUID clickerUUID) {
		if(clickerUUID.equals(player2UUID))
		{
			ReadyPlayer2 = !ReadyPlayer2;
			if(ReadyPlayer2)
			{
				headflipInventory.setItem(8, greenGlass);
			}
			else 
			{
				headflipInventory.setItem(8, redGlass);
			}
			player1.updateInventory();
			player2.updateInventory();
			tryStartHeadflip();
		}
	}

	private void inputInteraction(InventoryClickEvent event, HumanEntity clicker, UUID clickerUUID) {
		int index = getEmptyHfSlot(clickerUUID);
		if(index >= 0)
		{
			event.getInventory().setItem(index,event.getCurrentItem());
			clicker.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
			ReadyPlayer2 = false;
			ReadyPlayer1 = false;
			headflipInventory.setItem(8, redGlass);
			headflipInventory.setItem(0, redGlass);
			player1.updateInventory();
			player2.updateInventory();
		}
	}

	private void grabInteraction(InventoryClickEvent event, HumanEntity clicker) 
	{
		int index = clicker.getInventory().firstEmpty();
		if(index >= 0)
		{
			clicker.getInventory().addItem(event.getCurrentItem());
			event.getInventory().setItem(event.getSlot(), null);
			ReadyPlayer2 = false;
			ReadyPlayer1 = false;
			headflipInventory.setItem(8, redGlass);
			headflipInventory.setItem(0, redGlass);
			player1.updateInventory();
			player2.updateInventory();
		}
	}
	
	private void tryStartHeadflip()
	{
		if(ReadyPlayer1 && ReadyPlayer2)
		{
			List<ItemStack> items = new ArrayList<ItemStack>();
			for(int i = 0; i < player1Slots.length; i++)
			{
				ItemStack itemP1 = headflipInventory.getItem(player1Slots[i]);
				ItemStack itemP2 = headflipInventory.getItem(player2Slots[i]);
				if(itemP1 != null)
				{
					items.add(itemP1);
				}
				if(itemP2 != null)
				{
					items.add(itemP2);
				}
			}
			if(items.size() > 0)
			{
				startHeadflip(items);
			}
			else
			{
				player1.sendMessage(noWager);
				player2.sendMessage(noWager);
				items = new ArrayList<ItemStack>();
			}
		}
	}
	
	private void startHeadflip(List<ItemStack> items)
	{
		canBeCancelled = false;
		startedHeadflip = true;
		
		Random random = new Random();
		boolean player1Won = random.nextBoolean();
		
		createAnimationVisuals();
		
		headflipAnimationInventory = createInventory(27, headflipAnimationName, animation1Content);
		
		player1.openInventory(headflipAnimationInventory);
		player2.openInventory(headflipAnimationInventory);
		
		startHeadflipAnimation(items, player1Won);
	}

	private void createAnimationVisuals() 
	{
		animation1Content = new ItemStack[]{lightBlueGlass,blueGlass,lightBlueGlass,blueGlass,playerSkull1,lightBlueGlass,blueGlass,lightBlueGlass,blueGlass,
				playerSkull1,playerSkull2,playerSkull1,playerSkull2, playerSkull1, playerSkull2, playerSkull1, playerSkull2,playerSkull1,
				blueGlass,lightBlueGlass,blueGlass,lightBlueGlass,playerSkull2,blueGlass,lightBlueGlass,blueGlass,lightBlueGlass};
		animation2Content = new ItemStack[]{lightBlueGlass,blueGlass,lightBlueGlass,blueGlass,playerSkull1,lightBlueGlass,blueGlass,lightBlueGlass,blueGlass,
				playerSkull2,playerSkull1,playerSkull2,playerSkull1, playerSkull2, playerSkull1, playerSkull2, playerSkull1,playerSkull2,
				blueGlass,lightBlueGlass,blueGlass,lightBlueGlass,playerSkull2,blueGlass,lightBlueGlass,blueGlass,lightBlueGlass};
	}

	private void startHeadflipAnimation(List<ItemStack> items, boolean player1Won) 
	{
		int iterationRate = 2;
		int count = 30;
		int delay = 0;
		boolean winningPhase = false;
		
		//schedule multiple Runnables to play the animation
		for(int i = 0; i < 4; i++, iterationRate *= 2, count /= 2)
		{
			//if last element
			if(i == 3)
			{
				if(player1Won)
					count = 4;
				else
					count = 5;
				
				winningPhase = true;
			}
			
			HeadflipAnimationData data = CreateAnimationData(items, player1Won, count, winningPhase);
			HeadflipAnimationRunnable animation = new HeadflipAnimationRunnable(data);
			
			animation.runTaskTimer(main, delay, iterationRate);
			delay += count * iterationRate;
		}
	}

	private HeadflipAnimationData CreateAnimationData(List<ItemStack> items, boolean player1Won, int count, boolean winningPhase) 
	{
		HeadflipAnimationData data = new HeadflipAnimationData();
		data.startCount = count;
		data.headflipAnimation = headflipAnimationInventory;
		data.animation1Content = animation1Content;
		data.animation2Content = animation2Content;
		data.player1 = player1;
		data.player2 = player2;
		data.winningPhase = winningPhase;
		data.headflip = this;
		data.player1Won = player1Won;
		data.headflipHandler = headflipsHandler;
		data.items = items;
		data.config = main.getConfig();
		return data;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if(!finalClose)
		{
			if(event.getInventory().equals(headflipInventory) && canBeCancelled)
			{
				handleCancel();
			}
			else if(event.getInventory().equals(headflipAnimationInventory) && startedHeadflip)
			{
				handleCloseInAnimation();
			}
			else if(event.getInventory().equals(headflipAnimationInventory))
			{
				handleFinalCloseIntentory();
			}
		}
	}

	private void handleCloseInAnimation() 
	{
		BukkitTask openInventory = Bukkit.getScheduler().runTaskLater(main, new Runnable()
		{
			@Override
			public void run()
			{
				player2.openInventory(headflipAnimationInventory);
				player1.openInventory(headflipAnimationInventory);
			}
			
		},20);
		
		openInventoryTasks.add(openInventory);
	}

	private void handleFinalCloseIntentory() 
	{
		for(BukkitTask bukkitTask : openInventoryTasks)
		{
			bukkitTask.cancel();
		}
		openInventoryTasks.clear();
		Bukkit.getScheduler().runTaskLater(main, new Runnable()
		{
			@Override
			public void run()
			{
				HandlerList.unregisterAll(headflip);
			}
			
		},20);
	}

	private void handleCancel() 
	{
		finalClose = true;
		
		storeItemsOnCancel();
		
		player2.sendMessage(cancelHeadflip);
		player1.sendMessage(cancelHeadflip);
		player2.closeInventory();
		player1.closeInventory();
		Bukkit.getScheduler().runTaskLater(main, new Runnable()
		{
			@Override
			public void run()
			{
				HandlerList.unregisterAll(headflip);
			}
			
		},20);
	}

	private void storeItemsOnCancel() 
	{
		List<ItemStack> player1Wager = new ArrayList<ItemStack>();
		List<ItemStack> player2Wager = new ArrayList<ItemStack>();
		for(int i : player1Slots)
		{
			ItemStack item = headflipInventory.getItem(i);
			if(item != null && item.getType() != Material.AIR)
			{
				player1Wager.add(item);
			}
		}
		for(int i : player2Slots)
		{
			ItemStack item = headflipInventory.getItem(i);
			if(item != null && item.getType() != Material.AIR)
			{
				player2Wager.add(item);
			}
		}
		if(player1Wager.size() > 0)
		{
			headflipsHandler.storeItems(player1UUID, player1Wager);
		}
		if(player2Wager.size() > 0)
		{
			headflipsHandler.storeItems(player2UUID, player2Wager);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		InventoryView inventoryView = event.getView();
		if(inventoryView.getTopInventory().equals(headflipInventory))
		{
			InventoryInteraction currentAction = getInteractionFromSlot(event.getRawSlot());
			handleInteraction(currentAction, event);
			event.setCancelled(true);
		}
		else if(inventoryView.getTopInventory().equals(headflipAnimationInventory))
		{
			event.setCancelled(true);
		}
	}
	
}