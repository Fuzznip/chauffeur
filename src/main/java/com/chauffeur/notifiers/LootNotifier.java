package com.chauffeur.notifiers;
import com.chauffeur.ChauffeurConfig;
import com.chauffeur.models.EventConfig;
import com.chauffeur.models.WhitelistItem;
import com.chauffeur.models.payloads.LootPayload;
import com.chauffeur.models.payloads.Payload;
import com.chauffeur.models.payloads.SubmissionResponsePayload;
import com.chauffeur.services.EventService;
import com.chauffeur.utils.ClientPopup;
import com.chauffeur.utils.HttpClient;
import com.chauffeur.utils.ItemSearcher;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

@Slf4j
@Singleton
public class LootNotifier {
    @Inject
    private ClientPopup clientPopup;

    @Inject
    private ChauffeurConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ItemSearcher itemSearcher;

    @Inject
    private Client client;

    @Inject
    private EventService eventService;

    @Inject
    private HttpClient httpClient;

    @Inject
    private ConfigManager configManager;

    private String ensureHttpProtocol(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }

    public void handleNotify(Collection<ItemStack> items, String npcName)
    {
        log.info("Handling loot notification for {} items from {}", items.size(), npcName);

        if (items.isEmpty())
        {
            log.info("No items to notify");
            return;
        }

        EventConfig eventConfig = eventService.getEventConfig();
        if(eventConfig == null)
        {
            log.info("Event config is null");
            return;
        }

        // Dump the contents of eventConfig
        log.info("Event config: {}", eventConfig.toString());
        List<WhitelistItem> imageWhitelist = eventConfig.getImageWhitelist();
        // Print out the image whitelist
        for(WhitelistItem item : imageWhitelist)
        {
            log.info("Image whitelist item: {}", item.getDrop());
        }
        List<WhitelistItem> noImageWhitelist = eventConfig.getNoImageWhitelist();
        // Print out the no image whitelist
        for(WhitelistItem item : noImageWhitelist)
        {
            log.info("No image whitelist item: {}", item.getDrop());
        }

        String eventCode = configManager.getConfiguration("chauffeur", "eventCode");
        String configUrl = ensureHttpProtocol(config.serverUrl());
        String url = String.format("%s/submit?event_code=%s", configUrl, eventCode);

        for(ItemStack item : items)
        {
            String itemName = itemManager.getItemComposition(item.getId()).getName();
            WhitelistItem whitelistItem = new WhitelistItem();
            whitelistItem.setDrop(itemName);
            whitelistItem.setSource(null);
            log.info("Checking item: {}", itemName);
            // Check if the item is in the image whitelist
            if(imageWhitelist.contains(whitelistItem))
            {
                // Send drop data to server
                Payload<LootPayload> payload = new Payload<>();
                payload.data = new LootPayload();
                payload.data.itemName = itemName;
                payload.data.source = npcName;
                payload.data.itemId = item.getId();
                payload.playerName = client.getLocalPlayer().getName();

                SubmissionResponsePayload srp = httpClient.postRequest(url, payload, SubmissionResponsePayload.class);
                if(srp != null && srp.notification != null)
                {
                    clientPopup.addNotificationToQueue(srp.notification.title, srp.notification.body);
                }
                log.info("Item is in the image whitelist: {}", itemName);
                continue;
            }

            if(noImageWhitelist.contains(whitelistItem))
            {
                log.info("Item is in the no image whitelist: {}", itemName);
                continue;
            }

            whitelistItem.setSource(npcName);

            if(imageWhitelist.contains(whitelistItem))
            {
                log.info("Item with source is in the image whitelist: {} ({})", itemName, npcName);
                continue;
            }

            if(noImageWhitelist.contains(whitelistItem))
            {
                log.info("Item with source is in the no image whitelist: {} ({})", itemName, npcName);
                continue;
            }
        }
    }
}
