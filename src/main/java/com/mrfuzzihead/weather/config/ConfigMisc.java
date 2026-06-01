package com.mrfuzzihead.weather.config;

import com.mrfuzzihead.weather.util.WeatherUtil;
import com.mrfuzzihead.weather.util.WeatherUtilConfig;
import com.mrfuzzihead.weather.weathersystem.storm.StormObject;

import modconfig.ConfigComment;
import modconfig.IConfigCategory;

public class ConfigMisc implements IConfigCategory {

    // cleanup once GUI plan takes form

    // misc
    @ConfigComment("Enables Weather2's custom renderer for improved weather visuals (wind-angled rain, sky darkening, etc.). Disable only if you experience rendering glitches caused by conflicts with other mods.")
    public static boolean Misc_proxyRenderOverrideEnabled = true;
    // public static boolean Misc_takeControlOfGlobalRain = true;
    @ConfigComment("Enables the wind simulation system. Wind drives cloud movement, particle direction, and entity push forces during storms. Disabling this stops all wind-related effects across the mod.")
    public static boolean Misc_windOn = true;
    @ConfigComment("Radius in blocks around each player within which storm systems remain fully active and simulated. Low-intensity storms (below hail/tornado) that drift beyond this radius from ALL players are immediately removed. Dangerous storms (tornado, hail, high-wind) are exempt and decay naturally. Range: 64-4096. Default: 1024.")
    public static int Misc_simBoxRadiusCutoff = 1024;
    @ConfigComment("Radius in blocks around each player within which new cloud formations are allowed to spawn. Should be equal to or less than Misc_simBoxRadiusCutoff. Range: 64-4096. Default: 1024.")
    public static int Misc_simBoxRadiusSpawn = 1024;
    @ConfigComment("Forces Minecraft's default cloud layer off in the Overworld (dimension 0). Strongly recommended — prevents vanilla flat clouds from visually overlapping Weather2 volumetric cloud formations.")
    public static boolean Misc_ForceVanillaCloudsOff = true;
    @ConfigComment("How often storm and player data is automatically saved to disk, in ticks. 20 ticks = 1 real second. Default: 36000 = 30 minutes. Lower for more frequent saves (safer against crashes at the cost of disk I/O), higher for better performance.")
    public static int Misc_AutoDataSaveIntervalInTicks = 20 * 60 * 30;
    @ConfigComment("Enables verbose debug output to the game console and log file. Extremely chatty — only enable temporarily when diagnosing a specific issue.")
    public static boolean consoleDebug = false;

    // Weather
    @ConfigComment("When true (recommended), Weather2 storms only form while vanilla Minecraft rain is already active — vanilla rain acts as a trigger that Weather2 then intensifies. Keeps world.isRaining() truthful server-side, so other mods that check rain state (cauldrons, crop growth, etc.) work correctly. When false, Weather2 manages precipitation completely independently of vanilla rain, but forces world.isRaining()=false every tick which breaks most other mods.")
    public static boolean overcastMode = true;
    @ConfigComment("Controls the vanilla server rain state when overcastMode is false. 1 = force vanilla rain permanently ON, 0 = force vanilla rain permanently OFF (Weather2 handles all precipitation visuals), -1 = do not interfere (vanilla Minecraft controls rain normally). This setting is ignored when overcastMode is true.")
    public static int lockServerWeatherMode = 0; // is only used if overcastMode is off
    @ConfigComment("Prevents vanilla Minecraft thunderstorms from forcing world.isThundering()=true on the server. Recommended to keep enabled — Weather2 provides its own thunder and lightning system, and having both active simultaneously causes doubled lightning effects. Note: this applies in both overcastMode=true and overcastMode=false. In overcastMode=true, rain is unaffected so storms still form normally; however, world.isThundering() will always be false server-side, which may affect other mods that check the thundering state.")
    public static boolean preventServerThunderstorms = true;

