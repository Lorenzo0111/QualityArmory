package me.zombie_striker.qg.config.items;

import me.zombie_striker.qg.config.system.Config;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class AmmoDefinition extends ItemDefinition {
    @Config
    public String skullOwner;
    @Config
    public String skullOwnerCustomUrl;
    @Config
    public double piercingSeverity = 0;

    public AmmoDefinition(File file) {
        super(file);
    }

    @Override
    protected Map<String, String> typeRenames() {
        // Old name of max-item-stack; maxItemStack wins when both are set
        return Collections.singletonMap("maxAmount", "max-item-stack");
    }
}
