# Kirmizi Alan — Tileman Mod

A Fabric mod for Minecraft 1.21.1 that implements the Tileman challenge.

## What is Tileman?
You start with a small 3x3 area. Kill mobs to earn tiles, then walk to unlock new territory. You cannot leave your unlocked area.

## Features
- Start with a 3x3 area
- Kill mobs to earn tiles
- Walk to unlock adjacent blocks
- Area border shown with red particles
- HUD showing available and unlocked tiles
- Ender pearl restricted to unlocked area
- Multi-dimension support (Overworld, Nether, End)
- Progress saved per world

## Installation
1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download `kirmizialan-1.0.0.jar` from [Releases](../../releases)
4. Place the jar in your `.minecraft/mods/` folder

## Requirements
- Minecraft 1.21.1
- Fabric Loader 0.18.2+
- Fabric API 0.139.4+

## Building from source
```bash
./gradlew build
```
Output: `build/libs/kirmizialan-1.0.0.jar`