    // Server tick rates
    @ConfigComment("How often (in ticks) the server checks whether vanilla rain has started or stopped, and notifies clients. 20 ticks = 1 real second. Default: 100 = every 5 seconds. Only meaningful when overcastMode is true. Lower values keep Weather2's sky darkening more responsive to vanilla rain changes (at a tiny bandwidth cost); higher values save bandwidth but add lag to overcast transitions. Range: 20-2000.")
    public static int tickerRateSyncWeatherCheckVanilla = 100;
    @ConfigComment("How often (in ticks) low-intensity storm data (rain, light overcast) is sent from the server to all clients. 20 ticks = 1 second. Default: 40 = every 2 seconds. Increase on high-player-count servers to reduce bandwidth. Range: 5-400.")
    public static int tickerRateSyncWeatherLowWind = 40;
    @ConfigComment("How often (in ticks) high-intensity storm data (hail, tornado, hurricane) is sent from the server to all clients. 20 ticks = 1 second. Default: 2 = ~10 times per second. Keep low for smooth tornado visuals; increase slightly under heavy server load. Range: 1-40.")
    public static int tickerRateSyncWeatherHighWind = 2;
    @ConfigComment("How often (in ticks) volcano data is sent from the server to clients. 20 ticks = 1 second. Default: 40 = every 2 seconds. Range: 5-400.")
    public static int tickerRateSyncVolcanoes = 40;
    @ConfigComment("How often (in ticks) wind direction/speed data and inter-mod communication (IMC) storm broadcasts are sent. 20 ticks = 1 second. Default: 60 = every 3 seconds. Range: 20-600.")
    public static int tickerRateSyncWindAndIMC = 60;
    @ConfigComment("How often (in ticks) the server runs storm spawn and cleanup checks. 20 ticks = 1 second. Default: 20 = every second. WARNING: This directly scales storm spawn frequency — doubling this value halves how often storms can form AND doubles how long it takes to clean up dissipated storms. Range: 5-200.")
    public static int tickerRateSyncStormSpawnOrRemoveChecks = 20;

