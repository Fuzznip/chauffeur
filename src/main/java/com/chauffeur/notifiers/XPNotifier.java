package com.chauffeur.notifiers;

import com.chauffeur.utils.ClientPopup;
import net.runelite.api.Skill;

import javax.inject.Inject;

public class XPNotifier {
    @Inject
    private ClientPopup clientPopup;
    public void handleNotify(Skill skill, int xp)
    {
        clientPopup.addNotificationToQueue(skill.getName(), String.valueOf(xp));
    }
}
