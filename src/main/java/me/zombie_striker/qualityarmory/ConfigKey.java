package me.zombie_striker.qualityarmory;

public enum ConfigKey {
    CUSTOMITEM_MAXBULLETS("maxBullets"),
    CUSTOMITEM_AMMOTYPE("ammoType"),
    CUSTOMITEM_LORE("lore"),
    CUSTOMITEM_DISPLAYNAME("displayname"),


    SETTING_ENABLEDCRAFTING("enableCraftingWeapons"),
    SETTING_ENABLESHOP("enableShopCommand"),
    SETTING_DEBUG("enableDebugMessages")
    ;

    private final String key;

    ConfigKey(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
