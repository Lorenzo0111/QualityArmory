# QualityArmory API

The `me.zombie_striker.qg.api.QualityArmory` class provides static methods for interacting with the QualityArmory plugin, allowing other plugins to access information about guns, ammo, armor, and miscellaneous items, as well as perform actions like giving items or checking player states.

### **Core Item Management & Creation**

* **`createAndLoadNewGun(String name, String displayname, Material material, int id, WeaponType type, WeaponSounds sound, boolean hasIronSights, String ammotype, int damage, int maxBullets, int cost)`**
  * **Description**: Creates a new gun configuration file (`.yml`) in the `plugins/QualityArmory/newGuns/` directory and immediately attempts to load it into the running server.
  * **Parameters**:
    * `name`: Internal name for the gun (used in commands).
    * `displayname`: In-game display name (supports color codes).
    * `material`: Base `Material` for the item.
    * `id`: CustomModelData (1.14+) or Durability (<1.14) value.
    * `type`: The `WeaponType` enum value (e.g., `PISTOL`, `RIFLE`).
    * `sound`: The `WeaponSounds` enum value for firing.
    * `hasIronSights`: `true` if the gun should support aiming down sights.
    * `ammotype`: Internal name of the required `Ammo` item.
    * `damage`: Base damage per bullet.
    * `maxBullets`: Magazine capacity.
    * `cost`: Price in the `/qa shop`.
  * **Returns**: A `GunYML` object representing the created configuration (allows further modification before saving, though this method saves immediately).
  * **Note**: Creates a file and schedules a task to load it.
* **`createNewGunYML(String name, String displayname, Material material, int id, WeaponType type, WeaponSounds sound, boolean hasIronSights, String ammotype, int damage, int maxBullets, int cost)`**
  * **Description**: Creates a `GunYML` object representing a new gun configuration but _does not_ automatically save or load it. You would need to call methods on the returned `GunYML` object to save it.
  * **Parameters**: Same as `createAndLoadNewGun`.
  * **Returns**: A `GunYML` object representing the potential configuration.
* **`registerNewUsedExpansionItem(Material used, int id)`**
  * **Description**: Registers a specific Material/ID combination as being used by an expansion pack or addon. This helps prevent ID conflicts when QualityArmory searches for available IDs. Assumes variant 0.
  * **Parameters**:
    * `used`: The `Material` used by the expansion item.
    * `id`: The CustomModelData/Durability value used.
* **`registerNewUsedExpansionItem(Material used, int id, int var)`**
  * **Description**: Registers a specific Material/ID/Variant combination as being used by an expansion pack or addon.
  * **Parameters**:
    * `used`: The `Material` used by the expansion item.
    * `id`: The CustomModelData/Durability value used.
    * `var`: The variant value used.
* **`registerNewUsedExpansionItem(MaterialStorage ms)`**
  * **Description**: Registers a `MaterialStorage` object (which encapsulates Material, ID, and Variant) as being used by an expansion pack or addon.
  * **Parameters**:
    * `ms`: The `MaterialStorage` object representing the item.

### **Retrieving Registered Items**

* **`getGuns()`**: Returns an `Iterator<Gun>` for all loaded guns.
* **`getAmmo()`**: Returns an `Iterator<Ammo>` for all loaded ammo types.
* **`getMisc()`**: Returns an `Iterator<CustomBaseObject>` for all loaded miscellaneous items (like medkits, ammo bags).
* **`getArmor()`**: Returns an `Iterator<ArmorObject>` for all loaded armor pieces.
* **`getCustomItems()`**: Returns an `Iterator<CustomBaseObject>` containing all loaded guns, ammo, armor, and misc items combined.
* **`getCustomItemsAsList()`**: Returns a `List<CustomBaseObject>` containing all loaded guns, ammo, armor, and misc items combined.
* **`getGun(ItemStack is)`**: Returns the `Gun` object associated with the given `ItemStack`, or `null` if it's not a QA gun.
* **`getAmmo(ItemStack is)`**: Returns the `Ammo` object associated with the given `ItemStack`, or `null` if it's not QA ammo.
* **`getArmor(ItemStack is)`**: Returns the `ArmorObject` associated with the given `ItemStack`, or `null` if it's not QA armor.
* **`getMisc(ItemStack is)`**: Returns the `CustomBaseObject` (often castable to a specific type like `AmmoBag`) associated with the given `ItemStack`, or `null` if it's not a QA misc item.
* **`getCustomItem(ItemStack is)`**: Returns the `CustomBaseObject` (Gun, Ammo, Armor, or Misc) associated with the given `ItemStack`, or `null` if it's not a QA item.
* **`getCustomItem(Material material, int data, int variant)`**: Returns the `CustomBaseObject` matching the specified Material, CustomModelData/Durability, and Variant, or `null`.
* **`getCustomItem(MaterialStorage material)`**: Returns the `CustomBaseObject` matching the specified `MaterialStorage`, or `null`.
* **`getGunByName(String name)`**: Returns the `Gun` object with the matching internal name, or `null`.
* **`getAmmoByName(String name)`**: Returns the `Ammo` object with the matching internal name, or `null`.
* **`getArmorByName(String name)`**: Returns the `ArmorObject` with the matching internal name, or `null`.
* **`getMiscByName(String name)`**: Returns the `CustomBaseObject` (Misc) with the matching internal name, or `null`.
* **`getCustomItemByName(String name)`**: Returns the `CustomBaseObject` (any type) with the matching internal name, or `null`.
* **`getGunInHand(@NotNull HumanEntity entity)`**: Returns the `Gun` the player is effectively holding (checks main hand and off-hand for iron sights), or `null`.

