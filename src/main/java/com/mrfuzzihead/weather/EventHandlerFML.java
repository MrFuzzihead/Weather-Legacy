package com.mrfuzzihead.weather;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import com.mrfuzzihead.weather.client.SceneEnhancer;
import com.mrfuzzihead.weather.weathersystem.WeatherManagerServer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandlerFML {

    public static World lastWorld = null;

    @SubscribeEvent
    public void tickWorld(WorldTickEvent event) {
        if (event.phase == Phase.START) {

        }
    }

    @SubscribeEvent
    public void tickServer(ServerTickEvent event) {

        if (event.phase == Phase.START) {
            // System.out.println("tick weather2");
            ServerTickHandler.onTickInGame();

            /*
             * System.out.println("total: " + DimensionManager.getWorld(0).getTotalWorldTime());
             * System.out.println("not: " + DimensionManager.getWorld(0).getWorldTime());
             */
        }

    }

    @SubscribeEvent
    public void tickClient(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            ClientProxy.clientTickHandler.onTickInGame();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void tickRenderScreen(RenderTickEvent event) {
        if (event.phase == Phase.END) {
            ClientProxy.clientTickHandler.onRenderScreenTick();
        } else if (event.phase == Phase.START) {
            // fix for sky flicker with global overcast on and transitioning between vanilla weather states
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.thePlayer != null && mc.theWorld != null) {
                EntityPlayer entP = mc.thePlayer;
                float curRainStr = SceneEnhancer.getRainStrengthAndControlVisuals(entP, true);
                curRainStr = Math.abs(curRainStr);
                mc.theWorld.setRainStrength(curRainStr);
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerLoggedInEvent event) {
        ServerTickHandler.playerJoinedServerSyncFull((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void playerChangedDimension(PlayerChangedDimensionEvent event) {
        // When a player travels to a new dimension the client creates a fresh
        // WeatherManagerClient with no storms. Trigger a full server-to-client
        // sync so existing storms in the destination dimension are immediately
        // visible — without this, the player would see no storms until the next
        // periodic sync tick (up to 40 ticks / 2 seconds for low-intensity ones).
        WeatherManagerServer wm = ServerTickHandler.lookupDimToWeatherMan.get(event.toDim);
        if (wm != null) {
            wm.playerJoinedServerSyncFull((EntityPlayerMP) event.player);
        }
    }
}
