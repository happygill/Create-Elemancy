package org.madscientists.createelemancy.foundation.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.foundation.config.client.EClientConfig;
import org.madscientists.createelemancy.foundation.config.common.ECommonConfig;
import org.madscientists.createelemancy.foundation.config.server.EServerConfig;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class ElemancyConfig {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static EClientConfig client;
    private static ECommonConfig common;
    private static EServerConfig server;

    public static EClientConfig client() {
        return client;
    }
    public static ECommonConfig common() {
        return common;
    }
    public static EServerConfig server() {
        return server;
    }

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register(ModLoadingContext context) {
        client = register(EClientConfig::new, ModConfig.Type.CLIENT);
        common = register(ECommonConfig::new, ModConfig.Type.COMMON);
        server = register(EServerConfig::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            context.registerConfig(pair.getKey(), pair.getValue().specification);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig()
                    .getSpec())
                config.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig()
                    .getSpec())
                config.onReload();
    }

    public static BaseConfigScreen createConfigScreen(Minecraft mc, Screen parent) {
        BaseConfigScreen.setDefaultActionFor(Elemancy.ID, (base) -> base
                .withSpecs(ElemancyConfig.client().specification,
                        ElemancyConfig.common().specification,
                        ElemancyConfig.server().specification));

        return new BaseConfigScreen(parent, Elemancy.ID);
    }
}
