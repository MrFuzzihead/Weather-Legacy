package com.mrfuzzihead.weather.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.mrfuzzihead.weather.ServerTickHandler;
import com.mrfuzzihead.weather.config.ConfigMisc;
import com.mrfuzzihead.weather.weathersystem.WeatherManagerServer;
import com.mrfuzzihead.weather.weathersystem.storm.StormObject;

public class BlockTSensor extends Block {

    public BlockTSensor() {
        super(Material.clay);
        this.setTickRandomly(true);
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
        this.updateTick(var1, var2, var3, var4, var5);
    }

    @Override
    public void onBlockAdded(World var1, int var2, int var3, int var4) {
        // Schedule the first detection tick immediately so the sensor begins
        // checking as soon as it is placed, instead of waiting for a random tick
        // which can take ~60+ seconds to fire.
        if (!var1.isRemote) {
            var1.scheduleBlockUpdate(var2, var3, var4, this, this.tickRate(var1));
        }
    }

    @Override
    public void updateTick(World var1, int var2, int var3, int var4, Random var5) {

        if (var1.isRemote) return;

        boolean enable = false;

        WeatherManagerServer wms = ServerTickHandler.lookupDimToWeatherMan.get(var1.provider.dimensionId);
        if (wms != null) {
            // Iterate storm objects directly and use XZ-only distance against posGround.
            // The old getClosestStorm() used storm.pos (cloud altitude ~200 blocks up) for
            // the 3D euclidean distance, which inflated the distance by the sensor's Y gap
            // and silently shrank the effective horizontal detection radius.
            List<StormObject> storms = wms.getStormObjects();
            for (int i = 0; i < storms.size(); i++) {
                StormObject so = storms.get(i);
                if (so.isDead) continue;
                if (so.levelCurIntensityStage < StormObject.STATE_FORMING) continue;
                double dx = so.posGround.xCoord - var2;
                double dz = so.posGround.zCoord - var4;
                if (Math.sqrt(dx * dx + dz * dz) <= ConfigMisc.sensorActivateDistance) {
                    enable = true;
                    break;
                }
            }
        }

        // flags = 3 (0x1 | 0x2): notify neighbours (server-side redstone) AND send
        // a block-data packet to clients so their metadata stays in sync.
        // The old flags=2 skipped the client update, leaving client metadata at 0
        // and causing client-side power queries to always read 0.
        if (enable) {
            var1.setBlockMetadataWithNotify(var2, var3, var4, 15, 3);
        } else {
            var1.setBlockMetadataWithNotify(var2, var3, var4, 0, 3);
        }

        /*
         * if(var7.size() > 0) {
         * var1.setBlockMetadataWithNotify(var2, var3, var4, 15);
         * } else {
         * var1.setBlockMetadataWithNotify(var2, var3, var4, 0);
         * }
         */
        var1.notifyBlocksOfNeighborChange(var2, var3 - 1, var4, this);
        var1.notifyBlocksOfNeighborChange(var2, var3 + 1, var4, this);
        var1.notifyBlocksOfNeighborChange(var2, var3, var4, this);
        var1.markBlockRangeForRenderUpdate(var2, var3, var4, var2, var3, var4);
        var1.scheduleBlockUpdate(var2, var3, var4, this, this.tickRate(var1));
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
        return var1.getBlockMetadata(var2, var3, var4) == 0 ? 0 : 15;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
        return var1.getBlockMetadata(var2, var3, var4) == 0 ? 0 : 15;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_,
        EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        p_149727_5_.setPosition(p_149727_2_ + 0.5F, p_149727_3_ + 1.5F, p_149727_4_ + 0.5F);
        p_149727_5_.getEntityData()
            .setBoolean("inBedCustom", true);
        return super.onBlockActivated(
            p_149727_1_,
            p_149727_2_,
            p_149727_3_,
            p_149727_4_,
            p_149727_5_,
            p_149727_6_,
            p_149727_7_,
            p_149727_8_,
            p_149727_9_);
    }
}
