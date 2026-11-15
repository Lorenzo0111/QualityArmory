package me.zombie_striker.qg.hooks;

public final class MimicHookHandler {

    public static void register() {
        new MimicHookImpl().register();
    }

}
