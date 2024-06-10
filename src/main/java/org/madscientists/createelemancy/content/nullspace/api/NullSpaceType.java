package org.madscientists.createelemancy.content.nullspace.api;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record NullSpaceType(ResourceLocation id, Supplier<NullSpaceEffect> factory) {
    public NullSpaceEffect create() {
        return factory.get();
    }
}
