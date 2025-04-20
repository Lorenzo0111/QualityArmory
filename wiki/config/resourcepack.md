# ResourcePack Configuration

This guide explains how to configure QualityArmory to send the correct resource pack to your players.

### **Basic Setup & Auto-Detection (Recommended):**

* **`useDefaultResourcepack: true`**: Keep this `true` when using the auto-detection system. It tells the plugin to use the URLs defined under the `DefaultResourcepack:` section below.
* **`DefaultResourcepack:`**: This section defines the different resource pack URLs for various Minecraft versions.
  * You **must** have an entry with the key `"0"`. This is the **fallback URL** used if no specific version matches or if the player's version cannot be detected.
  * Add entries for specific Minecraft versions using the format `"minor"` or `"minor-patch"`. The plugin will select the _highest_ version entry that is _less than or equal to_ the player's version.
  * Keys should be strings (use quotes).
  * This system requires [ViaVersion](https://www.spigotmc.org/resources/19254/) installed to work.

**Example `DefaultResourcepack` Section:**

```yaml
# ... other config settings ...

useDefaultResourcepack: true

DefaultResourcepack:
  # Fallback/Default URL (REQUIRED)
  '0': 'https://your-default-pack-url.zip'
  # Pack for 1.19.0 up to (but not including) 1.20.0
  '19': 'https://your-1.19.x-pack-url.zip'
  # Pack specifically for 1.20.0 up to (but not including) 1.20.2
  '20': 'https://your-1.20.0-pack-url.zip'
  # Pack for 1.20.2 up to (but not including) 1.21.0
  '20-2': 'https://your-1.20.2-pack-url.zip'
  # Pack for 1.21.0 and newer (until a higher version is added)
  '21': 'https://your-1.21.x-pack-url.zip'

# ... other config settings ...
```

**How it works with the example:**

* Player joins with 1.18.2 -> Gets the `'0'` pack.
* Player joins with 1.19.4 -> Gets the `'19'` pack.
* Player joins with 1.20.1 -> Gets the `'20'` pack.
* Player joins with 1.20.4 -> Gets the `'20-2'` pack.
* Player joins with 1.21.0 -> Gets the `'21'` pack.

### **Manual Override (Alternative - Not Recommended unless needed):**

* If you don't want auto-detection or don't have ViaVersion, you can force the plugin to use _one specific_ set of models/systems.
* Set `Auto-Detect-Resourcepack: false`.
* Set **one** of the following to `true`:
  * `ManuallyOverrideTo_1_8_systems: true` (For 1.8 style models/mechanics)
  * `ManuallyOverrideTo_1_13_systems: true` (For 1.9-1.13 style models/mechanics)
  * `ManuallyOverrideTo_1_14_systems: true` (For 1.14+ style models/mechanics)
* In this case, the plugin will use the fallback `'0'` URL from `DefaultResourcepack`. This setup is less flexible and generally only used for troubleshooting or specific server setups.

### **Using a Single Custom URL (Simple):**

* If you only have _one_ resource pack URL you want _everyone_ to use, regardless of version:
* Set `useDefaultResourcepack: false`.
* Set `DefaultResourcepackOverride: true`.
* Define your single URL under `DefaultResourcepack:` :

```yaml
# ... other config settings ...

useDefaultResourcepack: false
DefaultResourcepackOverride: true

DefaultResourcepack: 'https://your-single-custom-pack-url.zip'

# ... other config settings ...
```
