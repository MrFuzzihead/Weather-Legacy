package com.mrfuzzihead.weather.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.mrfuzzihead.weather.Weather;
import com.mrfuzzihead.weather.util.WeatherFileUtil;

public class PlayerData {

    public static HashMap<String, NBTTagCompound> playerNBT = new HashMap<String, NBTTagCompound>();

    public static NBTTagCompound getPlayerNBT(String username) {
        if (!playerNBT.containsKey(username)) {
            tryLoadPlayerNBT(username);
        }
        return playerNBT.get(username);
    }

    public static void tryLoadPlayerNBT(String username) {
        // try read from hw/playerdata/player.dat
        // init with data, if fail, init default blank

        NBTTagCompound playerData = new NBTTagCompound();

        try {
            String fileURL = WeatherFileUtil.getWorldSavePath() + "com/mrfuzzihead"
                + File.separator
                + "PlayerData"
                + File.separator
                + username
                + ".dat";

            if ((new File(fileURL)).exists()) {
                try (FileInputStream fis = new FileInputStream(fileURL)) {
                    playerData = CompressedStreamTools.readCompressed(fis);
                }
            }
        } catch (Exception ex) {
            // Weather.dbg("no saved data found for " + username);
        }

        playerNBT.put(username, playerData);
    }

    public static void writeAllPlayerNBT(boolean resetData) {
        // Weather.dbg("writing out all player nbt");

        String fileURL = WeatherFileUtil.getWorldSavePath() + "com/mrfuzzihead" + File.separator + "PlayerData";
        if (!new File(fileURL).exists()) new File(fileURL).mkdir();

        Iterator it = playerNBT.entrySet()
            .iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            // Weather.dbg(pairs.getKey() + " = " + pairs.getValue());
            writePlayerNBT((String) pairs.getKey(), (NBTTagCompound) pairs.getValue());
        }

        if (resetData) {
            playerNBT.clear();
        }
    }

    public static void writePlayerNBT(String username, NBTTagCompound parData) {
        // Weather.dbg("writing " + username);

        String fileURL = WeatherFileUtil.getWorldSavePath() + "com/mrfuzzihead"
            + File.separator
            + "PlayerData"
            + File.separator
            + username
            + ".dat";

        try (FileOutputStream fos = new FileOutputStream(fileURL)) {
            CompressedStreamTools.writeCompressed(parData, fos);
        } catch (Exception ex) {
            ex.printStackTrace();
            Weather.dbg("Error writing Weather2 player data for " + username);
        }
    }

}
