# Weather-Legacy — Codebase Analysis

## Table of Contents
- [Critical / Severe Bug Fixes](#critical--severe-bug-fixes)
- [Feature Suggestions](#feature-suggestions)

---

## Critical / Severe Bug Fixes

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



