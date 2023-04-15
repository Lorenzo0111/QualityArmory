package me.zombie_striker.qualityarmory.hooks;

public final class MimicHookHandler {

    public static void register() {
        try {
            new MimicHookImpl().register();
        } catch (Exception | Error ignored) {}
    }

}
