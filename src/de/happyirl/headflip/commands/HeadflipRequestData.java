package de.happyirl.headflip.commands;

import java.util.Date;
import java.util.UUID;

public class HeadflipRequestData 
{
	public UUID source;
	public UUID target;
	public Date date;
	public HeadflipRequestData(UUID source, UUID target, Date date)
	{
		this.source = source;
		this.target = target;
		this.date = date;
	}
}
