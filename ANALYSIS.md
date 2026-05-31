# Weather-Legacy — Codebase Analysis

## Table of Contents
- [Critical / Severe Bug Fixes](#critical--severe-bug-fixes)
- [Feature Suggestions](#feature-suggestions)

---

## Critical / Severe Bug Fixes

### 1. 🔴 Blocks Permanently Deleted by Tornado When No Player Is Nearby

**Files:** `TornadoHelper.java`, `EntityMovingBlock.java`

In `TornadoHelper.tryRip`, a block is ripped from the world but an `EntityMovingBlock` is **only created if a player is within 140 blocks**. If no player is nearby, the block is removed from the world and no entity replaces it — the block is **permanently deleted** with no visual or gameplay feedback.

Separately, in `EntityMovingBlock.onUpdate()`:
```java
if (this.worldObj.getClosestPlayer(this.posX, 50, this.posZ, 140) == null) {
    setDead();
}
```
If a player moves away from an already-flying block, the entity kills itself. Since the block was already removed from the world when the entity was spawned, the block is **permanently gone**.

**Fix:** Either replace the block back into the world before dying, or always create an `EntityMovingBlock` regardless of player proximity and let gravity handle its fall.

---

### 2. 🔴 Incorrect 2D Distance Calculation in Tornado Pull Physics

**File:** `StormObject.java`, `spinEntity()` ~line 2108

```java
double distXZ = Math.sqrt(Math.abs(d1)) + Math.sqrt(Math.abs(d2));
```

This is **not** the 2D Euclidean distance. The correct formula is:
```java
double distXZ = Math.sqrt(d1 * d1 + d2 * d2);
```

The current formula grows much slower than actual distance and produces asymmetric results depending on which axis the entity is offset from the storm. This makes tornado pull forces inconsistent and inaccurate — entities at the same radius but at different angles experience different pull strengths.

---

### 3. 🔴 `new Random()` Instantiated Every Tick — Severe GC Pressure

**Files:** `StormObject.java`, `WindManager.java`, `WeatherManagerServer.java`

`Random` objects are created every single tick (20×/second) across the codebase:

| Location | Impact |
|---|---|
| `StormObject.tickWeatherEvents()` | Every tick, per storm |
| `StormObject.tickProgression()` | Every tick, per storm |
| `StormObject.spinEntity()` | Every entity pull call |
| `StormObject.spawnFogParticle()` | Every particle spawn |
| `WindManager` (~lines 149, 249) | Every wind tick |
| `WeatherManagerServer.tick()` | Every server tick |
| `WeatherManagerServer.trySpawnNearPlayerForLayer()` | Every spawn attempt |

With multiple storms active and dozens of players, this creates **thousands of short-lived `Random` objects per second**, causing excessive garbage collection pauses.

**Fix:** Make `Random` an instance field initialized once in the constructor for each class.

---

### 4. 🔴 `StormObject.addWeatherEffectLightning()` Casts to Server Manager Unconditionally

**File:** `StormObject.java`, ~line 2432

```java
public void addWeatherEffectLightning(EntityLightningBolt parEnt) {
    manager.getWorld().weatherEffects.add(parEnt);
    ((WeatherManagerServer) manager).syncLightningNew(parEnt);
}
```

This method is not `@SideOnly(Side.SERVER)` but unconditionally casts `manager` to `WeatherManagerServer`. If called from a client-side code path (where `manager` is `WeatherManagerClient`), this throws a `ClassCastException` and crashes the game.

**Fix:** Add a server-side guard (`!world.isRemote`) or annotate the method correctly.

---

### 5. 🟠 Storm Removed When No Player Nearby — Silent World-State Desync

**File:** `WeatherManagerServer.java`, ~lines 144–154

```java
EntityPlayer closestPlayer = world.getClosestPlayer(
    so.posGround.xCoord, so.posGround.yCoord, so.posGround.zCoord,
    ConfigMisc.Misc_simBoxRadiusCutoff);

if (closestPlayer == null) {
    removeStormObject(so.ID);
    syncStormRemove(so);
}
```

A fully-developed, deadly storm (e.g. a tornado at `STATE_STAGE5`) is silently removed the moment every player leaves the `simBoxRadiusCutoff` radius (default 1024 blocks). There is no wind-down, no persistence — the storm simply vanishes. Players who return to an area will see nothing where a violent tornado had been seconds before.

**Fix:** At minimum, let storms above `STATE_HIGHWIND` complete their decay cycle before removing them when players leave, or persist their state to disk.

---

### 6. 🟠 `EntityMovingBlock.vecX/Y/Z` Are Incremented But Never Used

**File:** `EntityMovingBlock.java`, lines 147–150

```java
this.vecX++;
this.vecY++;
this.vecZ++;
```

These fields were intended as controller-tracking vectors (see the commented-out block above), but the controller was removed. These fields now increment every tick doing nothing, and the `vecX/Y/Z` fields declared on the class shadow the parent's motion fields conceptually, creating confusion.

---

### 7. 🟠 Layer 1 Clouds Never Spawn — Dead Code Path

**File:** `WeatherManagerServer.java`, lines 171–178

```java
if (rand.nextInt(5) == 0) {
    // trySpawnNearPlayerForLayer(entP, 1);   // <-- commented out
}
```

The second cloud layer is configured (`Cloud_Layer1_Enable`, `Cloud_Layer0_Height`) but the spawn call is commented out. The infrastructure exists but layer 1 clouds never appear regardless of config.

---

### 8. 🟠 Default Block Grab List Breaks When List Mode Is Enabled

**File:** `ConfigMisc.java`, line 61

```java
public static String Storm_Tornado_GrabList = "planks, leaves";
```

The default list has a **space after the comma**. If `Storm_Tornado_GrabCond_List = true` and `Storm_Tornado_GrabCond_List_PartialMatches = false` (both are defaults), the entry `" leaves"` (with a leading space) will **not match** any block name. Only `"planks"` would work. The `Storm_Tornado_GrabCond_List_TrimSpaces` option is also commented out.

**Fix:** Trim whitespace when parsing the list, or change the default to `"planks,leaves"`.

---

### 9. 🟠 `blockify` Can Place a Block One Y-Level Too High

**File:** `EntityMovingBlock.java`, `blockify()`, lines 380–382

```java
if (!WeatherUtil.shouldRemoveBlock(var5) && !WeatherUtil.isOceanBlock(var5) && var2 < 255) {
    this.worldObj.setBlock(var1, var2 + 1, var3, this.tile, this.metadata, 3);
}
// ... then also places at var2:
if (this.worldObj.setBlock(var1, var2, var3, this.tile, this.metadata, 3)) { ... }
```

When a block lands, it attempts to place at **both `var2` and `var2 + 1`** if the target position is occupied. This causes dropped blocks to stack in unexpected locations, and in degenerate cases can place blocks at `y=256` (above the world limit), which is silently clamped or causes chunk corruption.

---

### 10. 🟡 `Vec3.createVectorHelper` Pool Mutation

**File:** `StormObject.java`, `tickWeatherEvents()` ~line 522

In Minecraft 1.7.10, `Vec3.createVectorHelper` returns objects from a shared pool. Storing the result in a field (`posGround`) and then mutating `.yCoord` directly is fragile — if the same pooled `Vec3` object is reused elsewhere in the same tick, `posGround.yCoord` can change unexpectedly.

**Fix:** Use a dedicated `Vec3` field (or a simple `double` for the y coordinate) rather than relying on a pooled `Vec3` for persistent state.

---

## Feature Suggestions

### 1. 🌪️ Tornado Warning System / Siren Range Mechanic
Currently, storm progression happens silently to players not watching the sky. A **siren block** exists in the config (`Block_sirenID`) but there's no automatic triggering logic tied to storm intensity stages. The progression from `STATE_NORMAL` → `STATE_STAGE5` takes many minutes — giving players a procedural warning system (chat message, sound cue, or automatic siren activation) when a storm reaches `STATE_HIGHWIND` or `STATE_DEADLY` would greatly improve gameplay feel.

---

### 2. 🌊 Sandstorm / Dust Storm Weather Type
The mod already supports `TYPE_WATER` (waterspout) as a biome-aware storm variant. Desert and mesa biomes have no special weather behavior. A **sandstorm** variant could:
- Deal gradual damage or blindness to unshielded players
- Move sand blocks via the existing `EntityMovingBlock` system
- Reduce visibility with custom particle fog (the `EntityRotFX` / cloud particle system already handles this)
- Only spawn in `temperature >= 2.0` biomes (desert, desert hills, hell)

---

### 3. ❄️ Blizzard Event
Cold biomes (`temperature <= 0.05`: taiga, ice plains, frozen river) have precipitation but no deadly weather type. A **blizzard** could:
- Use the existing storm progression stages to escalate over time
- Apply slowness and freezing damage at high intensity stages
- Use snow accumulation (`Snow_PerformSnowfall` / `Snow_ExtraPileUp`) driven by storm intensity
- Fill the gap between "rain" and "tornado" for cold climate gameplay

---

### 4. 🌈 Rainbow Post-Rain Effect
After a storm's `attrib_precipitation` flag turns off and `levelWater` drains below `levelWaterStartRaining`, spawn a short-lived rainbow particle arc in the sky. This uses the existing `EntityRotFX` particle system and the cloud position/angle data already tracked per storm. A purely cosmetic but highly atmospheric touch.

---

### 5. 💨 Wind-Powered Block / Anemometer Integration
`Block_anemometer` and `Block_windVaneID` are defined in config but there is no visible generation of Redstone signals or power based on wind speed. Hooking `WindManager.getWindSpeed()` into a Redstone output from the Anemometer block (stronger wind = stronger signal) would let players build **wind-powered farms, doors, and contraptions** that react to real in-game weather conditions.

---

### 6. 🌊 Flood Mechanic for Heavy Rain
The `levelWater` and `levelWaterStartRaining` values already track rainfall accumulation per storm. At extreme `levelWater` values, the mod could temporarily raise water level in low-lying areas (y < 64) near the storm, simulating flash flooding. The existing ocean-block detection (`WeatherUtil.isOceanBlock`) and block manipulation infrastructure in `EntityMovingBlock`/`TornadoHelper` provide a solid foundation.

---

### 7. 🌫️ Dense Fog Weather Event
A low-intensity, non-rain weather event that only uses the cloud particle system (fog layer) with very high density, greatly reducing render distance client-side. The fog particle spawning and cloud brightness are already parameterized in `spawnFogParticle()`. This would be a calm but eerie event type distinct from storms.

---

### 8. 🌀 Hurricane Eye — Calm Zone at Storm Center
For cyclone/hurricane types (`TYPE_WATER`), the eye of the storm (within ~20 blocks of `posGround`) is the most dangerous area in reality but is actually the calm center. Adding a peaceful zone at the center (no precipitation, reduced wind, sky visible) surrounded by the violent eyewall would make cyclones feel authentic and add an interesting risk/reward mechanic for players brave enough to reach it.

---

### 9. ⚡ Lightning Rod Block Integration
The mod spawns custom `EntityLightningBolt` instances with configurable fire odds. A **lightning rod** block (could reuse `Block_weatherDeflectorID` or a new ID) that intercepts nearby strikes and redirects them to itself (with an optional Redstone pulse output) would complement the existing lightning system and give players a way to protect builds in thunderstorm-prone areas.

---

### 10. 📡 Inter-Mod Weather API Improvements (IMC)
The `nbtStormsForIMC()` method already broadcasts storm positions, types, and intensity via FML IMC every 60 ticks. Expanding this to include:
- **Forecast data** (predicted storm path based on `stormMoveDir` and `stormMoveSpeed`)
- **Wind direction/speed** (already sent separately via `syncWindUpdate`)
- A **query channel** where other mods can request weather at a specific coordinate

...would make Weather-Legacy a proper weather backbone for modpacks rather than a standalone experience.

