package org.madscientists.createelemancy.foundation.config.common;

import com.simibubi.create.foundation.config.ConfigBase;

public class ECommonConfig extends ConfigBase {
    @Override
    public String getName() {
        return "Elemancy Common";
    }

    public ConfigInt commonTest = i(1, 0, 100, "commonTest", "This is a test value for the common config.");
}