    // tornado
    @ConfigComment("When true, tornadoes can grab and fling players into the air. Disable for a more forgiving experience.")
    public static boolean Storm_Tornado_grabPlayer = true;
    @ConfigComment("When true, tornadoes only grab players — all other entities (mobs, animals, dropped items) are ignored. Has no effect if Storm_Tornado_grabPlayer is false.")
    public static boolean Storm_Tornado_grabPlayersOnly = false;
    @ConfigComment("When true, tornadoes rip blocks from the ground and hurl them. Disable to prevent terrain destruction while keeping other tornado effects.")
    public static boolean Storm_Tornado_grabBlocks = true;
    @ConfigComment("When true, a block's grabbability is determined by how quickly a diamond axe could mine it — mostly wooden planks, leaves, and similar soft materials. Mutually exclusive with Storm_Tornado_GrabCond_List; if both are true, this takes priority.")
    public static boolean Storm_Tornado_GrabCond_StrengthGrabbing = true;
    @ConfigComment("When true, uses Storm_Tornado_GrabList to determine which blocks can be grabbed, instead of the diamond-axe strength calculation. Set Storm_Tornado_GrabCond_StrengthGrabbing to false when using this option.")
    public static boolean Storm_Tornado_GrabCond_List = false;
    @ConfigComment("When true, block names in Storm_Tornado_GrabList are matched as substrings (e.g. 'planks' matches 'oak_planks', 'birch_planks', etc.). When false, the block's full registry name must match exactly.")
    public static boolean Storm_Tornado_GrabCond_List_PartialMatches = false;
    // public static boolean Storm_Tornado_GrabCond_List_TrimSpaces = true;
    @ConfigComment("When true, Storm_Tornado_GrabList acts as a blacklist — the listed blocks are protected and cannot be grabbed. When false (default), only the listed blocks can be grabbed (whitelist mode).")
    public static boolean Storm_Tornado_GrabListBlacklistMode = false;
    @ConfigComment("Comma-separated list of block name substrings used when Storm_Tornado_GrabCond_List is true. Example: 'planks,leaves,log'. Whether this is a whitelist or blacklist is controlled by Storm_Tornado_GrabListBlacklistMode.")
    public static String Storm_Tornado_GrabList = "planks,leaves";
    @ConfigComment("Maximum number of blocks a single tornado can carry at once. Higher values allow larger debris clouds but increase memory and server CPU usage. Range: 1-2000. Default: 200.")
    public static int Storm_Tornado_maxBlocksPerStorm = 200;
    @ConfigComment("Maximum number of blocks a tornado can rip from the ground per server tick (20 ticks = 1 second). Higher values cause faster terrain destruction. Range: 1-50. Default: 5.")
    public static int Storm_Tornado_maxBlocksGrabbedPerTick = 5;
    @ConfigComment("1-in-N chance each tick that a block currently orbiting a tornado is permanently destroyed instead of being deposited on the ground when ejected. Lower = blocks destroyed more frequently, higher = blocks survive and land. Range: 1-1000. Default: 15 (~6.7% destruction rate per tick).")
    public static int Storm_Tornado_rarityOfDisintegrate = 15;
    @ConfigComment("1-in-N chance that a block breaks apart when it lands after being thrown by a tornado, rather than placing itself as a block. Lower = more breakage on landing. Range: 1-1000. Default: 5.")
    public static int Storm_Tornado_rarityOfBreakOnFall = 5;
    @ConfigComment("1-in-N chance per tornado tick that the tornado ignites nearby blocks, creating a 'firenado' fire effect. Set to -1 to disable entirely (default). Range: -1 (disabled) or 1-100000 (higher = rarer fire events).")
    public static int Storm_Tornado_rarityOfFirenado = -1;
    @ConfigComment("When true, prevents tornadoes from grabbing terrain blocks — specifically dirt, grass, sand, and logs. This overrides all other grab conditions and is recommended to prevent catastrophic landscape damage.")
    public static boolean Storm_Tornado_RefinedGrabRules = true;
    @ConfigComment("When true, a newly-formed tornado's initial heading is aimed toward the nearest player. Disable for fully random tornado movement.")
    public static boolean Storm_Tornado_aimAtPlayerOnSpawn = true;
    @ConfigComment("Random angular variation (in degrees) applied when aiming a tornado at a player on spawn. 0 = laser-precise tracking, higher = wider spread. Range: 0-180. Default: 5.")
    public static int Storm_Tornado_aimAtPlayerAngleVariance = 5;
    @ConfigComment("When true, storms can never escalate beyond the high-wind stage — no tornadoes or waterspouts will ever form. Storms will still rain and produce lightning. Good for peaceful or building-focused servers.")
    public static boolean Storm_NoTornadoesOrCyclones = false;
    @ConfigComment("1-in-N chance per spawn check that a high-wind storm over an ocean biome upgrades into a waterspout. Lower = waterspouts appear more often. Range: 1-10000. Default: 150.")
    public static int Storm_OddsTo1OfHighWindWaterSpout = 150;

