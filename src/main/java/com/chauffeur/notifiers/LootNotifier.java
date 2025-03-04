package com.chauffeur.notifiers;
import com.chauffeur.utils.ClientPopup;
import com.chauffeur.utils.ItemSearcher;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.chatcommands.ChatCommandsPlugin;

import javax.inject.Inject;
import java.util.Collection;

@Slf4j
public class LootNotifier {
    @Inject
    private ClientPopup clientPopup;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ItemSearcher itemSearcher;

    @Inject
    private Client client;

    public void handleNotify(Collection<ItemStack> items, String npcName)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for(ItemStack item : items)
        {
            String itemName = client.getItemDefinition(item.getId()).getName();
            stringBuilder.append(itemName).append(" x").append(item.getQuantity()).append(", ");
        }
        String lootString = stringBuilder.toString();
        log.debug("Loot: {}", lootString);
        lootString = lootString.substring(0, lootString.length() - 2);
        clientPopup.addNotificationToQueue("Tile Completed!", "You killed a " + npcName + " and received: " + lootString);
    }
}
