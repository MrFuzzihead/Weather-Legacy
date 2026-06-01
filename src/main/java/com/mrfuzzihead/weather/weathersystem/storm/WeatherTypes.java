package com.mrfuzzihead.weather.weathersystem.storm;

import java.util.ArrayList;
import java.util.List;

public class WeatherTypes {

    public static List<WeatherEntityConfig> weatherEntTypes;

    static {
        initWeatherTypes();
    }

    public static void initWeatherTypes() {
        weatherEntTypes = new ArrayList();
        WeatherEntityConfig sConf = new WeatherEntityConfig();

        // Array index → Stage constant → Fujita category
        // 0 → FORMING → waterspout / forming tornado
        // 1 → STAGE1 → F0 (64–116 km/h, mid ≈ 90 km/h) ratio 1.00
        // 2 → STAGE2 → F1 (117–180 km/h, mid ≈ 148 km/h) ratio 1.64
        // 3 → STAGE3 → F2 (181–253 km/h, mid ≈ 217 km/h) ratio 2.41
        // 4 → STAGE4 → F3 (254–332 km/h, mid ≈ 293 km/h) ratio 3.26
        // 5 → STAGE5 → F4 (333–418 km/h, mid ≈ 375 km/h) ratio 4.17
        // 6 → STAGE6 → F5 (419–512 km/h, mid ≈ 465 km/h) ratio 5.17
        // 7 → STAGE7 → F6 (513–603 km/h, mid ≈ 558 km/h) ratio 6.20 (theoretical)
        //
        // Pull and lift rates scale linearly with the Fujita wind-speed midpoint
        // relative to F0 (base pull = 0.030F, base lift = 0.040F at F0).

        // waterspout / forming tornado (index 0)
        sConf.tornadoInitialSpeed = 0.2F;
        sConf.tornadoPullRate = 0.04F;
        sConf.tornadoLiftRate = 0.05F;
        sConf.relTornadoSize = 0;
        sConf.tornadoBaseSize = 3;
        sConf.tornadoWidthScale = 1.0F;
        sConf.grabDist = 40D;
        sConf.tornadoTime = 4500;
        sConf.type = 0;
        sConf.grabsBlocks = false;
        weatherEntTypes.add(sConf); // index 0

        // F0 tornado — ratio 1.00 (index 1)
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.2F;
        sConf.tornadoPullRate = 0.030F;
        sConf.tornadoLiftRate = 0.040F;
        sConf.relTornadoSize = -10;
        sConf.tornadoWidthScale = 1.2F;
        weatherEntTypes.add(sConf); // index 1

        // F1 tornado — ratio 1.64 (index 2)
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.2F;
        sConf.tornadoPullRate = 0.049F;
        sConf.tornadoLiftRate = 0.066F;
        sConf.relTornadoSize = -20;
        sConf.tornadoWidthScale = 1.5F;
        weatherEntTypes.add(sConf); // index 2

        // F2 tornado — ratio 2.41 (index 3)
        sConf = new WeatherEntityConfig();
        sConf.tornadoInitialSpeed = 0.2F;
        sConf.tornadoPullRate = 0.072F;
        sConf.tornadoLiftRate = 0.096F;
        sConf.relTornadoSize = -30;
        sConf.tornadoWidthScale = 1.5F;
        weatherEntTypes.add(sConf); // index 3

        // F3 tornado — ratio 3.26 (index 4)
        sConf = new WeatherEntityConfig();
        sConf.tornadoPullRate = 0.098F;
        sConf.tornadoLiftRate = 0.130F;
        sConf.relTornadoSize = -40;
        sConf.tornadoWidthScale = 1.9F;
        weatherEntTypes.add(sConf); // index 4

        // F4 tornado — ratio 4.17 (index 5)
        sConf = new WeatherEntityConfig();
        sConf.tornadoPullRate = 0.125F;
        sConf.tornadoLiftRate = 0.167F;
        sConf.relTornadoSize = -50;
        sConf.tornadoWidthScale = 1.9F;
        weatherEntTypes.add(sConf); // index 5

        // F5 tornado — ratio 5.17 (index 6)
        sConf = new WeatherEntityConfig();
        sConf.tornadoPullRate = 0.155F;
        sConf.tornadoLiftRate = 0.207F;
        sConf.relTornadoSize = -65;
        sConf.tornadoWidthScale = 2.5F;
        weatherEntTypes.add(sConf); // index 6

        // F6 tornado — ratio 6.20, theoretical (index 7)
        sConf = new WeatherEntityConfig();
        sConf.tornadoPullRate = 0.186F;
        sConf.tornadoLiftRate = 0.248F;
        sConf.relTornadoSize = -80;
        sConf.tornadoWidthScale = 3.0F;
        weatherEntTypes.add(sConf); // index 7
    }
}
