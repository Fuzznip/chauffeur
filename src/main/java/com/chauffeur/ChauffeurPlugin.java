package com.chauffeur;

import com.chauffeur.notifiers.*;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import net.runelite.http.api.loottracker.LootRecordType;

import java.awt.image.BufferedImage;
import java.util.Collection;

@Slf4j
@PluginDescriptor(
	name = "Chauffeur"
)
public class ChauffeurPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ChauffeurConfig config;

	@Inject
	private ClueNotifier clueNotifier;
	@Inject
	private KCNotifier kcNotifier;
	@Inject
	private LootNotifier lootNotifier;
	@Inject
	private PetNotifier petNotifier;
	@Inject
	private SlayerNotifier slayerNotifier;
	@Inject
	private XPNotifier xpNotifier;

	@Inject
	private ClientToolbar clientToolbar;
	private NavigationButton navButton;
	private ChauffeurPanel chauffeurPanel;

	public static String sanitize(String str)
	{
		if (str == null || str.isEmpty()) return "";
		return Text.removeTags(str.replace("<br>", "\n")).replace('\u00A0', ' ').trim();
	}

	@Override
	protected void startUp() throws Exception
	{
		chauffeurPanel = injector.getInstance(ChauffeurPanel.class);
		final BufferedImage image = ImageUtil.loadImageResource(ChauffeurPlugin.class, "/panel_icon.png");
		navButton = NavigationButton.builder()
			.tooltip("Chauffeur")
			.icon(image)
			.priority(9)
			.panel(chauffeurPanel)
			.build();

		clientToolbar.addNavigation(navButton);
		chauffeurPanel.init();

		log.info("Chauffeur started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		chauffeurPanel.shutdown();
		clientToolbar.removeNavigation(navButton);
		log.info("Chauffeur stopped!");
	}

//	@Subscribe
//	public void onGameStateChanged(GameStateChanged gameStateChanged)
//	{
//		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
//		{
//			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.eventCodes(), null);
//		}
//	}

	@Provides
	ChauffeurConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ChauffeurConfig.class);
	}

	@Subscribe(priority = 1) // run before base loot tracker plugin
	public void onChatMessage(ChatMessage message)
	{
		String chatMessage = sanitize(message.getMessage());
		switch(message.getType())
		{
			case GAMEMESSAGE:
				log.info("Game Message");
				kcNotifier.handleNotify(chatMessage);
		}
	}

	@Subscribe
	public void onLootReceived(LootReceived lootReceived)
	{
		log.info("We got a loot drop from " + lootReceived.getName());
		if(lootReceived.getType() == LootRecordType.PICKPOCKET)
			log.info("pickpocket!");
	}

	@Subscribe(priority = 1)
	public void onNpcLootReceived(NpcLootReceived npcLootReceived)
	{
		NPC npc = npcLootReceived.getNpc();
		Collection<ItemStack> items = npcLootReceived.getItems();

		lootNotifier.handleNotify(items, npc.getName());

		log.info("We got an NPC drop from " + npc.getName());
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		final Skill skill = statChanged.getSkill();
		final int xp = statChanged.getXp();

		xpNotifier.handleNotify(skill, xp);
	}
}
