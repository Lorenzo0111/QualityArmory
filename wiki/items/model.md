# Creating the Model

I suggest watching a tutorial on YouTube to learn how to create a model, here is a suggested one:

{% embed url="https://youtu.be/wm-giZnkUk4?si=uEGsoA-zT1St5_OO" %}

### Choosing a Model Editor

* **For Simple Models:**\
  MrCrayfish’s Model Creator works well for basic guns.
* **For Advanced Models:**\
  [BlockBench](http://blockbench.net) is recommended. It supports groups and complex display settings, making it ideal for first-person/off-hand configuration and iron sight alignment.

### Exporting the Model

1. Build your gun model in BlockBench.
2. Export it as a `.json` file once complete.
3. **Name the file the same as the gun** (e.g., `ak47.json`). This keeps things organized and makes it easier to update models later.
   * **Note:** From Minecraft **1.12+**, filenames **must be all lowercase**

### Adding Iron Sights

If you want to add iron sights:

* Set **first-person off-hand display** values — QA uses off-hand for scoping, but it appears in the **right hand**.
* Also define the **third-person display values** for proper iron sight positioning.
* You can use existing models in the pack as references to copy values or setups.

### Integrating the Model in Resource Packs

#### Minecraft 1.9 – 1.13

You’ll need to modify the `diamond_axe.json` file at `/assets/minecraft/models/item/diamond_axe.json`

Find the JSON object structure that looks like this:

```json
{
    "predicate": {
        "damaged": 0,
        "damage": 0.0161
    },
    "model": "item/aa12"
},
```

Insert a new entry after the closing }, like so:

```json
{
    "predicate": {
        "damaged": 0,
        "damage": ID
    },
    "model": "item/MODEL"
}
```

Replace the placeholders:

```yaml
ID:
Calculated as 0.0161 + (0.006 * index)
(e.g., ID 27 → 0.0167, ID 28 → 0.0173, etc.)

MODEL:
The model file name (without .json)
```

#### Minecraft 1.14+

In 1.14+, Mojang introduced the custom\_model\_data tag — a cleaner way to add models.\
You can modify `/assets/minecraft/models/item/crossbow.json` and add a new entry:

Here’s an example structure:

```json
{
    "predicate": {
        "custom_model_data": 66
    },
    "model": "item/stengun"
},
{
    "predicate": {
        "custom_model_data": 67
    },
    "model": "item/m32a1"
},
{
    "predicate": {
        "custom_model_data": 68
    },
    "model": "item/aiming"
}
```

Add your custom gun model:

```json
{
    "predicate": {
        "custom_model_data": ID
    },
    "model": "item/MODEL"
}
```

Replace the placeholders:

```yaml
ID:
This should match the custom_model_data value set in your plugin/mod (e.g., in the YAML config).

MODEL:
Name of your model file (no .json extension).
```
