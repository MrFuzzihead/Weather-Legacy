package com.mrfuzzihead.weather.block;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

import com.mrfuzzihead.weather.ServerTickHandler;
import com.mrfuzzihead.weather.config.ConfigMisc;
import com.mrfuzzihead.weather.weathersystem.WeatherManagerServer;
import com.mrfuzzihead.weather.weathersystem.storm.StormObject;

public class TileEntityWeatherMachine extends TileEntity {

    // gui ideas

    /*
     * Activity Mode: Locked on / Locked off / Time cycle
     * Weather Type: Snow / Rain / Deadly Storm
     * if activity mode on delay, otherwise just track last storm the tile entity made and wait for it to be dead or
     * really far?:
     * Weather Rate: 5 / 10 / 20 / 30 / 1 hr / 2 hr
     * Size: ya
     */

    // 0 = snow (no, dont use anymore), 1 = rain, 2 = F1 tornado, 3 = stage 1 cyclone
    public int weatherType = 1;
    // 0 = lightning, 1 = F1, 2 = F2, etc (snow would use this to increase snow rate maaaaaaaybbbeeeeee, needs more vars
    // in StormObject)
    public int weatherIntensity = 0;
    // 0 = uhh
    public int weatherRate = 0;
    // ya
    public int weatherSize = 50;
    // prevent storm moving via wind
    public boolean lockStormHere = true;

    public StormObject lastTickStormObject = null;

    /**
     * The ID of the storm that was active when the world was last saved.
     * Non-zero only between readFromNBT() and the first successful reconnect
     * in updateEntity(). Cleared once the lookup either succeeds or confirms
     * the storm no longer exists.
     */
    private long savedStormID = 0L;

    public void cycleWeatherType() {
        weatherType++;
        int maxID = 6;
        if (ConfigMisc.Storm_NoTornadoesOrCyclones || ConfigMisc.Block_WeatherMachineNoTornadoesOrCyclones) {
            maxID = 4;
        }
        if (weatherType > maxID) {
            weatherType = 1; // skip snow
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        WeatherManagerServer wm = ServerTickHandler.lookupDimToWeatherMan.get(worldObj.provider.dimensionId);
        if (wm != null) {
            // StormObject lastTickStormObject = wm.getClosestStorm(Vec3.createVectorHelper(xCoord,
            // StormObject.layers.get(0), zCoord), deflectorRadius, StormObject.STATE_NORMAL, true);

            if (lastTickStormObject != null) {

                wm.removeStormObject(lastTickStormObject.ID);
                wm.syncStormRemove(lastTickStormObject);
            }
        }
    }

    public void updateEntity() {
        if (!worldObj.isRemote) {

            // TEMP
            weatherSize = 100;

            // weatherType = 3;

            if (worldObj.getTotalWorldTime() % 40 == 0) {

                if (lastTickStormObject != null && lastTickStormObject.isDead) {
                    lastTickStormObject = null;
                }

                if (lastTickStormObject == null) {
                    WeatherManagerServer manager = ServerTickHandler.lookupDimToWeatherMan
                        .get(worldObj.provider.dimensionId);

                    if (manager != null) {
                        // Bug fix: on server restart the weather manager reloads the storm
                        // from disk and the machine's lastTickStormObject is null, so the
                        // machine used to immediately create a second orphan storm.
                        // Instead, try to reconnect to the storm that was active before
                        // the restart by looking it up with the ID we saved to disk.
                        if (savedStormID != 0L) {
                            StormObject existing = manager.lookupStormObjectsByID.get(savedStormID);
                            if (existing != null && !existing.isDead) {
                                // Reclaim the storm: naturallySpawned is not persisted by
                                // StormObject, so restore the machine-owned flag explicitly.
                                existing.naturallySpawned = false;
                                lastTickStormObject = existing;
                            }
                            // Whether we reconnected or the storm no longer exists, stop
                            // attempting this lookup so we do not create a duplicate below.
                            savedStormID = 0L;
                        }

                        if (lastTickStormObject == null) {
                            StormObject so = new StormObject(manager);
                            so.initFirstTime();
                            so.pos = Vec3.createVectorHelper(xCoord, StormObject.layers.get(0), zCoord);
                            so.layer = 0;
                            so.userSpawnedFor = "" + xCoord + yCoord + zCoord;
                            // so.canSnowFromCloudTemperature = true;
                            so.naturallySpawned = false;

                            manager.addStormObject(so);
                            manager.syncStormNew(so);
                            lastTickStormObject = so;
                        }
                    }
                }
            }

            if (lastTickStormObject != null && !lastTickStormObject.isDead) {

                Random rand = new Random();

                if (lockStormHere) {
                    // lastTickStormObject.pos = Vec3.createVectorHelper(xCoord + rand.nextFloat() - rand.nextFloat(),
                    // StormObject.layers.get(0), zCoord + rand.nextFloat() - rand.nextFloat());
                    lastTickStormObject.pos = Vec3.createVectorHelper(xCoord, StormObject.layers.get(0), zCoord);
                }

                lastTickStormObject.size = weatherSize;

                lastTickStormObject.levelWater = 1000;
                lastTickStormObject.attrib_precipitation = true;
                lastTickStormObject.hasStormPeaked = false;
                lastTickStormObject.levelCurStagesIntensity = 0.9F;

                // defaults
                lastTickStormObject.levelCurIntensityStage = StormObject.STATE_NORMAL;
                lastTickStormObject.stormType = StormObject.TYPE_LAND;
                lastTickStormObject.levelTemperature = 40;

                if (weatherType == 0) {
                    lastTickStormObject.levelTemperature = -40;
                } else if (weatherType == 1) {} else if (weatherType == 2) {
                    lastTickStormObject.stormType = StormObject.TYPE_LAND;
                    lastTickStormObject.levelCurIntensityStage = StormObject.STATE_THUNDER;
                } else if (weatherType == 3) {
                    lastTickStormObject.stormType = StormObject.TYPE_LAND;
                    lastTickStormObject.levelCurIntensityStage = StormObject.STATE_HIGHWIND;
                } else if (weatherType == 4) {
                    lastTickStormObject.stormType = StormObject.TYPE_LAND;
                    lastTickStormObject.levelCurIntensityStage = StormObject.STATE_HAIL;
                } else if (weatherType == 5) {
                    lastTickStormObject.stormType = StormObject.TYPE_LAND;
                    lastTickStormObject.levelCurIntensityStage = StormObject.STATE_STAGE1;
                } else if (weatherType == 6) {
                    lastTickStormObject.stormType = StormObject.TYPE_WATER;
                    lastTickStormObject.levelCurIntensityStage = StormObject.STATE_STAGE1;
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound var1) {
        super.writeToNBT(var1);
        var1.setInteger("weatherType", weatherType);
        // Persist the active storm's ID so updateEntity() can reconnect to it
        // after a server restart instead of creating a duplicate storm.
        if (lastTickStormObject != null && !lastTickStormObject.isDead) {
            var1.setLong("lastStormID", lastTickStormObject.ID);
        }
    }

    public void readFromNBT(NBTTagCompound var1) {
        super.readFromNBT(var1);
        weatherType = var1.getInteger("weatherType");
        // 0 if the key is absent (pre-fix saves or no storm was active).
        savedStormID = var1.getLong("lastStormID");
    }
}
