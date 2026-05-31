package com.mrfuzzihead.weather.util;

import java.io.File;

import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Utility methods for resolving save-file paths without depending on CoroUtil.
 */
public final class WeatherFileUtil {

    private WeatherFileUtil() {}

    /**
     * Returns the absolute path of the current world's root save directory,
     * with a trailing {@link File#separator}.
     * Equivalent to {@code CoroUtilFile.getWorldSaveFolderPath() + CoroUtilFile.getWorldFolderName()}.
     */
    public static String getWorldSavePath() {
        File root = DimensionManager.getCurrentSaveRootDirectory();
        if (root != null) {
            return root.getAbsolutePath() + File.separator;
        }
        // Fallback: use the server's folder name relative to the working directory
        String folderName = FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getFolderName();
        return new File(folderName).getAbsolutePath() + File.separator;
    }

    /**
     * Returns the Minecraft installation / working directory path (no trailing separator).
     * Equivalent to {@code CoroUtilFile.getMinecraftSaveFolderPath()}.
     */
    public static String getMinecraftDataPath() {
        return new File(".").getAbsolutePath();
    }
}
