package me.zombie_striker.qualityarmory;

public enum MessageKey {
    PREFIX("Prefix"),
    KICK_ON_DECLINE_RESOURCEPACK("DeclineResourcepackKickMessage"),
    RELOAD_MESSAGE("ReloadMessage"),
    NO_PERM_COMMAND("NoPermissionMessage"),
    ;

    private final String key;

    MessageKey(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