    // storm
    @ConfigComment("When true, blocks hurled by a tornado deal damage to players and mobs they collide with. Disable to keep tornado visuals without the projectile damage.")
    public static boolean Storm_FlyingBlocksHurt = true;
    @ConfigComment("Maximum number of cloud formations (active storm systems) that can exist per player per cloud layer. Directly caps total storm count on the server (value * player count * layers). Lower values reduce network bandwidth and server load. Range: 1-100. Default: 20.")
    public static int Storm_MaxPerPlayerPerLayer = 20;
    @ConfigComment("Distance in blocks within which two separate storm systems can collide and merge into a single stronger combined storm. Range: 1-1024. Default: 128.")
    public static int Storm_Deadly_CollideDistance = 128;
    @ConfigComment("Base 1-in-N chance per tick that a thunderstorm produces a lightning strike. Lower = more frequent lightning. The actual chance decreases as storm intensity grows. Range: 1-10000. Default: 200.")
    public static int Storm_LightningStrikeBaseValueOddsTo1 = 200;
    @ConfigComment("When true, Weather2 storms do not show rain/snow particle effects. The storm's wind, lightning, and tornado mechanics still function normally. Useful for reducing particle-related performance impact.")
    public static boolean Storm_NoRainVisual = false;
    @ConfigComment("Maximum size radius (in blocks) that a storm system can grow to. Larger storms look more dramatic but have a wider impact radius. Range: 50-2048. Default: 300.")
    public static int Storm_MaxRadius = 300;
    @ConfigComment("How often (in ticks) the core storm progression logic runs: rainfall buildup, intensity stage changes, temperature adjustments, tornado escalation checks, etc. 20 ticks = 1 second. Default: 60 = every 3 seconds. Increasing this slows the entire storm lifecycle proportionally.")
    public static int Storm_AllTypes_TickRateDelay = 60;
    @ConfigComment("How many internal 'water units' a storm gains per progression tick when accumulating moisture. Works together with Storm_Rain_WaterBuildUpOddsTo1* (which controls whether buildup is attempted) and Storm_Rain_WaterBuildUp (the threshold to start raining). Higher = rain starts sooner. Range: 1-1000. Default: 10.")
    public static int Storm_Rain_WaterBuildUpRate = 10;
    @ConfigComment("How many internal 'water units' a storm consumes per progression tick while it is actively raining. When this drains the total to zero the storm stops raining. Should be less than Storm_Rain_WaterBuildUpRate to allow rain to sustain itself. Range: 1-1000. Default: 3.")
    public static int Storm_Rain_WaterSpendRate = 3;
    @ConfigComment("1-in-N chance per progression tick that a storm positioned over a water/ocean biome gains water units. Lower = rain starts sooner over oceans. Range: 1-1000. Default: 15.")
    public static int Storm_Rain_WaterBuildUpOddsTo1FromSource = 15;
    @ConfigComment("1-in-N chance per progression tick that a storm over land gains water units. Lower = rain starts sooner everywhere. Higher = longer dry spells before rain. Range: 1-1000. Default: 100.")
    public static int Storm_Rain_WaterBuildUpOddsTo1FromNothing = 100;
    @ConfigComment("Total internal 'water units' a storm must accumulate before it can start raining. Higher = longer dry period before precipitation begins. Works with Storm_Rain_WaterBuildUpRate and Storm_Rain_WaterBuildUpOddsTo1*. Range: 1-10000. Default: 150.")
    public static int Storm_Rain_WaterBuildUp = 150;
    @ConfigComment("How quickly a storm's internal temperature shifts toward the surrounding biome temperature each progression tick. Temperature determines whether a storm produces rain vs. snow. Higher = faster adaptation to biome climate. Range: 0.01-1.0. Default: 0.1.")
    public static double Storm_TemperatureAdjustRate = 0.1D;
    // public static double Storm_Deadly_MinIntensity = 5.3D;
    @ConfigComment("Number of hailstone entities spawned per tick during a hail event. Higher = denser, more damaging hail but more entity processing overhead. Range: 1-100. Default: 10.")
    public static int Storm_HailPerTick = 10;
    @ConfigComment("1-in-N chance per spawn check that a cloud formation over an ocean biome develops into a tropical cyclone or waterspout system. Lower = more frequent ocean storms. Range: 1-10000. Default: 300.")
    public static int Storm_OddsTo1OfOceanBasedStorm = 300;
    @ConfigComment("1-in-N chance per spawn check that a cloud formation over land escalates into a deadly storm (tornado, hail, high-wind event). Set to -1 to disable land-based deadly storms entirely (ocean storms are separately controlled by Storm_OddsTo1OfOceanBasedStorm). Range: -1 (disabled) or 1-10000. Default: -1.")
    public static int Storm_OddsTo1OfLandBasedStorm = -1;
    @ConfigComment("Base 1-in-N odds that a storm advances to the next intensity stage per progression check. Lower = storms escalate faster. This base value grows with each stage (via Storm_OddsTo1OfProgressionStageMultiplier), making each successive stage harder to reach. Range: 1-1000. Default: 15.")
    public static int Storm_OddsTo1OfProgressionBase = 15;
    @ConfigComment("Added to the progression odds for each intensity stage a storm has already reached: effective odds = base + (current stage * this value). Higher = each successive stage becomes progressively rarer to achieve. Range: 1-100. Default: 3.")
    public static int Storm_OddsTo1OfProgressionStageMultiplier = 3;
    @ConfigComment("Delay in ticks between storm visual particle spawn attempts on the server side. 0 = attempt every tick (densest visuals). Increase to thin out particle counts and reduce entity overhead. Range: 0-100. Default: 0.")
    public static int Storm_ParticleSpawnDelay = 0;

