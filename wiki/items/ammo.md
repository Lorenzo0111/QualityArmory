# Ammo Configuration

{% hint style="info" %}
I suggest copying another ammo's configuration as a reference
{% endhint %}

These are all the fields used by an ammo with their description:

* `invalid`: If set to `true`, this ammo configuration file will be skipped during loading. Defaults to `false`
* `name`: The internal, unique identifier for this ammo type. Used for referencing this ammo in gun configurations
* `displayname`: The name shown in-game for the ammo item. Supports color codes (e.g., `&f`). Defaults to the `name` if not specified
* `lore`: A list of strings that will appear as the item's lore (description below the name). Supports color codes
* `id`: The ammo id, used as durability before Minecraft 1.13 and for custom model data for newer versions
* `variant`: Used for item variants (e.g., different wood types, potion effects). Defaults to 0
* `craftingRequirements`: Defines the materials needed to craft this ammo. Each string follows the format `MATERIAL_NAME,DATA_VALUE,AMOUNT`
* `craftingReturnAmount`: The quantity of ammo items produced when crafted using the `craftingRequirements`
* `price`: The cost to purchase this ammo from the shop. If the price is 0 or less, `allowInShop` defaults to false. Defaults to 100
* `allowInShop`: Explicitly sets whether this ammo can be bought in the shop. Defaults to `true` if `price` is greater than 0
* `maxItemStack`: The maximum number of this ammo item that can be held in a single inventory stack
* `material`: The Bukkit Material name for the item used as ammo (e.g., `PHANTOM_MEMBRANE`, `IRON_NUGGET`)
* `skull_owner`: If `material` is `PLAYER_HEAD`, this sets the head's owner by player name
* `skull_owner_custom_url`: If `material` is `PLAYER_HEAD`, this sets the head's skin using a Base64 texture URL
* `piercingSeverity`: Multiplier applied to the base gun damage
