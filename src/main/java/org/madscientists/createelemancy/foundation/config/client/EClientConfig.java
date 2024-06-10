package org.madscientists.createelemancy.foundation.config.client;

import com.simibubi.create.foundation.config.ConfigBase;

public class EClientConfig extends ConfigBase {
    @Override
    public String getName() {
        return "Elemancy Client";
    }

    public ConfigInt clientTest = i(0, 0, 100, "clientTest", "This is a test config value for the events");
}
