package org.madscientists.createelemancy.content.registry;

import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import org.madscientists.createelemancy.Elemancy;

import static org.madscientists.createelemancy.Elemancy.registrate;

public class ElemancyDamageTypes {

    public static final ResourceKey<DamageType>
            GRIND = key("grind",
            "%1$s was processed by Grinding Wheels","%1$s was thrown into Grinding Wheels by %2$s"),

            FORCES = key("unknown_forces",
            "%1$s messed with forces beyond their understanding"),

            NULLIFIED = key("nullified",
            "%1$s was Nullified");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Elemancy.asResource(name));
    }

    private static ResourceKey<DamageType> key(String name,String deathMessage) {
        registrate().addRawLang("death.attack.createelemancy."+name,deathMessage);
        return ResourceKey.create(Registries.DAMAGE_TYPE, Elemancy.asResource(name));
    }

    private static ResourceKey<DamageType> key(String name,String deathMessage, String playerDeathMessage) {
        registrate().addRawLang("death.attack.createelemancy."+name,deathMessage);
        registrate().addRawLang("death.attack.createelemancy."+name+".player",playerDeathMessage);
        return ResourceKey.create(Registries.DAMAGE_TYPE, Elemancy.asResource(name));
    }

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        new DamageTypeBuilder(GRIND).scaling(DamageScaling.ALWAYS).register(ctx);
        new DamageTypeBuilder(FORCES).register(ctx);
        new DamageTypeBuilder(NULLIFIED).register(ctx);
    }

    public static void register() {
    }
}
