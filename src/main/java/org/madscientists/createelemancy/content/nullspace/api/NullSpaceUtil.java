package org.madscientists.createelemancy.content.nullspace.api;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class NullSpaceUtil {
    private static final Map<ResourceLocation,Supplier<NullSpaceEffect>> NULL_SPACE_TYPES = new HashMap<>();

    public static Optional<NullSpaceEffect> getNullSpaceEffect(ResourceLocation id) {
        return Optional.ofNullable(NULL_SPACE_TYPES.get(id)).map(Supplier::get);
    }

    public static void registerNullSpaceType(NullSpaceType type) {
        if(NULL_SPACE_TYPES.containsKey(type.id()))
            throw new IllegalArgumentException("Duplicate null space type: " + type.id());

        NULL_SPACE_TYPES.put(type.id(), type.factory());
    }

    public static List<ResourceLocation> getNullSpaceTypeIds() {
        return List.copyOf(NULL_SPACE_TYPES.keySet());
    }
}
