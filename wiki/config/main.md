# Main Configuration

This is the documentation of the main configuration, also known as config.yml.\
Below you can find a list of all the options that you can configure and their explanation.

* **ENABLE-DEBUG**: `false` - Toggles detailed debug messages in the console. Useful for troubleshooting.
* **language**: `en` - Sets the language file used for plugin messages (e.g., `en` for English).
* **FriendlyFireEnabled**: `false` - If `true`, allows players on the same team or party to damage each other with guns.
* **KickPlayerIfDeniedResourcepack**: `false` - If `true`, players who decline the server resource pack prompt will be kicked.
* **useDefaultResourcepack**: `true` - If `true`, the plugin uses its built-in default resource pack URLs. If `false`, it uses the URLs specified in `DefaultResourcepackOverride`.
* **EnableWeaponDurability**: `false` - If `true`, QualityArmory weapons will lose durability when used.
* **BulletDetection.step**: `0.1` - Controls the distance interval (in blocks) for checking bullet collisions. Smaller values are more accurate but may use more server resources.
* **BlockBullets.door**: `false` - If `true`, bullets will be stopped by door blocks.
* **BlockBullets.halfslabs**: `false` - If `true`, bullets will be stopped by half-slab blocks.
* **BlockBullets.leaves**: `false` - If `true`, bullets will be stopped by leaf blocks.
* **BlockBullets.water**: `false` - If `true`, bullets will be stopped upon entering water blocks.
* **BlockBullets.glass**: `false` - If `true`, bullets will be stopped by glass blocks.
* **enableInteract.Chests**: `false` - If `true`, allows players to open chests while holding a QualityArmory weapon.
* **overrideAnvil**: `false` - If `true`, replaces the standard anvil interface with the QualityArmory crafting bench when interacting with an anvil.
* **showPossibleCrashHelpMessage**: `true` - If `true`, displays a message suggesting `/qa getResourcepack` if a player might be experiencing client crashes due to the resource pack.
* **anticheatFix**: `false` - Enables specific adjustments intended to improve compatibility with some anti-cheat plugins.
* **verboseItemLogging**: `false` - If `true`, prints detailed information to the console during the loading of guns, ammo, and other items.
* **enable\_permssionsToShoot**: `false` - If `true`, players require the permission `qualityarmory.shoot.<gun_name>` to fire specific guns.
* **sendOnJoin**: `true` - If `true`, automatically prompts players to download the resource pack when they join the server.
* **sendTitleOnJoin**: `false` - If `true`, displays a title message related to the resource pack when players join.
* **SecondsTillRPIsSent**: `5.0` - The delay in seconds after a player joins before the resource pack prompt is sent (if `sendOnJoin` is true).
* **enableBulletTrails**: `true` - If `true`, displays particle trails following bullets.
* **BulletTrailsSpacing**: `0.5` - The distance (in blocks) between particles in a bullet trail.
* **enableIgnoreArmorProtection**: `false` - If `true`, bullets ignore the protection value of armor worn by the target.
* **enableIgnoreUnbreakingChecks**: `false` - If `true`, the Unbreaking enchantment on items is ignored when calculating durability loss (if `EnableWeaponDurability` is true).
* **enableIgnoreSkipForBasegameItems**: `false` - A legacy or internal setting likely related to how the plugin handles interactions with vanilla Minecraft items.
* **Items.enable\_Unbreaking**: `true` - If `true`, makes QualityArmory items inherently unbreakable, regardless of the `EnableWeaponDurability` setting.
* **enableReloadingOnDrop**: `false` - If `true`, allows players to reload by pressing their drop key (default 'Q') while holding a gun.
* **enableReloadingWhenSwapToOffhand**: `true` - If `true`, allows players to reload by pressing their swap-to-offhand key (default 'F') while holding a gun.
* **enableReloadOnlyWhenSwapToOffhand**: `false` - If `true`, reloading is _only_ possible using the swap-to-offhand key ('F'), disabling other methods like right-click (if applicable).
* **allowGunHitEntities**: `false` - If `true`, allows guns to register melee damage when hitting entities directly.
* **showOutOfAmmoOnTitle**: `false` - If `true`, displays a large title message on the screen when the player tries to shoot with an empty gun.
* **showReloadingTitle**: `false` - If `true`, displays a large title message on the screen while the player is reloading.
* **showAmmoInXPBar**: `false` - If `true`, displays the current gun's ammo count in the player's experience bar.
* **perWeaponPermission**: `false` - If `true`, requires players to have individual permissions (`qualityarmory.use.<item_name>`) to use each specific gun, ammo, or misc item.
* **useMoveForRecoil**: `true` - If `true`, recoil is simulated by directly moving the player's camera view. If `false`, other methods might be used.
* **enableExplosionDamage**: `false` - If `true`, explosions caused by QualityArmory projectiles can destroy blocks.
* **enableExplosionDamageDrop**: `false` - If `true` and `enableExplosionDamage` is true, blocks destroyed by explosions will drop their corresponding items.
* **enablePrimaryWeaponLimiter**: `false` - If `true`, enforces limits on the number of primary and secondary weapons a player can carry simultaneously.
* **weaponlimiter\_primaries**: `2` - The maximum number of items classified as 'primary' weapons a player can carry if the limiter is enabled.
* **weaponlimiter\_secondaries**: `2` - The maximum number of items classified as 'secondary' weapons a player can carry if the limiter is enabled.
* **enableCrafting**: `true` - Enables the `/qa craft` command and the crafting system for QualityArmory items.
* **enableShop**: `true` - Enables the `/qa shop` command and the GUI shop for buying QualityArmory items.
* **AUTO-UPDATE**: `true` - If `true`, the plugin will attempt to automatically download and install updates on server start.
* **Swap-Reload-and-Shooting-Controls**: `false` - If `true`, swaps the default mouse button actions (e.g., makes right-click shoot and left-click aim/reload).
* **Order-Shop-By-Price**: `false` - If `true`, items listed in the `/qa shop` GUI will be sorted by their configured price.
* **enable\_lore\_gun-info\_messages**: `true` - If `true`, displays weapon statistics like damage, ammo type, etc., in the item's lore text.
* **enable\_lore\_control-help\_messages**: `true` - If `true`, displays basic control instructions (e.g., "\[LMB] Shoot") in the item's lore text.
* **Enable\_Headshot\_Instantkill**: `true` - If `true`, landing a headshot on a player or mob results in an instant kill.
* **Enable\_Headshot\_Notification\_Sound**: `true` - If `true`, plays a sound effect to the shooter when they successfully land a headshot.
* **Headshot\_Notification\_Sound**: `entity.experience_orb.pickup` - The specific sound event name to play for a headshot notification.
* **Enable\_Headshot\_Sounds**: `true` - If `true`, enables additional sounds related to headshots (e.g., impact/gore sounds).
* **Headshot\_Blacklist**: `[]` - A list of entity types (e.g., `ZOMBIE`, `PLAYER`) that are immune to the special effects of headshots (like instant kill).
* **Enable\_AutoArm\_Grenades**: `false` - If `true`, grenades are automatically armed when thrown, bypassing the need to manually "pull the pin" first.
* **gravityConstantForDropoffCalculations**: `0.05` - The value representing gravity's effect used in calculating bullet trajectory and drop-off.
* **allowGunReload**: `true` - A master switch to enable or disable all gun reloading mechanics.
* **Auto-Detect-Resourcepack**: `true` - If `true`, the plugin attempts to automatically select the correct resource pack version based on the detected Minecraft server version.
* **ManuallyOverrideTo\_1\_8\_systems**: `false` - If `true`, forces the plugin to use item models and mechanics designed for Minecraft 1.8, overriding auto-detection.
* **ManuallyOverrideTo\_1\_13\_systems**: `false` - If `true`, forces the plugin to use item models and mechanics designed for Minecraft 1.9-1.13, overriding auto-detection.
* **ManuallyOverrideTo\_1\_14\_systems**: `false` - If `true`, forces the plugin to use item models (like Crossbow) and mechanics designed for Minecraft 1.14+, overriding auto-detection.
* **unknownTranslationKeyFixer**: `false` - If `true`, attempts to apply fixes for potential issues related to missing or incorrect translation keys in item names or lore.
* **Enable\_Creation\_Of\_Default\_Files**: `true` - If `true`, the plugin will automatically generate default configuration files (like for guns, ammo, etc.) in its folder if they are missing.
* **EnableGlowEffects**: `false` - If `true`, enables glowing effects on certain items or potentially entities under specific conditions.
* **Break-Block-Texture-If-Shot**: `true` - If `true`, displays block breaking particle effects when a block is hit by a bullet.
* **enableRecoil**: `true` - Enables or disables the visual recoil effect when firing weapons.
* **experimental.BulletWounds.InitialBloodLevel**: `1500.0` - The starting value for the internal "blood level" tracker used by the experimental bleeding system.
* **experimental.BulletWounds.BloodIncreasePerSecond**: `0.01` - Likely represents the rate at which the "blood level" decreases per second when bleeding (needs verification, name is counter-intuitive).
* **experimental.BulletWounds.Medkit\_Heal\_Bloodloss\_Rate**: `0.05` - The rate at which using a medkit counteracts bleeding or restores the "blood level".
* **experimental.BulletWounds.enableBleeding**: `false` - Enables the experimental system where players can suffer from bleeding after being shot, potentially requiring a medkit.
* **disableHotbarMessages.OutOfAmmo**: `false` - If `true`, prevents the "Out of Ammo" message from appearing above the hotbar.
* **disableHotbarMessages.Shoot**: `false` - If `true`, prevents the ammo count message from appearing above the hotbar when shooting.
* **disableHotbarMessages.Reload**: `false` - If `true`, prevents the "Reloading..." message from appearing above the hotbar.
* **automaticallyReloadGunWhenOutOfAmmo**: `false` - If `true`, the gun will automatically begin reloading as soon as the player tries to fire with no ammo left.
* **generalModifiers.sway.Run**: `1.3` - A multiplier affecting the amount of weapon sway while the player is running.
* **generalModifiers.sway.Walk**: `1.5` - A multiplier affecting the amount of weapon sway while the player is walking.
* **generalModifiers.sway.Ironsights**: `0.8` - A multiplier affecting the amount of weapon sway while the player is aiming down sights (values < 1 reduce sway).
* **generalModifiers.sway.Sneak**: `0.7` - A multiplier affecting the amount of weapon sway while the player is sneaking (values < 1 reduce sway).
* **deathmessages.enable**: `true` - If `true`, enables custom death messages indicating kills made with QualityArmory weapons.
* **impenetrableEntityTypes**: `[ARROW]` - A list of entity types that bullets cannot pass through (e.g., arrows, potentially other projectiles).
* **DefaultResourcepackOverride**: `false` - If `true`, forces the plugin to use the resource pack URL(s) defined under `DefaultResourcepack` instead of its internal defaults.
* **IronSightsOnRightClick**: `false` - If `true`, aiming down sights (iron sights) is activated by right-clicking instead of the default (usually sneaking).
* **SwapSneakToSingleFire**: `false` - If `true`, changes the control for single-fire mode on automatic weapons to Sneak + Left-Click.
* **DestructableMaterials**: `[MATERIAL_NAME_HERE]` - A list of material names (e.g., `GLASS`, `WHITE_WOOL`) that can be broken by bullets if `enableExplosionDamage` is appropriately configured or via specific weapon properties.
* **RegenDestructableBlocksAfter**: `-1` - The time in server ticks after which a block destroyed by a QualityArmory weapon/explosion will regenerate. `-1` disables regeneration.
* **overrideAttackSpeed**: `true` - If `true` (primarily for 1.14+), overrides the default item attack speed attribute, potentially allowing faster firing rates.
* **DefaultResourcepack**: Defines the resource pack URLs. Refer to [ResourcePack Configuration](resourcepack.md) for more information.
