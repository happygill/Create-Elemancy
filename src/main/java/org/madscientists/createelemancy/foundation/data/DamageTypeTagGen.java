package org.madscientists.createelemancy.foundation.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancyDamageTypes;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagGen extends TagsProvider<DamageType> {
    public DamageTypeTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, Elemancy.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(ElemancyDamageTypes.GRIND);
    }

    @Override
    public String getName() {
        return "Create Elemancy's Damage Type Tags";
    }
}
