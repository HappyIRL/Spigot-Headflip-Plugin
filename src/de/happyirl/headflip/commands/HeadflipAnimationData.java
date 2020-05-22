package de.happyirl.headflip.commands;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HeadflipAnimationData 
{
	public int startCount;
	public Inventory headflipAnimation;
	public ItemStack[] animation1Content, animation2Content;
	public Player player1, player2;
	public boolean winningPhase;
	public Headflip headflip;
	public boolean player1Won;
	public HeadflipsHandler headflipHandler;
	public List<ItemStack> items;
	public FileConfiguration config;
}
