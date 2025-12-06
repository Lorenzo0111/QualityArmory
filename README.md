<div align="center">

![QualityArmory](images/banner.png)

[![CurseForge](https://img.shields.io/curseforge/dt/278412?style=for-the-badge&logo=curseforge&label=CurseForge&color=%23F16436)](https://www.curseforge.com/minecraft/bukkit-plugins/qualityarmory)
[![Spigot](https://img.shields.io/spiget/downloads/47561?style=for-the-badge&logo=spigotmc&color=%23ED8106&label=Spigot)](https://www.spigotmc.org/resources/quality-armory.47561/)
[![Modrinth](https://img.shields.io/modrinth/dt/qualityarmory?style=for-the-badge&logo=modrinth&color=%231BD96A&label=Modrinth)](https://modrinth.com/plugin/qualityarmory)

**A powerful, feature-rich gun plugin for Minecraft servers**

</div>

---

## ✨ Features

### 🎯 Core Functionality

- **Custom Guns** - Create unlimited custom weapons with unique stats, sounds, and behaviors
- **Ammunition System** - Realistic ammo management with different ammo types
- **Armor & Protection** - Custom armor sets with bullet protection mechanics
- **Attachments** - Weapon attachments system for enhanced customization
- **Resource Pack Support** - Custom 3D models and textures via resource packs
- **Multi-Version Support** - Works across different Minecraft versions with ViaVersion

### 🛠️ Advanced Features

- **Recoil System** - Realistic weapon recoil with smooth camera movement (ProtocolLib)
- **Muzzle Flashes** - Dynamic lighting effects when firing (LightAPI)
- **Friendly Fire Protection** - Party-based friendly fire system (Parties)
- **Economy Integration** - Weapon shops with Vault support
- **NPC Integration** - Citizens NPC support for custom gun-wielding NPCs
- **GUI System** - Intuitive menu system for crafting, shopping, and item management

### 🔌 Plugin Integrations

QualityArmory seamlessly integrates with popular plugins:

- **Protection**: WorldGuard, GriefPrevention, Residence, Towny
- **Economy**: Vault, ChestShop, QuickShop
- **AntiCheats**: Vulcan, Matrix, Spartan
- **Utilities**: PlaceholderAPI, ProtocolLib, ViaVersion, LightAPI
- **NPCs**: Citizens, Sentinel
- **Items**: ItemBridge, Mimic

---

## 📥 Download

[![Download on Modrinth](https://img.shields.io/badge/Download-Modrinth-%231BD96A?style=for-the-badge&logo=modrinth)](https://modrinth.com/plugin/qualityarmory)
[![Dev Builds](https://img.shields.io/badge/Download-Dev%20Builds-blue?style=for-the-badge&logo=github)](https://ci.codemc.io/job/ZombieStriker/job/QualityArmory/lastSuccessfulBuild/artifact/target/QualityArmory.jar)

---

## 🚀 Quick Start

1. **Download** the latest QualityArmory.jar
2. **Place** it in your server's `plugins` folder
3. **Start** your server
4. **Configure** guns, ammo, and items in the generated config files
5. **Enjoy!** No additional dependencies required

### Optional Enhancements

While QualityArmory works standalone, these plugins add extra features:

- **LightAPI** - Muzzle flash lighting effects
- **ProtocolLib** - Smooth recoil animations
- **Vault** - Economy integration for weapon shops
- **ViaVersion** - Cross-version compatibility
- **Parties** - Friendly fire protection

---

## 👨‍💻 For Developers

### Maven Dependency

Add QualityArmory to your project:

```xml
<repository>
    <id>codemc-repo</id>
    <url>https://repo.codemc.io/repository/maven-public/</url>
</repository>

<dependency>
    <groupId>me.zombie_striker</groupId>
    <artifactId>QualityArmory</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

> **📦 Version Note:** Replace `VERSION` with the latest release version
>
> [![GitHub Release](https://img.shields.io/github/v/release/Lorenzo0111/QualityArmory?style=logo=github&color=lightgray&label=Latest%20Version)](https://github.com/Lorenzo0111/QualityArmory/releases/latest)

### API Documentation

Check out the [Developer Documentation](https://wiki.lorenzo0111.me/qualityarmory/developers/api) for API usage examples and integration guides.

---

## 📚 Documentation

The documentation is available on our [website](https://wiki.lorenzo0111.me/qualityarmory):

---

## 🎮 Commands & Permissions

### Main Commands

- `/qa` or `/qualityarmory` - Main command
- `/qa give <player> <item>` - Give items to players
- `/qa reload` - Reload configuration
- `/qa shop` - Open weapon shop
- `/qa craft` - Open crafting menu

### Key Permissions

- `qualityarmory.*` - All permissions
- `qualityarmory.admin.*` - Admin commands
- `qualityarmory.give` - Give items
- `qualityarmory.shop` - Access shop
- `qualityarmory.craft` - Access crafting
- `qualityarmory.usegun` - Use weapons

---

## 🤝 Contributing

Contributions are welcome! Feel free to:

- Report bugs
- Suggest features
- Submit pull requests
- Improve documentation

---

## 👥 Authors

- **Zombie_Striker** - Original Creator
- **Lorenzo0111** - Maintainer

---

## 📄 License

See the [LICENSE](LICENSE) file for details.

---

## 🔗 Links

- [Spigot Resource Page](https://www.spigotmc.org/resources/quality-armory.47561/)
- [CI/CD Builds](https://ci.codemc.io/job/ZombieStriker/job/QualityArmory/)
- [Maven Repository](https://repo.codemc.io/repository/maven-public/)
