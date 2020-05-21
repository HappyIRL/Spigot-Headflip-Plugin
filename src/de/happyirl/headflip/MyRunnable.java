package de.happyirl.headflip;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.happyirl.headflip.commands.Headflip;
import de.happyirl.headflip.commands.HeadflipsHandler;

public class MyRunnable extends BukkitRunnable
{
	private int count;
	private boolean content1 = true;
	private Inventory headflipAnimation;
	ItemStack[] animation1Content;
	ItemStack[] animation2Content;
	Player player1;
	Player player2;
	private Sound rotationSound;
	private boolean winningPhase;
	public int animationID;
	private Headflip headflip;
	private boolean player1Won;
	private HeadflipsHandler headflipHandler;
	private List<ItemStack> items;
	private final String headflipWin;
	private final String headflipLose;
	
	
	public MyRunnable(int count, Inventory headflipAnimation, ItemStack[] animation1Content,
			ItemStack[] animation2Content, Player player1, Player player2, boolean winningPhase,
			Headflip headflip, boolean player1Won, HeadflipsHandler headflipHandler, List<ItemStack> items, Main main)
	{
		this.items = items;
		this.headflipHandler = headflipHandler;
		this.player1Won = player1Won;
		this.headflip = headflip;
		this.player1 = player1;
		this.player2 = player2;
		this.count = count;
		this.headflipAnimation = headflipAnimation;
		this.animation1Content = animation1Content;
		this.animation2Content = animation2Content;
		rotationSound = Sound.NOTE_PLING;
		this.winningPhase = winningPhase;
		this.headflipWin = main.config.getString("headflip.headflipWin");
		this.headflipLose = main.config.getString("headflip.headflipLose");
	}
	@Override
	public void run() 
	{
		if(!winningPhase)
		{
			if(count > 0)
			{
				if(content1)
				{
					headflipAnimation.setContents(animation2Content);
				}
				else
				{
					headflipAnimation.setContents(animation1Content);
				}
				content1 = !content1;
				playRotationSound(player1);
				playRotationSound(player2);
				player1.updateInventory();
				player2.updateInventory();
				count--;
			}
			else
			{
				this.cancel();
			}
		}
		else
		{
			if(count > 0)
			{
				if(content1)
				{
					headflipAnimation.setContents(animation2Content);
				}
				else
				{
					headflipAnimation.setContents(animation1Content);
				}
				content1 = !content1;
				playRotationSound(player1);
				playRotationSound(player2);
				player1.updateInventory();
				player2.updateInventory();
				count--;
			}
			else
			{
				if(player1Won)
				{
					completeHeadflip(player1, player2);
				}
				else
				{
					completeHeadflip(player2, player1);
				}
				headflip.finishHeadflip();
				this.cancel();
			}
		}
	}
	private void completeHeadflip(Player winner, Player loser)
	{
		headflipHandler.addCollectionItems(winner.getUniqueId(), items);
		winner.sendMessage(headflipWin);
		winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 1, 1);
		loser.sendMessage(headflipLose);
		loser.playSound(loser.getLocation(), Sound.LEVEL_UP, 1, 1);
	}
	private void playRotationSound(Player player)
	{
		player.playSound(player.getLocation(), rotationSound, 1, 1);
	}

}