### **Checking Item Types**

* **`isCustomItem(ItemStack is)`**: Returns `true` if the `ItemStack` is any registered QA item (Gun, Ammo, Armor, Misc, or registered Expansion item).
* **`isCustomItem(ItemStack is, int dataOffset)`**: Checks if the item would be a custom item if its CustomModelData/Durability were offset by `dataOffset`. Useful for checking durability changes.
* **`isCustomItemNextId(ItemStack is)`**: Checks if an item with the _next_ sequential CustomModelData/Durability value (same material) is a registered QA item.
* **`isGun(ItemStack is)`**: Returns `true` if the `ItemStack` is a registered QA gun.
* **`isAmmo(ItemStack is)`**: Returns `true` if the `ItemStack` is a registered QA ammo type.
* **`isArmor(ItemStack is)`**: Returns `true` if the `ItemStack` is a registered QA armor piece.
* **`isMisc(ItemStack is)`**: Returns `true` if the `ItemStack` is a registered QA miscellaneous item.
* **`isAmmoBag(ItemStack is)`**: Returns `true` if the `ItemStack` is a registered QA miscellaneous item that is specifically an `AmmoBag`.
* **`isIronSights(ItemStack is)`**: Returns `true` if the `ItemStack` represents the temporary item shown when aiming down sights.

### **Player Interaction & State**

* **`sendResourcepack(final Player player, final boolean warning)`**
  * **Description**: Sends the appropriate server resource pack to the player after a configured delay (`SecondsTillRPIsSent`). Uses the multi-version system if enabled.
  * **Parameters**:
    * `player`: The player to send the pack to.
    * `warning`: If `true`, sends warning messages/titles before sending the pack prompt.
  * **Note**: Schedules tasks. Relies on `config.yml` settings for URLs, delays, and behavior. Requires ViaVersion for version detection if `Auto-Detect-Resourcepack` is true.
* **`allowGunsInRegion(Location loc)`**
  * **Description**: Checks with the registered protection hooks (like WorldGuard) if PvP or gun usage is allowed at the specified location.
  * **Parameters**:
    * `loc`: The `Location` to check.
  * **Returns**: `true` if allowed, `false` otherwise. Returns `true` if no protection hooks deny it or if hooks fail.
* **`sendHotbarGunAmmoCount(final Player p, final CustomBaseObject gun, ItemStack usedItem, boolean reloading)`**
  * **Description**: Sends the ammo count/status message above the player's hotbar. Uses the format defined in `config.yml`.
  * **Parameters**:
    * `p`: The player to send the message to.
    * `gun`: The `Gun` or `AttachmentBase` object being used.
    * `usedItem`: The `ItemStack` representing the gun in the player's hand.
    * `reloading`: `true` if the player is currently reloading this weapon.
* **`sendHotbarGunAmmoCount(final Player p, final CustomBaseObject gun, ItemStack usedItem, boolean reloading, int currentAmountInGun, int maxAmount)`**
  * **Description**: Sends the ammo count/status message above the player's hotbar, allowing manual specification of current/max ammo (useful for custom reload handlers).
  * **Parameters**:
    * `p`: The player.
    * `gun`: The `Gun` or `AttachmentBase`.
    * `usedItem`: The `ItemStack`.
    * `reloading`: `true` if reloading.
    * `currentAmountInGun`: The current ammo count to display.
    * `maxAmount`: The maximum ammo capacity to display.
