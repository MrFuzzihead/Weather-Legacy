package com.mrfuzzihead.weather.block;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

import com.mrfuzzihead.weather.ClientTickHandler;
import com.mrfuzzihead.weather.Weather;
import com.mrfuzzihead.weather.config.ConfigMisc;
import com.mrfuzzihead.weather.util.WeatherUtilSound;
import com.mrfuzzihead.weather.weathersystem.storm.StormObject;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTSiren extends TileEntity {

    public long lastPlayTime = 0L;
    public long lastVolUpdate = 0L;
    // public int soundID = -1;
    public int lineBeingEdited = -1;

    public void updateEntity() {
        if (worldObj.isRemote) {
            tickClient();
        }
    }

    @SideOnly(Side.CLIENT)
    public void tickClient() {
        // Only play when driven by a redstone signal.
        // Redstone wire/lever power levels are part of block metadata,
        // which is always synced server→client, so this check is safe client-side.
        if (!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) || this.lastPlayTime > System.currentTimeMillis()) {
            return;
        }

        this.lastPlayTime = System.currentTimeMillis() + 13000L;
        WeatherUtilSound.playNonMovingSound(
            Vec3.createVectorHelper(xCoord, yCoord, zCoord),
            Weather.modID + ":streaming.siren",
            1.0F,
            1.0F,
            120);
    }

    public void writeToNBT(NBTTagCompound var1) {
        super.writeToNBT(var1);
    }

    public void readFromNBT(NBTTagCompound var1) {
        super.readFromNBT(var1);

    }
}
