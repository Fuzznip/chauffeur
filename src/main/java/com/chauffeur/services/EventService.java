package com.chauffeur.services;

import com.chauffeur.models.EventConfig;
import javax.inject.Singleton;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class EventService {
    private EventConfig currentEventConfig;

    public void setEventConfig(EventConfig config) {
        log.info("Setting event config: {}", config);
        this.currentEventConfig = config;
    }

    public EventConfig getEventConfig() {
        return currentEventConfig;
    }
}
