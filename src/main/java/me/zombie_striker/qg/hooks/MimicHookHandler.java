package me.zombie_striker.qg.hooks;

public final class MimicHookHandler {

    public static void register() {
        try {
            new MimicHookImpl().register();
        } catch (Exception | Error ignored) {
        }
    }

}
