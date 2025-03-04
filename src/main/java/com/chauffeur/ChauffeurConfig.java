package com.chauffeur;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("chauffeur")
public interface ChauffeurConfig extends Config
{
	@ConfigItem(
		keyName = "eventCode",
		name = "Event Code",
		description = "The event code for the event you wish to partake in"
	)
	default String eventCode()
	{
		return "";
	}

	@ConfigItem(
		keyName = "serverUrl",
		name = "Server URL",
		description = "The URL of the backend server that holds the event data"
	)
	default String serverUrl()
	{
		return "stabiliserver.up.railway.app";
	}
}
