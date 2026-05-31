package com.mrfuzzihead.weather;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

import com.mrfuzzihead.weather.block.TileEntityAnemometer;
import com.mrfuzzihead.weather.block.TileEntityTSiren;
import com.mrfuzzihead.weather.block.TileEntityWeatherDeflector;
import com.mrfuzzihead.weather.block.TileEntityWeatherForecast;
import com.mrfuzzihead.weather.block.TileEntityWeatherMachine;
import com.mrfuzzihead.weather.block.TileEntityWindVane;
import com.mrfuzzihead.weather.client.block.TileEntityAnemometerRenderer;
import com.mrfuzzihead.weather.client.block.TileEntityTSirenRenderer;
import com.mrfuzzihead.weather.client.block.TileEntityWeatherDeflectorRenderer;
import com.mrfuzzihead.weather.client.block.TileEntityWeatherForecastRenderer;
import com.mrfuzzihead.weather.client.block.TileEntityWeatherMachineRenderer;
import com.mrfuzzihead.weather.client.block.TileEntityWindVaneRenderer;
import com.mrfuzzihead.weather.client.entity.RenderFlyingBlock;
import com.mrfuzzihead.weather.client.entity.RenderLightningBolt;
import com.mrfuzzihead.weather.client.entity.particle.EntityFallingRainFX;
import com.mrfuzzihead.weather.client.entity.particle.EntityFallingSnowFX;
import com.mrfuzzihead.weather.entity.EntityIceBall;
import com.mrfuzzihead.weather.entity.EntityLightningBolt;
import com.mrfuzzihead.weather.entity.EntityMovingBlock;
import com.mrfuzzihead.weather.util.WeatherUtilSound;

import CoroUtil.render.RenderNull;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static IIcon radarIconRain;
    public static IIcon radarIconLightning;
    public static IIcon radarIconWind;
    public static IIcon radarIconHail;
    public static IIcon radarIconTornado;
    public static IIcon radarIconCyclone;

    public static ClientTickHandler clientTickHandler;

    public ClientProxy() {
        clientTickHandler = new ClientTickHandler();
    }

    @Override
    public void init() {
        super.init();

        WeatherUtilSound.init();

        // MinecraftForge.EVENT_BUS.register(new SoundLoader());

        // TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);

        addMapping(EntityIceBall.class, new RenderFlyingBlock(Blocks.ice));
        addMapping(EntityMovingBlock.class, new RenderFlyingBlock(null));
        addMapping(EntityLightningBolt.class, new RenderLightningBolt());
        addMapping(EntityFallingRainFX.class, new RenderNull());
        addMapping(EntityFallingSnowFX.class, new RenderNull());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTSiren.class, new TileEntityTSirenRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindVane.class, new TileEntityWindVaneRenderer());
        ClientRegistry
            .bindTileEntitySpecialRenderer(TileEntityWeatherForecast.class, new TileEntityWeatherForecastRenderer());
        ClientRegistry
            .bindTileEntitySpecialRenderer(TileEntityWeatherMachine.class, new TileEntityWeatherMachineRenderer());
        ClientRegistry
            .bindTileEntitySpecialRenderer(TileEntityWeatherDeflector.class, new TileEntityWeatherDeflectorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAnemometer.class, new TileEntityAnemometerRenderer());
    }

    private static void addMapping(Class<? extends Entity> entityClass, Render render) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, render);
    }
}
