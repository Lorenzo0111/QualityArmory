package me.zombie_striker.qg.config;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.config.system.BaseConfiguration;
import me.zombie_striker.qg.config.system.Comment;
import me.zombie_striker.qg.config.system.Config;
import me.zombie_striker.qg.config.system.Section;
import me.zombie_striker.qg.utils.LocalUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Messages loaded from {@code lang/messages_<language>.yml}.
 * <p>
 * Every String is colorized when it is loaded, so the fields can be sent to players as they are.
 * Use {@link #format(String, Object...)} to replace {@code %placeholders%}.
 */
public class MessagesConfig extends BaseConfiguration {
    private static final String MATERIALS_PATH = "materials";

    public General general = new General();
    public Commands commands = new Commands();
    public Help help = new Help();
    public Shop shop = new Shop();
    public Crafting crafting = new Crafting();
    public Resourcepack resourcepack = new Resourcepack();
    public Lore lore = new Lore();
    public Weapon weapon = new Weapon();
    public Grenade grenade = new Grenade();
    public Medkit medkit = new Medkit();
    public Bleeding bleeding = new Bleeding();
    public AmmoBag ammoBag = new AmmoBag();

    private boolean dirty = false;

    @Section
    public static class General {
        @Config(oldPath = "Prefix")
        public String prefix = "&8[&2QualityArmory&8]&r";
        @Config(oldPath = "NoPerm")
        public String noPermission = "&c You do not have permission to do that.";
        @Config(oldPath = "Reload")
        public String reloaded = " Guns and configs have been reloaded.";
        @Config
        public String playerNotFound = " This player does not exist.";
        @Config
        public String playerNotOnline = "&c That player is not online";
        @Config
        public String consoleRequiresPlayer = " A player is required from the console. /QA give <item> <player> [amount]";
    }

    @Section
    public static class Commands {
        @Config
        public String holdGunRequired = "&c You need to hold a gun to do this.";
        @Config
        public String holdAmmoTemplateRequired = " You need to hold an item that will be used as ammo.";
        @Config
        public String ammoCreated = " A new ammo type has been created.";
        @Config
        public String ammoCreatedReloadHint = " You will need to use /qa reload for the new ammo type to appear.";
        @Config
        public String itemNameRequired = " The item name is required";
        @Config
        public String validItems = " Valid items: ";
        @Comment("Placeholders: %item%")
        @Config
        public String itemNotFound = " Could not find item \"%item%\"";
        @Config
        public String locationRequired = " A valid location is required";
        @Comment("Placeholders: %item%")
        @Config
        public String droppingItem = " Dropping item %item% at that location";
        @Comment("Placeholders: %item%")
        @Config
        public String dropFailed = " Failed to drop item %item% at that location";
        @Comment("Placeholders: %item%")
        @Config
        public String givingItemSelf = " Adding %item% to your inventory";
        @Comment("Placeholders: %item%, %player%")
        @Config
        public String givingItemOther = " Adding %item% to %player%'s inventory";
        @Config
        public String resourcepackOverride = " Overriding resourcepack requirement.";
    }

    @Section
    @Comment("Lines of the /qa help message")
    public static class Help {
        @Config
        public String header = " Commands:";
        @Config
        public String give = "&6/QA give <Item> [player] [amount]:&7 Gives the sender the item specified (guns, ammo, misc.)";
        @Config
        public String craft = "&6/QA craft [Item]:&7 Opens the crafting menu.";
        @Config
        public String shop = "&6/QA shop: &7Opens the shop menu";
        @Config
        public String getResourcepack = "&6/QA getResourcepack: &7Sends the link to the resourcepack. Disables the resourcepack prompts until /qa sendResourcepack is used";
        @Config
        public String sendResourcepack = "&6/QA sendResourcepack: &7Sends the resourcepack prompt. Enables the resourcepack prompt. Enabled by default";
        @Config
        public String reload = "&6/QA reload: &7Reloads all values in QA.";
        @Config
        public String createNewAmmo = "&6/QA createNewAmmo <name>: &7Creates a new ammo type using the item in your hand as a template";
    }

    @Section
    public static class Shop {
        @Comment("Placeholders: %item%, %cost%")
        @Config(oldPath = "Shop_Confirm")
        public String purchaseConfirm = "&aYou have purchased %item% for $%cost%!";
        @Config(oldPath = "NoMoney")
        public String noMoney = "You do not have enough money to buy this.";
        @Config
        public String noEconomy = "ECONOMY NOT ENABLED. REPORT THIS TO THE OWNER!";
    }

    @Section
    public static class Crafting {
        @Config(oldPath = "Missing_Ingredients")
        public String missingIngredients = "You do not have all the materials needed to craft this.";
        @Config(oldPath = "NoPermAnvilMessage")
        public String noAnvilPermission = " &a You do not have permission to use this armory bench. Shift+Click to access anvil.";
    }

    @Section
    public static class Resourcepack {
        @Config(oldPath = "NoResourcepackMessage1")
        public String downloading = " &c&l Downloading Resourcepack...";
        @Config(oldPath = "NoResourcepackMessage2")
        public String acceptPrompt = " &f Accept the resourcepack to see the custom items";
        @Config(oldPath = "Kick_message_if_player_denied_request")
        public String kickMessage = "&c You have been kicked because you did not accept the resourcepack. \n&f If you want to rejoin the server, edit the server entry and set \"Resourcepack Prompts\" to \"Accept\" or \"Prompt\"";
        @Config(oldPath = "Resourcepack_InCaseOfCrash")
        public String crashHelp = "In case the resourcepack crashes your client, reject the request and use /qa getResourcepack to get the resourcepack.";
        @Config(oldPath = "Resourcepack_Download")
        public String download = "Download this resourcepack and enable it to see the custom items (Note that it may take some time to load)";
        @Config(oldPath = "Resourcepack_NowBypass")
        public String nowBypassing = "By issuing this command, you are now added to a whitelist for the resourcepack. You should no longer see the prompt";
        @Config(oldPath = "Resourcepack_NowOptIn")
        public String nowOptedIn = "By issuing this command, you have been removed from the whitelist. You will now receive the resourcepack prompt";
    }

    /**
     * Lore labels. These are also used to read values back from existing items,
     * so changing them will make items created before the change lose that information.
     */
    @Section
    @Comment({"Item lore labels. QualityArmory reads these back from existing items,",
            "so changing them makes items created before the change lose that information"})
    public static class Lore {
        @Config(oldPath = "Lore_Ammo")
        public String ammo = "&aAmmo";
        @Config(oldPath = "lore_bullets")
        public String bullets = "&aBullets";
        @Config(oldPath = "Lore_Damage")
        public String damage = "&aDamage";
        @Config(oldPath = "Lore_Durib")
        public String durability = "Durability";
        @Config(oldPath = "Lore_ingredients")
        public String ingredients = "Ingredients";
        @Config(oldPath = "Lore_Variants")
        public String variant = "&7Varient:";
        @Config(oldPath = "Lore_Varients")
        public String variantLegacy = "&7Varient:";
        @Config(oldPath = "Lore_Price")
        public String price = "&6Price: ";
        @Config(oldPath = "Lore_DamagePerSecond")
        public String dps = "&2DPS";
        @Config(oldPath = "Lore_Crafts")
        public String crafts = "Crafts";
        @Config(oldPath = "Lore_Returns")
        public String returns = "Returns";

        public Controls controls = new Controls();

        @Section
        public static class Controls {
            @Config(oldPath = "Lore-LMB-Single")
            public String singleFire = "&8[LMB] to use Single-fire mode";
            @Config(oldPath = "Lore-LMB-FullAuto")
            public String fullAuto = "&8[Sneak]+[LMB] to use Automatic-fire";
            @Config(oldPath = "Lore-RMB-Reload")
            public String reload = "&8[RMB] to reload";
            @Config(oldPath = "Lore-Ironsights-RMB")
            public String ironsightsRightClick = "&8[RMB] to open ironsights";
            @Config(oldPath = "Lore-Ironsights-Sneak")
            public String ironsightsSneak = "&8[Sneak] to open ironsights";
            @Config(oldPath = "Lore-Reload-Dropitem")
            public String reloadDrop = "&8[DropItem] to reload";
            @Config(oldPath = "Lore-Reload-RMB")
            public String reloadRightClick = "&8[RMB] to reload";
            @Config(oldPath = "Lore-Helmet-RMB")
            public String equipHelmet = "&8[RMB] to equip helmet.";
        }
    }

    @Section
    @Comment("Gun states shown in the item name and hotbar")
    public static class Weapon {
        @Config(oldPath = "Reloading_Message")
        public String reloading = "[Reloading...]";
        @Comment("Placeholders: %total% (ammo in the inventory)")
        @Config(oldPath = "State_AmmoCount")
        public String ammoCount = "[%total%]";
        @Config(oldPath = "State_OutOfAmmo")
        public String outOfAmmo = "[Out of Ammo]";
        @Comment({"Placeholders: %name%, %amount% (bullets loaded), %max% (magazine size),",
                "%state% (one of reloading, ammo-count or out-of-ammo)"})
        @Config(oldPath = "HotbarMessage")
        public String hotbarFormat = "%name% = %amount%/%max% %state%";
    }

    @Section
    public static class Grenade {
        @Config(oldPath = "grenadePull")
        public String pullPin = " Pull the pin first...";
        @Config(oldPath = "grenadeAlreadyPulled")
        public String alreadyPulled = "You already pulled the pin!";
    }

    @Section
    public static class Medkit {
        @Config(oldPath = "Medkit-FullyHealed")
        public String fullyHealed = "&fYou are fully healed. No need for this right now!";
        @Comment("Placeholders: %player%")
        @Config(oldPath = "Medkit-FullyHealed-Other")
        public String fullyHealedOther = "&f%player% is already fully healed. No need for this right now!";
        @Config(oldPath = "Medkit-Healing")
        public String healing = "Healing";
        @Config(oldPath = "Medkit-Bleeding")
        public String bleedingRate = "Blood-Loss Rate:";
    }

    @Section
    public static class Bleeding {
        @Config(oldPath = "Bleeding.StartBleeding")
        public String start = "&cYOU ARE BLEEDING! FIND A MEDKIT TO STOP THE BLEEDING!";
        @Config(oldPath = "Bleeding.ProtectedByKevlar")
        public String protectedByKevlar = "&aYour Kevlar vest protected you from that bullet!";
    }

    @Section
    public static class AmmoBag {
        @Config(oldPath = "AmmoBag.current")
        public String ammo = "&aAmmo: ";
        @Config(oldPath = "AmmoBag.type")
        public String type = "&aAmmo Type: ";
    }

    public MessagesConfig(String language) {
        super(prepareFile(language), QAMain.getInstance().getLogger());

        // Pre-v3 flat keys. Renamed keys are moved through oldPath
        migration(1, config -> {
            move("EntityType", MATERIALS_PATH);

            // Medkit numbers are gameplay settings, they now live in config.yml
            MainConfig main = QAMain.getConfiguration();
            boolean mainChanged = false;
            if (config.contains("Medkit-HEALING_HEARTS_AMOUNT")) {
                main.set("medkit.heal-amount", config.getDouble("Medkit-HEALING_HEARTS_AMOUNT"));
                config.set("Medkit-HEALING_HEARTS_AMOUNT", null);
                mainChanged = true;
            }
            if (config.contains("Medkit-HEALING_WEAPPING_DELAY_IN_SECONDS")) {
                main.set("medkit.heal-delay-seconds", config.getDouble("Medkit-HEALING_WEAPPING_DELAY_IN_SECONDS"));
                config.set("Medkit-HEALING_WEAPPING_DELAY_IN_SECONDS", null);
                mainChanged = true;
            }
            if (mainChanged) {
                main.save();
                main.load();
            }

            // Messages that are no longer used
            config.set("ShopName", null);
            config.set("CraftingBenchName", null);
            config.set("gui.prevPage", null);
            config.set("gui.nextPage", null);
            config.set("Medkit-Lore_RMB", null);
            config.set("Bleeding.Losingconsciousness", null);
        });
    }

    /**
     * Picks the messages file for a language. If it doesn't exist yet, the file from the
     * pre-v3 name ({@code message_<language>.yml}) is moved over, or the bundled translation is copied.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File prepareFile(String language) {
        QAMain plugin = QAMain.getInstance();
        File folder = new File(plugin.getDataFolder(), "lang");
        if (folder.exists() && !folder.isDirectory())
            folder.delete();
        folder.mkdirs();

        File file = new File(folder, "messages_" + language + ".yml");
        if (file.exists()) return file;

        File legacy = new File(folder, "message_" + language + ".yml");
        if (legacy.exists()) {
            if (legacy.renameTo(file)) return file;
            plugin.getLogger().warning("Could not rename " + legacy.getName() + " to " + file.getName() + ". Using the old file name.");
            return legacy;
        }

        try (InputStream stream = plugin.getResource("lang/messages_" + language + ".yml")) {
            if (stream != null)
                Files.copy(stream, file.toPath());
        } catch (IOException e) {
            plugin.handleException(e);
        }
        return file;
    }

    /**
     * Replaces {@code %key%} placeholders in a message.
     *
     * @param message      The message
     * @param placeholders Pairs of placeholder name (without the percent signs) and value
     * @return The formatted message
     */
    public static String format(String message, Object... placeholders) {
        for (int i = 0; i + 1 < placeholders.length; i += 2)
            message = message.replace("%" + placeholders[i] + "%", String.valueOf(placeholders[i + 1]));
        return message;
    }

    /**
     * Gets the translated display name of a material, used in crafting ingredient lists.
     * Missing materials are added to the file with their material name; call {@link #saveIfChanged()} to persist them.
     *
     * @param material The material name
     * @return The colorized display name
     */
    public String getMaterialName(String material) {
        String path = MATERIALS_PATH + "." + material;
        String value = this.config.getString(path);
        if (value == null) {
            this.config.set(path, material);
            this.dirty = true;
            value = material;
        }
        return LocalUtils.colorize(value);
    }

    /**
     * Saves the file if {@link #getMaterialName(String)} added new entries.
     */
    public void saveIfChanged() {
        if (!this.dirty) return;
        this.save();
        this.dirty = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object transformValue(Field field, Object value) {
        if (value instanceof String)
            return LocalUtils.colorize((String) value);
        if (value instanceof List) {
            List<Object> result = new ArrayList<>();
            for (Object entry : (List<Object>) value)
                result.add(entry instanceof String ? LocalUtils.colorize((String) entry) : entry);
            return result;
        }
        return value;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    protected String[] getHeader() {
        return new String[]{
                "QualityArmory messages",
                "Color codes (&a) and hex colors (&#55ff55, 1.16+) are supported. Use /qa reload to apply changes."
        };
    }
}
