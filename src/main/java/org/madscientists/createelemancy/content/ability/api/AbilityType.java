package org.madscientists.createelemancy.content.ability.api;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record AbilityType(ResourceLocation id, Supplier<Ability> factory) {
    public Ability create() {
        return factory.get();
    }
}
