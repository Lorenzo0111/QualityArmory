# Armor Configuration

{% hint style="info" %}
I suggest copying another armor's configuration as a reference
{% endhint %}

These are all the fields used by an armor with their description:

* `invalid`: If set to `true`, this armor configuration file will be skipped during loading. Defaults to `false`
* `name` : The internal, unique identifier for this armor piece
* `displayname`: The name shown in-game for the armor item. Supports color codes (e.g., `&6`). Defaults to the `name` with white color if not specified
* `lore`: A list of strings that will appear as the item's lore (description below the name). Supports color codes
* `id`: The armor id, used as durability before Minecraft 1.13 and for custom model data for newer versions
* `variant`: Used for item variants. Defaults to 0
* `craftingRequirements`: Defines the materials needed to craft this armor. Each string follows the format `MATERIAL_NAME,DATA_VALUE,AMOUNT`
* `price`: The cost to purchase this armor from the shop. If the price is 0 or less, `allowInShop` defaults to false. Defaults to 100
* `allowInShop`: Explicitly sets whether this armor can be bought in the shop
* `MiscType`: Specifies the type of item. For armor, this should be `HELMET`
* `minProtectionHeight`: The minimum Y-coordinate relative to the player's feet where the helmet provides protection
* `maxProtectionHeight`: The maximum Y-coordinate relative to the player's feet where the helmet provides protection
* `protection`  The amount of armor points this item provides. Defaults to 0. This value is used to add the `generic.armor` attribute modifier to the item
* `material`: The Bukkit Material name for the item used as armor. Defaults to `DIAMOND_AXE` if not specified