* **`getAmmoInInventory(Player player, Ammo a)`**
  * **Description**: Calculates the total amount of a specific `Ammo` type the player has in their main inventory, including amounts stored in `AmmoBag` items. Returns `99999` if the player is in Creative mode.
  * **Parameters**:
    * `player`: The player whose inventory to check.
    * `a`: The `Ammo` type to count.
  * **Returns**: The total count of the specified ammo.
* **`getAmmoInInventory(Player player, Ammo a, boolean ignoreBag)`**
  * **Description**: Calculates the amount of a specific `Ammo` type the player has. Can optionally ignore ammo bags.
  * **Parameters**:
    * `player`: The player.
    * `a`: The `Ammo` type.
    * `ignoreBag`: If `true`, does not count ammo inside `AmmoBag` items.
  * **Returns**: The total count of the specified ammo.
* **`getAmmoInBag(@NotNull Player player, Ammo a)`**
  * **Description**: Calculates the total amount of a specific `Ammo` type stored _only_ within `AmmoBag` items in the player's inventory.
  * **Parameters**:
    * `player`: The player.
    * `a`: The `Ammo` type.
  * **Returns**: The total count within ammo bags.
* **`addAmmoToInventory(Player player, Ammo a, int amount)`**
  * **Description**: Attempts to add the specified amount of ammo to the player's inventory, stacking with existing ammo items first, then filling empty slots.
  * **Parameters**:
    * `player`: The player.
    * `a`: The `Ammo` type to add.
    * `amount`: The quantity to add.
  * **Returns**: `true` if all ammo was successfully added, `false` if there wasn't enough space.
* **`removeAmmoFromInventory(Player player, Ammo a, int amount)`**
  * **Description**: Attempts to remove the specified amount of ammo from the player's inventory, taking from main inventory stacks first, then from `AmmoBag` items if necessary. Does nothing if the player is in Creative mode.
  * **Parameters**:
    * `player`: The player.
    * `a`: The `Ammo` type to remove.
    * `amount`: The quantity to remove.
  * **Returns**: `true` if the specified amount was successfully removed, `false` if the player didn't have enough ammo.
* **`getBulletsInHand(Player player)`**: Returns the current ammo count stored in the NBT data of the gun item the player is holding.
* **`isOverLimitForPrimaryWeapons(Gun g, Player p)`**
  * **Description**: Checks if adding the specified gun (`g`) would put the player (`p`) over the primary or secondary weapon limit configured in `config.yml` (if the limiter is enabled).
  * **Parameters**:
    * `g`: The `Gun` being checked.
    * `p`: The player.
  * **Returns**: `true` if the player is already at or above the limit for that weapon type (primary/secondary), `false` otherwise.
* **`giveOrDrop(HumanEntity entity, ItemStack item)`**
  * **Description**: Gives the specified `ItemStack` to the entity. If their inventory is full, drops the item at their location instead.
  * **Parameters**:
    * `entity`: The `HumanEntity` (usually a `Player`) to give the item to.
    * `item`: The `ItemStack` to give.

### **Item & Utility Methods**

* **`getCustomItemAsItemStack(String name)`**: Retrieves a `CustomBaseObject` by its internal name and returns a fresh `ItemStack` representation of it.
* **`getCustomItemAsItemStack(CustomBaseObject obj)`**: Returns a fresh `ItemStack` representation of the given `CustomBaseObject`. Returns `null` if the input object is `null`.
* **`getIronSightsItemStack()`**: Returns the specific `ItemStack` used to represent the aiming-down-sights view.
* **`findSafeSpot(ItemStack newItem, boolean findHighest, boolean allowPockets)`**: Searches for an unused CustomModelData/Durability value for the given item's Material, starting from the item's current value and searching up (`findHighest = true`) or down (`findHighest = false`). `allowPockets` determines if it searches for any gap or just the absolute highest/lowest used ID. Primarily for internal use or advanced item creation.
* **`findSafeSpot(Material newItemtype, int startingData, boolean findHighest, boolean allowPockets)`**: Same as above, but takes Material and starting data directly.
* **`findSafeSpotVariant(ItemStack newItem, boolean findHighest)`**: Searches for an unused Variant value for the given item's Material and CustomModelData/Durability, starting from 0 and searching up (`findHighest = true`) or down (`findHighest = false`).
* **`findSafeSpotVariant(Material newItemtype, int startingData, boolean findHighest)`**: Same as above, but takes Material and data directly.
* **`getMaxPagesForGUI()`**: Calculates the number of pages needed to display all registered QA items in the default shop/GUI layout (5 rows of 9 items per page).
* **`repeat(String string, int times)`**: A simple utility method to repeat a given string multiple times. Used internally for formatting reload progress bars.
