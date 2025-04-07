package com.vcb.spring.ai.model;

import java.util.List;

public record Player(String playerName, List<String> achievements) {
    public Player(String playerName, String... achievements) {
        this(playerName, List.of(achievements));
    }

    public Player(String playerName) {
        this(playerName, List.of());
    }
}
