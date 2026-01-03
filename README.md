# OreDetector - Minecraft Fabric Mod

A server-side Minecraft Fabric mod for Minecraft 1.21.1 that detects and logs ore mining activities with permission support.

## Features

### 🔍 **Ore Detection**
- Detects only the most valuable ores: Diamond Ore, Deepslate Diamond Ore, and Ancient Debris
- Focused detection prevents spam and highlights the most important finds

### ⛏️ **Vein Detection**
- Groups connected ore blocks into veins (including diagonally connected blocks)
- Advanced 26-block detection pattern captures complex vein shapes
- Sends single notification per vein (not per block)
- Prevents spam when mining large ore deposits

### 🔐 **Permission System**
- Integrates with LuckPerms (optional)
- Two permission nodes:
  - `oredetector.detectionmessages` - Receive real-time mining notifications
  - `oredetector.orelog` - Access to ore log command
- Falls back to operator permissions if LuckPerms unavailable

### 📊 **Real-time Notifications**
- Simple, clean notifications when players discover ore veins
- Format: "[MineDetector] PlayerName found X ore type"
- No spam - one message per vein discovered

### 📋 **Ore Logging & History**
- Detailed logging of all mining activities
- Command: `/orelog [hours]` (default: 24 hours)
- Shows timestamp, player, ore count, coordinates, and dimension
- Includes teleport commands for easy navigation
- Command: `/orelog [hours]` (default: 24 hours)
- Searchable history with timestamps
- Memory-efficient with automatic cleanup

## Commands

### `/orelog [hours]`
View ore mining history for the specified time period.

**Examples:**
- `/orelog` - Show last 24 hours
- `/orelog 6` - Show last 6 hours  
- `/orelog 168` - Show last week (max)

**Output includes:**
- Timestamp of mining event
- Player name
- Number of ores in vein
- Ore type
- Exact coordinates
- Dimension

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `oredetector.detectionmessages` | Receive real-time ore mining notifications | Operators |
| `oredetector.orelog` | Access to `/orelog` command | Operators |

## Installation

1. Download the latest release
2. Place the `.jar` file in your server's `mods` folder
3. Install Fabric Loader if not already installed
4. (Optional) Install LuckPerms for advanced permission management
5. Restart your server

## Configuration

Currently, the mod works out-of-the-box with no configuration required.

## Technical Details

- **Minecraft Version:** 1.21.1
- **Mod Loader:** Fabric
- **Dependencies:** Fabric API
- **Optional Dependencies:** LuckPerms
- **Server-side only:** Yes

## Example Usage

When a player mines a diamond vein:
```
[OreDetector] Steve found 8 diamond ore
```

Using the log command:
```
/orelog 2
=== Ore Log - Last 2 Hours ====
Found 3 mining events:
[Jan 03 14:32:15] Steve mined 8x diamond at 123, -45, 678 (Overworld) (Click to teleport: /tp @s 123 -45 678)
[Jan 03 13:45:22] Alex mined 12x iron at 234, 56, 789 (Overworld) (Click to teleport: /tp @s 234 56 789)
[Jan 03 13:12:08] Steve mined 4x gold at -45, 32, -123 (Nether) (Click to teleport: /tp @s -45 32 -123)
```