    // per player storm settings
    @ConfigComment("1-in-N chance per spawn check that a deadly storm (tornado, cyclone) forms targeting a specific player. Lower = deadly storms are more frequent per player. Only active when Server_Storm_Deadly_UseGlobalRate is false. Range: 1-10000. Default: 30.")
    public static int Player_Storm_Deadly_OddsTo1 = 30;
    @ConfigComment("Minimum time in ticks before another deadly storm can target the same player. 20 ticks = 1 second, 24000 ticks = 1 in-game day. Default: 72000 = 3 in-game days. Set to -1 to remove the cooldown entirely. Only active when Server_Storm_Deadly_UseGlobalRate is false.")
    public static int Player_Storm_Deadly_TimeBetweenInTicks = 20 * 60 * 20 * 3; // 3 mc days
    @ConfigComment("1-in-N chance per spawn check that a rain storm forms near a given player. Lower = rain is more frequent. Set to -1 to disable player-triggered rain entirely. Range: -1 (disabled) or 1-10000. Default: 150.")
    public static int Player_Storm_Rain_OddsTo1 = 150;

    // per server storm settings
    @ConfigComment("When true, deadly storm (tornado/cyclone) formation uses a single server-wide cooldown and spawn rate instead of tracking each player independently. Best for large servers where one tornado should affect everyone, not just one player.")
    public static boolean Server_Storm_Deadly_UseGlobalRate = false;
    @ConfigComment("1-in-N chance per spawn check that a deadly storm forms anywhere on the server. Only active when Server_Storm_Deadly_UseGlobalRate is true. Range: 1-10000. Default: 30.")
    public static int Server_Storm_Deadly_OddsTo1 = 30;
    @ConfigComment("Minimum time in ticks between server-wide deadly storm events. 20 ticks = 1 second, 24000 ticks = 1 in-game day. Default: 72000 = 3 in-game days. Set to -1 to remove the cooldown. Only active when Server_Storm_Deadly_UseGlobalRate is true.")
    public static int Server_Storm_Deadly_TimeBetweenInTicks = 20 * 60 * 20 * 3;

    // clouds
    @ConfigComment("Delay in ticks between cloud particle spawn attempts. 0 = attempt every tick. Increase to reduce visual particle density and improve client performance. Range: 0-100. Default: 0.")
    public static int Cloud_ParticleSpawnDelay = 0;
    @ConfigComment("Minimum distance in blocks that must separate two cloud formation spawn points. Prevents cloud systems from clustering together. Range: 64-4096. Default: 256.")
    public static int Cloud_Formation_MinDistBetweenSpawned = 256;
    @ConfigComment("Enables a second cloud layer (layer 1) rendered above the primary layer. Adds visual depth and realism but increases particle entity count. Layer 1 formations count toward the Storm_MaxPerPlayerPerLayer cap.")
    public static boolean Cloud_Layer1_Enable = false;
    @ConfigComment("Y-coordinate (height in blocks) at which the primary cloud layer (layer 0) renders. Default: 200 (safely above the build height). Lower values bring clouds closer to the ground. Changes applied via the in-game config GUI take effect immediately. Range: 64-512.")
    public static int Cloud_Layer0_Height = 200;

