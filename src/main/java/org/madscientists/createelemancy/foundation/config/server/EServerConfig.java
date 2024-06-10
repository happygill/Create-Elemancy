package org.madscientists.createelemancy.foundation.config.server;

import com.simibubi.create.foundation.config.ConfigBase;

public class EServerConfig extends ConfigBase {
    @Override
    public String getName() {
        return "Elemancy Server";
    }

    public ConfigInt explosionPowerModifier = i(1, 1, "explosionPowerModifier", "Multiplier for the power of explosions when opposite fluids touch");
}