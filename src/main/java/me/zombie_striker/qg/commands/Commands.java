package me.zombie_striker.qg.commands;

/**
 * Builds the {@code /qualityarmory} command.
 */
public final class Commands {

    private Commands() {
    }

    public static CommandRegistry create() {
        return new CommandRegistry()
                .register(new VersionCommand())
                .register(new DebugCommand())
                .register(new DumpItemCommand())
                .register(new CreateNewAmmoCommand())
                .register(new SendResourcepackCommand())
                .register(new GetResourcepackCommand())
                .register(new ListItemIdsCommand())
                .register(new ReloadCommand())
                .register(new DropCommand())
                .register(new GiveCommand())
                .registerPlayer(new OverrideCommand())
                .registerPlayer(new CraftCommand())
                .registerPlayer(new ShopCommand())
                .suggestInOrder("give", "drop", "getResourcepack", "sendResourcepack", "version", "dumpItem", "shop", "craft", "reload");
    }
}