    // lightning
    @ConfigComment("1-in-N chance that a Weather2 lightning strike sets the struck location on fire. Lower = fires are more common. Set to -1 to prevent Weather2 lightning from ever starting fires. Range: -1 (no fire) or 1-10000. Default: 20.")
    public static int Lightning_OddsTo1OfFire = 20;
    @ConfigComment("The fire block metadata value assigned to fires ignited by lightning. In Minecraft, fire metadata represents the fire's age: 0 = freshly lit (burns longest), 15 = nearly extinguished (goes out quickly). Default: 3 (slightly aged, burns for a moderate duration). Range: 0-15.")
    public static int Lightning_lifetimeOfFire = 3;

    // snow
    @ConfigComment("When true, Weather2 storms over cold biomes (tundra, taiga, ice plains) cause snow to accumulate on the ground below them. The biome must naturally support snowfall for this to work.")
    public static boolean Snow_PerformSnowfall = false;
    @ConfigComment("When true (and Snow_PerformSnowfall is enabled), snow layers can stack on top of each other up to Snow_MaxBlockBuildupHeight layers. When false, snow is limited to a single layer per block.")
    public static boolean Snow_ExtraPileUp = false;
    @ConfigComment("1-in-N chance per tick that a snow layer is added to a valid surface block during a storm. Lower = snow accumulates faster. Range: 1-10000. Default: 64.")
    public static int Snow_RarityOfBuildup = 64;
    @ConfigComment("Maximum number of snow layers that can stack on a single block when Snow_ExtraPileUp is enabled. Range: 1-8 (8 = full block height). Default: 3.")
    public static int Snow_MaxBlockBuildupHeight = 3;
    @ConfigComment("When true, snow accumulation is applied gradually and smoothly rather than appearing instantly on each block. Results in a more natural-looking snowfall progression.")
    public static boolean Snow_SmoothOutPlacement = false;

    // particles
    @ConfigComment("When true, leaf particles are visually blown around by the wind. Purely cosmetic — disable to reduce particle count.")
    public static boolean Wind_Particle_leafs = true;
    @ConfigComment("Multiplier for the overall wind particle spawn rate. 1.0 = default density, 0.5 = half as many particles, 2.0 = twice as many. Range: 0.0-5.0. Default: 1.0.")
    public static double Wind_Particle_effect_rate = 1D;
    @ConfigComment("When true, small air/dust particles are shown blowing in the wind. Purely cosmetic — disable to reduce particle count.")
    public static boolean Wind_Particle_air = true;
    @ConfigComment("(No longer functional since version 1.3.2) Originally enabled sand particles blowing in the wind. This setting has no effect in current versions.")
    public static boolean Wind_Particle_sand = true;// not used since 1.3.2
    @ConfigComment("When true, waterfall splash particles are visually affected by wind direction. Purely cosmetic.")
    public static boolean Wind_Particle_waterfall = true;
    // public static boolean Wind_Particle_snow = false;
    @ConfigComment("When true, fire and ember particles are carried and scattered by the wind. Purely cosmetic.")
    public static boolean Wind_Particle_fire = true;
    @ConfigComment("When true, wind speed is constant with no gusts or lulls. When false, wind varies dynamically over time with gusts, calms, and directional shifts for a more realistic feel.")
    public static boolean Wind_NoWindEvents = true;
    @ConfigComment("How often the background particle processing thread runs, in milliseconds (not ticks). Lower = smoother particle animation but higher CPU usage. Range: 50-5000. Default: 400.")
    public static int Thread_Particle_Process_Delay = 400;
    @ConfigComment("When true, Weather2 storms spawn their own rain and snow particles above the player. Disable to remove precipitation particles from storms while keeping all other weather effects.")
    public static boolean Particle_RainSnow = true;
    @ConfigComment("When true, only particles from vanilla Minecraft (net.minecraft.*) and Weather2 are rendered — all other mods' particle effects are suppressed. Use this to improve performance in heavily-modded installs with excessive particle counts from other mods.")
    public static boolean Particle_VanillaAndWeatherOnly = false;
    @ConfigComment("Multiplier for the density of rain and snow particles spawned by Weather2 storms. 1.0 = default, 0.5 = half density, 2.0 = double density. Range: 0.0-5.0. Default: 1.0.")
    public static double Particle_Precipitation_effect_rate = 1D;

