package com.mrfuzzihead.weather.weathersystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;

import com.mrfuzzihead.weather.config.ConfigMisc;

public class EntityRendererProxyWeather2Mini extends EntityRenderer {

    public EntityRendererProxyWeather2Mini(Minecraft var1, IResourceManager resMan) {
        super(var1, resMan);
    }

    @Override
    protected void renderRainSnow(float par1) {

        boolean overrideOn = ConfigMisc.Misc_proxyRenderOverrideEnabled;

        if (!overrideOn) {
            super.renderRainSnow(par1);
            return;
        } else {

            // note, the overcast effect change will effect vanilla non particle rain distance too, particle rain for
            // life!
            if (!ConfigMisc.Particle_RainSnow) {
                super.renderRainSnow(par1);
            }

        }
    }
}
