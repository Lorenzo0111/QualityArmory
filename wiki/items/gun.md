# Gun Configuration

Creating a gun requires many steps, from configuring the settings to creating the model.&#x20;

{% hint style="info" %}
I suggest copying another gun's configuration as a reference
{% endhint %}

These are all the fields used by a gun with their description:

* `name`: The internal, unique identifier for the weapon used in commands (e.g., `/qa give cz75`). It's recommended to keep this lowercase and without space
* **`displayname`**: The name shown on the item in-game. Supports standard Minecraft color codes (e.g., `&6`).
* **`id`**: Used with `material` and `variant` to uniquely identify the item type.
  * **Minecraft 1.14+**: This value corresponds to the `CustomModelData` tag on the item, used by resource packs to apply the correct model/texture.
  * **Minecraft <1.14**: This value corresponds to the item's durability/damage value, used by resource packs to apply the correct model/texture.
* **`variant`**: An additional identifier used alongside `material` and `id`. Allows creating different guns that use the same base `material` and `id`/`CustomModelData` but have different stats or behaviors.
* **`craftingRequirements`**: A list of materials required to craft this item using the `/qa craft` system. Format for each entry: `'MATERIAL_NAME,METADATA/DAMAGE_VALUE,AMOUNT'`. (Metadata/Damage value is often 0 for modern versions).
* **`weapontype`**: Classifies the weapon. Primarily used for organization and potentially by other plugins or specific features (like weapon limits). Can influence default sounds if `weaponsounds` isn't set. Valid types are: `PISTOL,SMG,RPG,RIFLE,SHOTGUN,FLAMER,SNIPER,BIG_GUN,GRENADES,SMOKE_GRENADES,FLASHBANGS,INCENDARY_GRENADES,MOLOTOV,PROXYMINES,STICKYGRENADE,MINES,MELEE,MISC,AMMO,HELMET,MEDKIT,AMMO_BAG,LAZER,BACKPACK,PARACHUTE,CUSTOM`
* **`weaponsounds`**: The sound event name(s) played when the weapon is fired. Can be a single string or a list of strings for variety. If omitted, a default sound based on `weapontype` is used.
* **`enableIronSights`**: If `true`, allows the player to aim down sights. Behavior depends on `IronSightsOnRightClick` in `config.yml`. If `false`, right-click (or the aim key) typically triggers reload.
* **`ammotype`**: The internal name of the `Ammo` item required to reload and fire this weapon. Must match the `name` field of an ammo configuration file. If omitted or invalid, the gun might have infinite ammo implicitly.
* **`damage`**: The base amount of damage each bullet inflicts upon hitting an entity. Headshots may multiply this.
* **`maxbullets`**: The maximum number of bullets the weapon can hold in its magazine/chamber.
* **`price`**: The cost of the weapon in the `/qa shop` if enabled.
  * If set to `-1` the gun won't show in the shop
* **`isPrimaryWeapon`**:  Used by the weapon limiter feature (if enabled in `config.yml`). `true` classifies it as a primary weapon, `false` as secondary.
* **`material`**: The base Minecraft item material used for this weapon. This affects the item's appearance if no resource pack is used and is crucial for resource packs to target the item. `CROSSBOW` and `BOW` allow for charging animations, `FISHING_ROD` can work for some effects, otherwise `DIAMOND_HOE` or other tools/items are common.
* **`invalid`**: If set to `true`, QualityArmory will ignore this file and not load the weapon. Useful for temporarily disabling a weapon without deleting the file.
* **`durability`**: The total durability points the weapon has if `EnableWeaponDurability` is `true` in `config.yml`. Each shot or configured action consumes durability.
* **`maxItemStack`**: The maximum stack size for this item in inventories. Typically `1` for weapons.
* **`setZoomLevel`**: Applies a Slowness effect to simulate scope zoom when aiming (if `enableIronSights` is true). `0` = no zoom, `1` = Slowness I, `2` = Slowness II, etc. Higher values mean more zoom.
* **`sway`**:  Configures weapon accuracy (spread). Lower values are more accurate.
  * **`defaultValue`**: The base sway value when standing still and not aiming/sneaking.
  * **`defaultMultiplier`**: A general multiplier applied to sway calculations, potentially affecting how much movement/running impacts accuracy.
  * **`sneakModifier`**: If `true`, sway is affected by the global `swayModifier_Sneak` from `config.yml` when the player is sneaking.
  * **`moveModifier`**: If `true`, sway is affected by the global `swayModifier_Walk` from `config.yml` when the player is walking.
  * **`runModifier`**: If `true`, sway is affected by the global `swayModifier_Run` from `config.yml` when the player is sprinting.
