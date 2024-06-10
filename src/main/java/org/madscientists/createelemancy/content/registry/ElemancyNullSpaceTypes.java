package org.madscientists.createelemancy.content.registry;

import net.minecraft.resources.ResourceLocation;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceType;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceUtil;
import org.madscientists.createelemancy.content.nullspace.effects.InversionNullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.effects.NeoNullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.effects.SimpleNullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.effects.SingularityNullSpaceEffect;

import java.util.function.Supplier;

public class ElemancyNullSpaceTypes {
    public static void register() {}
    public static NullSpaceType INVERSION = register(Elemancy.asResource("inversion_null_space"), InversionNullSpaceEffect::new);
    public static NullSpaceType NEO = register(Elemancy.asResource("neo_null_space"), NeoNullSpaceEffect::new);
    public static NullSpaceType SINGULARITY = register(Elemancy.asResource("singularity_null_space"), SingularityNullSpaceEffect::new);
    public static NullSpaceType SIMPLE = register(Elemancy.asResource("simple_null_space"), SimpleNullSpaceEffect::new);

    public static NullSpaceType register(ResourceLocation id, Supplier<NullSpaceEffect> factory) {
        NullSpaceType type = new NullSpaceType(id, factory);
        NullSpaceUtil.registerNullSpaceType(type);
        return type;
    }
}
