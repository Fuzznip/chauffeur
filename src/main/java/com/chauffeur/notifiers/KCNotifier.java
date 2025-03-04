package com.chauffeur.notifiers;

import com.chauffeur.utils.ClientPopup;

import javax.inject.Inject;

public class KCNotifier {
    @Inject
    private ClientPopup clientPopup;
    public void handleNotify(String chatMessage)
    {
        clientPopup.addNotificationToQueue("Tile Completed!", "You killed a " + chatMessage);
    }
}
