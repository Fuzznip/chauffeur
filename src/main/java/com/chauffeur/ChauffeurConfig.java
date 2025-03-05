package com.chauffeur;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("chauffeur")
public interface ChauffeurConfig extends Config
{
	@ConfigItem(
		keyName = "serverUrl",
		name = "Server URL",
		description = "The URL of the backend server that holds the event data"
	)
	default String serverUrl()
	{
		return "http://127.0.0.1:8080";
	}
}
