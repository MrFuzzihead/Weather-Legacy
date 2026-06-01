package com.mrfuzzihead.weather;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.mrfuzzihead.weather.config.ConfigMisc;
import com.mrfuzzihead.weather.player.PlayerData;
import com.mrfuzzihead.weather.util.WeatherUtilConfig;
import com.mrfuzzihead.weather.weathersystem.WeatherManagerServer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import modconfig.ConfigMod;

// @NetworkMod(channels = { "WeatherData", "EZGuiData" }, clientSideRequired = true, serverSideRequired = true,
// packetHandler = WeatherPacketHandler.class)
@Mod(modid = Weather.MODID, name = Weather.MODNAME, version = Tags.VERSION)
public class Weather {

    public static final String MODID = "weather";
    public static final String MODNAME = "Weather";

    @Mod.Instance(value = Weather.MODID)
    public static Weather instance;
    public static String modID = "weather";

    public static long lastWorldTime;

    /** For use in preInit ONLY */
    public Configuration preInitConfig;

    @SidedProxy(clientSide = "com.mrfuzzihead.weather.ClientProxy", serverSide = "com.mrfuzzihead.weather.CommonProxy")
    public static CommonProxy proxy;

    public static boolean initProperNeededForWorld = true;

    public static String eventChannelName = "com/mrfuzzihead";
    public static final FMLEventChannel eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(eventChannelName);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        eventChannel.register(new EventHandlerPacket());
        MinecraftForge.EVENT_BUS.register(new EventHandlerForge());
        FMLCommonHandler.instance()
            .bus()
            .register(new EventHandlerFML());

        ConfigMod.addConfigFile(event, "weather2Misc", new ConfigMisc());
        WeatherUtilConfig.nbtLoadDataAll();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.init();

    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandWeather2());
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartedEvent event) {

    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppedEvent event) {
        writeOutData(true);
        resetStates();

        initProperNeededForWorld = true;
    }

    public static void initTry() {
        if (initProperNeededForWorld) {
            System.out.println("Weather2 being reinitialized");
            initProperNeededForWorld = false;

            ServerTickHandler.initialize();
        }
    }

    public static void resetStates() {
        ServerTickHandler.reset();
    }

    public static void writeOutData(boolean unloadInstances) {
        try {
            // Save all registered dimension managers, not just dim 0.
            // Non-overworld storm data was previously silently discarded on
            // every save cycle and at shutdown.
            for (WeatherManagerServer wm : ServerTickHandler.lookupDimToWeatherMan.values()) {
                wm.writeToFile();
            }
            PlayerData.writeAllPlayerNBT(unloadInstances);
            // doesn't cover all needs, client connected to server needs this called from gui close too
            // maybe don't call this from here so client connected to server doesn't override what a client wants his
            // 'server' settings to be in his singleplayer world
            // factoring in we don't do per world settings for this
            // WeatherUtilConfig.nbtSaveDataAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Triggered when communicating with other mods
     *
     * @param event
     */
    @EventHandler
    public void handleIMCMessages(IMCMessage event) {}

    public static void dbg(Object obj) {
        if (ConfigMisc.consoleDebug) {
            System.out.println(obj);
        }
    }
}
