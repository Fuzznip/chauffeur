package com.chauffeur.utils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.WidgetNode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetModalMode;
import net.runelite.client.callback.ClientThread;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.chauffeur.utils.PopupConstants.*;

@Slf4j
public class ClientPopup {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    private WidgetNode popupWidgetNode;
    private final List<Pair<String, String>> queuedPopups = new ArrayList<>();

    public void addNotificationToQueue(String title, String message){
        String cleanTitle = title.replace("~", "").replace("|","");
        String cleanMessage = message.replace("~", "").replace("|", "");
        queuedPopups.add(Pair.of(cleanTitle, cleanMessage));
        if(queuedPopups.size() == 1) {
            showPopup(cleanTitle, cleanMessage);
        }
    }

    private void showPopup(String title, String message)
    {
        clientThread.invokeLater(() -> {
            try {
                int componentId = client.isResized()
                        ? client.getVarbitValue(Varbits.SIDE_PANELS) == 1
                        ? RESIZABLE_MODERN_LAYOUT
                        : RESIZABLE_CLASSIC_LAYOUT
                        : FIXED_CLASSIC_LAYOUT;
                popupWidgetNode = client.openInterface(componentId, 660, WidgetModalMode.MODAL_CLICKTHROUGH);
                client.runScript(3343, title, message, -1);

                playSound();

                clientThread.invokeLater(this::tryClearMessage);
            } catch(IllegalStateException e) {
               log.warn("Failed to show popup");
            }
        });
    }

    private void playSound() {
        try {
            Clip clip = null;
            AudioInputStream audioInputStream = null;
            clip = AudioSystem.getClip();
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/sounds/chauffeur_task_completed.wav"));
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            log.warn("Failed to play sound", e);
        } /*finally {
            if(audioInputStream != null) {
                try {
                    audioInputStream.close();
                } catch (Exception e) {
                    log.warn("Failed to close audio input stream", e);
                }
            }
            if(clip != null && clip.isOpen()) {
                clip.close();
            }
        }*/
    }

    public boolean tryClearMessage()
    {
        Widget w = client.getWidget(660, 1);
        if(w != null && w.getWidth() > 0)
        {
            return false;
        }

        try{
            client.closeInterface(popupWidgetNode, true);
        } catch( Exception ex) {
            log.warn("Failed to clear message");
        }

        popupWidgetNode = null;
        queuedPopups.remove(0);

        if(!queuedPopups.isEmpty())
        {
            clientThread.invokeLater(()-> {
                showPopup(queuedPopups.get(0).getLeft(), queuedPopups.get(0).getRight());
                return true;
            });
        }

        return true;
    }
}