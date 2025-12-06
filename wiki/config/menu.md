# Menu Configuration

`menu.yml` controls the new GUI system for `/qa shop` and `/qa craft` (categories, item pages, navigation). Edit and save, then reload the plugin.

## Categories

- Defined under `categories.shop` and `categories.crafting`; the order in the file is the order shown in the GUI.
- Each category entry includes:
  - **`name`**: Button label (supports `&` colors).
  - **`icon.type`**: Bukkit material for the icon item.
  - **`icon.name` / `icon.lore`**: Custom name and lore for the icon item.
  - **`filter`**: Controls which registry items are loaded (case-insensitive). Supported filters in the current code:
    - `ALL`: Everything from guns, ammo, misc, armor.
    - `GUN`: Only `Gun` objects.
    - `AMMO`: Only `Ammo` objects.
    - `ARMOR`: Only `ArmorObject` items.
    - `MISC`: Items that are not Gun/Ammo/Armor (anything else registered).
    - Any other value falls back to `ALL`.
- Add a new category by copying an existing block:

```yaml
categories:
  shop:
    explosives:
      name: "&cExplosives"
      icon:
        type: "TNT"
        name: "&c&lExplosives"
        lore:
          - "&7All explosive weapons"
        filter: "MISC" # must be one of: ALL, GUN, AMMO, ARMOR, MISC
```

## Menu layouts

- Layouts live under `menus.*`:
  - `shop_categories` / `crafting_categories`: Category selector menus.
  - `shop_items` / `crafting_items`: Paginated item menus (`title` accepts `%category%`).
- **`structure`**: Each row is 9 characters (inventory width). Characters map to slots left→right, top→bottom:
  - `.` → Dynamic slots (category buttons or items, filled in order)
  - `#` → Decorative filler from `items.custom.#`
  - `B` → Back to categories
  - `<` → Previous page button
  - `>` → Next page button
- Example bottom row in items views: `B # # # < # > # #` puts Back on the first slot, Previous at slot 5, Next at slot 7 (0-indexed within the row).
- To add more decorative blocks, insert another symbol in `structure` (e.g., `*`) and define it under `items.custom.*`. Rows shorter/longer than 9 are ignored.

## Buttons & custom items

- `items.previous` / `items.next`: Navigation buttons. Supports `%prevpage%`, `%nextpage%` and `%pages%`.
- `items.back`: Returns to the category selector defined by the current mode (shop or crafting).
- `items.custom`: Maps every non-special symbol used in `structure` to an item. The symbol is the first character of the key. Default `#` is a black stained glass pane. Add more keys for extra fillers or separators:

```yaml
items:
  custom:
    "*":
      type: "GRAY_STAINED_GLASS_PANE"
      name: "&r"
      lore: []
```

## Placeholders

- `%category%`: Current category display name (used in titles).
- `%prevpage%` / `%nextpage%` / `%pages%`: Current and total pages (used in navigation lore).

## Tips

- Shop and crafting must be enabled in `config.yml` (`enableShop`, `enableCrafting`) for the menus to be reachable.
- Shop pages only show items with `price >= 0` and `enableShop: true`. Crafting pages only show items that have `ingredients` configured.
- If a button symbol in `structure` has no matching item definition, that slot stays empty.
- Keep row lengths to 9 characters—shorter or longer rows are ignored. Use spaces only inside quotes if you need visual separators; otherwise prefer symbols mapped via `items.custom`.
