package org.madscientists.createelemancy.foundation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancyDamageTypes;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GeneratedEntriesProvider extends DatapackBuiltinEntriesProvider {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ElemancyDamageTypes::bootstrap);

    public GeneratedEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Elemancy.ID));
    }

    @Override
    public String getName() {
        return "Create Elemancy's Generated Registry Entries";
    }
}

