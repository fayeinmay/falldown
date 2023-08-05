package de.jandev.falldown.model.player;

public enum PlayerRole {
    USER(0), PREMIUM(1), YOUTUBER(5), MODERATOR(10), ADMINISTRATOR(20), OWNER(30), DEVELOPER(50);

    private final int power;

    PlayerRole(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}
