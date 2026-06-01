package com.mrfuzzihead.weather;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.WorldEvent.Save;

import com.mrfuzzihead.weather.weathersystem.WeatherManagerServer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandlerForge {

    @SubscribeEvent
    public void worldSave(Save event) {
        // WorldEvent.Save fires separately for every loaded dimension.
        // Only save the manager for the dimension that triggered this event;
        // calling writeOutData() here would redundantly write every dimension's
        // data on each individual dimension save (N² writes with N dimensions).
        int dim = event.world.provider.dimensionId;
        WeatherManagerServer wm = ServerTickHandler.lookupDimToWeatherMan.get(dim);
        if (wm != null) {
            try {
                wm.writeToFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void worldRender(RenderWorldLastEvent event) {
        ClientTickHandler.checkClientWeather();
        ClientTickHandler.weatherManager.tickRender(event.partialTicks);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerIcons(TextureStitchEvent event) {
        if (event.map.getTextureType() == 1) {
            ClientProxy.radarIconRain = event.map.registerIcon(Weather.modID + ":radar/radarIconRain");
            ClientProxy.radarIconLightning = event.map.registerIcon(Weather.modID + ":radar/radarIconLightning");
            ClientProxy.radarIconWind = event.map.registerIcon(Weather.modID + ":radar/radarIconWind");
            ClientProxy.radarIconHail = event.map.registerIcon(Weather.modID + ":radar/radarIconHail");
            ClientProxy.radarIconTornado = event.map.registerIcon(Weather.modID + ":radar/radarIconTornado");
            ClientProxy.radarIconCyclone = event.map.registerIcon(Weather.modID + ":radar/radarIconCyclone");
        }
    }
}
