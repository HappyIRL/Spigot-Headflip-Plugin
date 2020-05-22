package de.happyirl.headflip;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.happyirl.headflip.commands.HeadflipAnimationData;



public class HeadflipAnimationRunnable extends BukkitRunnable
{
	private int count;
	private boolean content1 = true;
	private Sound rotationSound;
	
	private final String headflipWin;
	private final String headflipLose;
	
	private HeadflipAnimationData data;
	
	public HeadflipAnimationRunnable(HeadflipAnimationData data)
	{
		this.data = data;
		this.count = data.startCount;
		this.rotationSound = Sound.NOTE_PLING;
		this.headflipWin = data.config.getString("headflip.headflipWin");
		this.headflipLose = data.config.getString("headflip.headflipLose");
	}
	
	@Override
	public void run() 
	{
		if(count > 0)
		{
			if(content1)
			{
				data.headflipAnimation.setContents(data.animation2Content);
			}
			else
			{
				data.headflipAnimation.setContents(data.animation1Content);
			}
			content1 = !content1;
			data.player1.updateInventory();
			data.player2.updateInventory();
			playRotationSound(data.player1);
			playRotationSound(data.player2);
			count--;
		}
		else //count = 0
		{
			if(data.winningPhase) 
			{
				if(data.player1Won)
				{
					completeHeadflip(data.player1, data.player2);
				}
				else
				{
					completeHeadflip(data.player2, data.player1);
				}
				data.headflip.finishHeadflip();
			}
			
			this.cancel();
		}
	}

	private void completeHeadflip(Player winner, Player loser)
	{
		data.headflipHandler.storeItems(winner.getUniqueId(), data.items);
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
