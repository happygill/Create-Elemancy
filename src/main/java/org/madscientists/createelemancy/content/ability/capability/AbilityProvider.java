package org.madscientists.createelemancy.content.ability.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<Abilities> ABILITIES = CapabilityManager.get(new CapabilityToken<Abilities>() {
    });
    private Abilities abilities = null;
    private final LazyOptional<Abilities> optional = LazyOptional.of(this::getOrCreateAbilities);
    private Abilities getOrCreateAbilities() {
        if (this.abilities == null) {
            this.abilities = new Abilities();
        }

        return this.abilities;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ABILITIES) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        getOrCreateAbilities().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getOrCreateAbilities().loadNBTData(nbt);
    }
}
