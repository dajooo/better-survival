# Better Survival

A feature-rich Minecraft Paper plugin that enhances the survival gameplay experience with various quality-of-life improvements.

## Features

### 🌾 Crop Right Click
Farm and automatically replant crops by right-clicking them when fully grown. Supports:
- Beetroot
- Wheat
- Potatoes
- Carrots
- Nether Wart

### 🏠 Homes System
Set multiple teleport points throughout your world:
- Set homes with `/sethome [name]`
- Teleport to homes using `/home [name]`
- List all homes with `/homes`
- Delete homes using `/deletehome [name]`
- Teleport to your last location using `/back`

### 🪓 Timber
Cut down entire trees instantly while sneaking:
- Break connected logs and leaves automatically
- Tool durability is properly consumed
- Configurable block limit
- Works with all vanilla tree types

### 🪓 Vein Miner
Break whole ore veins instantly while sneaking:
- Break connected ores automatically
- Tool durability is properly consumed
- Configurable block limit

### 🌱 Sapling Twerk
Make saplings grow faster by sneaking near them:
- Affects all nearby saplings in a 2-block radius
- Configurable growth chance
- Works with all vanilla tree types

### 🛏️ Better Beds
Skip the night with only a percentage of players sleeping:
- Configurable minimum percentage of players needed
- Customizable morning time
- Shows sleeping progress in chat
- Works per-world

### 🛠️ Custom Recipes
Adds various quality-of-life crafting recipes:
- **Slabs to Blocks**: Convert slabs back to full blocks (supports all vanilla slab types)
- **Horse Armor**: Craft horse armor using respective materials (Gold, Iron, Diamond) + Wool
- **Logs to Chests**: Craft 4 chests from logs (any type)
- **Logs to Sticks**: Craft 8 sticks directly from logs
- **Rotten Flesh to Leather**: Smelt rotten flesh into leather
- **Magma Block to Cream**: Convert magma blocks into 4 magma cream
- **Name Tag**: Craft name tags using string, iron, and leather

### 🪦 Graves
When a player dies, their inventory is dropped as a chest:
- Chest contains all items from the player's inventory
- An text display is shown above the chest with the player's name
- Only breaakable by the player who died or players with the `bettersurvival.graves.break` permission

## Installation

1. Download the latest release from the [releases page](https://github.com/dajooo/better-survival/tags)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Optional: Configure the plugin in `plugins/BetterSurvival/config.yml`

## Requirements

- Paper 1.21.4 or higher
- Java 21 or higher
- Optional: LuckPerms for permissions support

## Configuration

Features can be individually enabled/disabled and configured in the config files:

### Timber Configuration
```yaml
timber:
  enabled: true
  limit: 512  # Maximum number of blocks to break at once
```

### Sapling Twerk Configuration
```yaml
sapling-twerk:
  enabled: true
  chance: 10  # Percentage chance of growth boost (0-100)
```

### Crop Right Click Configuration
```yaml
crop-right-click:
  enabled: true
  allowedCropSeeds:
    - BEETROOT_SEEDS
    - WHEAT_SEEDS
    - POTATO
    - CARROT
    - NETHER_WART
```

### Better Beds Configuration
```yaml
better-beds:
  enabled: true
  minPercentage: 50  # Minimum percentage of players needed to skip night
  morningTime: 1000  # Time to set when skipping to morning
```

### Custom Recipes Configuration
```yaml
custom-recipes:
  enabled: true
  slabs: true           # Enable slab to block recipes
  horseArmor: true      # Enable horse armor recipes
  logsToChest: true     # Enable logs to chest recipe
  logsToSticks: true    # Enable logs to sticks recipe
  rottenFleshToLeather: true  # Enable rotten flesh to leather recipe
  magmaBlockToMagmaCream: true  # Enable magma block to cream recipe
  nameTag: true         # Enable name tag recipe
```

## Building from Source

### Prerequisites
- JDK 21 or higher
- Gradle

### Steps
1. Clone the repository:
```bash
git clone https://github.com/dajooo/better-survival.git
```

2. Build the project:
```bash
./gradlew build
```

The built JAR file will be located in `build/libs/`.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you encounter any issues or have questions:
1. Check the [GitHub Issues](https://github.com/dajooo/better-survival/issues)
2. Create a new issue if your problem hasn't been reported yet

---

Made with ❤️ by [dajooo](https://dario.lol)
