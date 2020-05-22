package de.happyirl.headflip.commands;

import java.util.UUID; 

public class HeadflipRequestData 
{
	public UUID source;
	public UUID target;
	
	public HeadflipRequestData(UUID source, UUID target)
	{
		this.source = source;
		this.target = target;
	
	}	
}