    // sound
    @ConfigComment("Volume multiplier for ambient wind sounds. 0.0 = completely silent, 1.0 = full volume. Default: 0.05 (subtle). Range: 0.0-1.0.")
    public static double volWindScale = 0.05D;
    @ConfigComment("Volume multiplier for waterfall ambient sounds near rivers and falls. 0.0 = silent, 1.0 = full volume. Range: 0.0-1.0. Default: 0.5.")
    public static double volWaterfallScale = 0.5D;
    @ConfigComment("Volume multiplier for the rustling/wind-through-trees sound during storms. 0.0 = silent, 1.0 = full volume. Range: 0.0-1.0. Default: 0.5.")
    public static double volWindTreesScale = 0.5D;

    // blocks
    @ConfigComment("(Unused — the Siren block no longer auto-activates based on storm proximity. It now requires a Redstone signal to play. This field is kept for config backwards-compatibility.) Range: 1-4096. Default: 256.")
    public static double sirenActivateDistance = 256D;
    @ConfigComment("Distance in blocks from a storm's center within which a placed Weather Sensor block will output a Redstone signal. Range: 1-4096. Default: 256.")
    public static double sensorActivateDistance = 256D;
    @ConfigComment("When true, the Weather Machine block can only create rain and thunderstorms — it cannot summon tornadoes or cyclones. Useful for allowing player-controlled weather without enabling tornado griefing on servers.")
    public static boolean Block_WeatherMachineNoTornadoesOrCyclones = false;

    // dimension settings
    @ConfigComment("Comma-separated list of dimension IDs where Weather2 manages precipitation (rain and snow). Common IDs: 0=Overworld, -1=Nether, 1=The End. Default: '0,-127' (Overworld + Weather2 internal dimension).")
    public static String Dimension_List_Weather = "0,-127";
    @ConfigComment("Comma-separated list of dimension IDs where cloud formations can spawn and be rendered. A dimension must appear here for any storm visuals to be visible in it. Common IDs: 0=Overworld, -1=Nether, 1=The End. Default: '0,-127'.")
    public static String Dimension_List_Clouds = "0,-127";
    @ConfigComment("Comma-separated list of dimension IDs where deadly storm events (tornadoes, cyclones, hail) can occur. A dimension can appear in Dimension_List_Weather without appearing here to get rain-only weather. Default: '0,-127'.")
    public static String Dimension_List_Storms = "0,-127";
    @ConfigComment("Comma-separated list of dimension IDs where wind particle effects (blowing leaves, dust, waterfall splashes) are active. Can be enabled independently of full storm simulation. Default: '0,-127'.")
    public static String Dimension_List_WindEffects = "0,-127";

    public ConfigMisc() {

    }

    @Override
    public String getConfigFileName() {
        return "weather";
    }

    @Override
    public String getCategory() {
        return "Weather2: Misc";
    }

    @Override
    public void hookUpdatedValues() {
        // Weather.dbg("block list processing disabled");
        WeatherUtil.doBlockList();
        WeatherUtilConfig.processLists();
        if (Storm_MaxRadius < 1) {
            Storm_MaxRadius = 1;
        }
        // Refresh the static_YPos_layer0 snapshot and the layers list so that a
        // runtime change to Cloud_Layer0_Height (e.g., via the in-game EZGui panel)
        // takes effect immediately for newly spawned cloud formations.
        StormObject.refreshLayerHeights();
    }

}
