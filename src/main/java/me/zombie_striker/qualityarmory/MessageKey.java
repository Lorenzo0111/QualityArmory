package me.zombie_striker.qualityarmory;

public enum MessageKey {
    PREFIX("Prefix"),
    KICK_ON_DECLINE_RESOURCEPACK("DeclineResourcepackKickMessage"),
    RELOAD_MESSAGE("ReloadMessage"),
    NO_PERM_COMMAND("NoPermissionMessage"),

    TUTORIAL_GUN_FIRE("GunTutorial.GunFireLore"),
    TUTORIAL_GUN_RELOAD("GunTutorial.GunReloadLore"),
    ;

    private final String key;

    MessageKey(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