* **`firerate`**: For automatic weapons (`isAutomatic: true`), this controls how many shots are fired per "burst" or firing cycle. Often kept at `1`. The actual speed is controlled more by `delayForShoot`.
* **`delayForReload`**: The time in seconds it takes to reload the weapon.
* **`delayForShoot`**: The minimum time in seconds between consecutive shots (fire rate limiter). Lower values mean faster firing.
* **`bullets-per-shot`**: The number of bullets consumed and fired with each shot. Useful for shotguns (e.g., `8`), but each projectile calculates damage independently.
* **`isAutomatic`**: If `true`, the weapon can fire continuously when the fire button is held down (respecting `delayForShoot`). If `false`, it's semi-automatic (one shot per click).
* **`maxBulletDistance`**: The maximum distance (in blocks) a bullet will travel before despawning or stopping calculation.
* **`unlimitedAmmo`**: If `true`, the weapon does not consume ammo and does not need reloading.
* **`LightLeveOnShoot`**: If LightAPI is installed, creates a temporary light source with this light level (0-15) at the shooter's location when firing.
* **`recoil`**: The magnitude of vertical camera kick applied to the shooter when firing, if `enableRecoil` is true in `config.yml`.
* **`slownessOnEquip`**: Applies a permanent Slowness effect to the player while holding this weapon. `0` = no effect, `1` = Slowness I, `2` = Slowness II, etc.
* **`particles`**: Configures the bullet trail particle effect if `enableBulletTrails` is true in `config.yml`.
  * **`bullet_particle`**: The name of the particle type to use (from Bukkit `Particle` enum). `REDSTONE` is special as it allows custom colors via RGB.
  * **`bullet_particleR`**: Red component (0.0-1.0) for `REDSTONE` particles.
  * **`bullet_particleG`**: Green component (0.0-1.0) for `REDSTONE` particles.
  * **`bullet_particleB`**: Blue component (0.0-1.0) for `REDSTONE` particles.
  * **`bullet_particleData`**: Extra data value for certain particle types (often unused, 0).
  * **`bullet_particleMaterial`**: For particles like `BLOCK_DUST`, specifies the material whose texture is used.
* **`Version_18_Support`**:  A flag potentially used for compatibility adjustments or model selection when ViaVersion/ViaRewind are present, indicating if the model is primarily designed for 1.8 clients.
* **`ChargingHandler`**: Specifies a custom Java class (handler) to manage weapon charging behavior (e.g., bows, miniguns). `none` means no special charging mechanic. Requires custom coding to add new handlers.
* **`ReloadingHandler`**: Specifies a custom Java class (handler) to manage weapon reloading behavior (e.g., shotguns reloading one shell at a time). `none` uses the default magazine reload. Requires custom coding to add new handlers.
* **`addMuzzleSmoke`**: If `true`, spawns a brief smoke particle effect at the barrel when firing.
* **`drop-glow-color`**: If set to a valid ChatColor name (e.g., `RED`, `GOLD`), makes the dropped item entity glow with that color. `none` disables the glow.
* **`headshotMultiplier`**: The factor by which base `damage` is multiplied when a headshot occurs. `1.0` means no extra damage.
* **`weaponsounds_volume`**: Controls the volume multiplier for the firing sounds (`weaponsounds`).
* **`firing_knockback`**: Applies a knockback force _to the shooter_ when firing. Negative values push backwards.
* **`KilledByMessage`**: The custom death message format used when a player is killed by this weapon. Placeholders: `%player%` (victim), `%killer%` (attacker), `%name%` (weapon display name).
* **`DestructableMaterials`**: A list of block material names (e.g., `GLASS`, `WHITE_WOOL`) that bullets from this specific gun can break. Requires `enableExplosionDamage` or similar features to be enabled globally or per-weapon. `MATERIAL_NAME_HERE` is a placeholder.
