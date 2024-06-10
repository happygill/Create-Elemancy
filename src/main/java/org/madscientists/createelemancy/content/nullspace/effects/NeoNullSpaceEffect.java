package org.madscientists.createelemancy.content.nullspace.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.content.nullspace.NullSpaceBlockEntity;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceType;
import org.madscientists.createelemancy.content.registry.ElemancyNullSpaceTypes;

import java.util.UUID;

import static org.madscientists.createelemancy.content.registry.ElemancyNullSpaceTypes.NEO;

public class NeoNullSpaceEffect extends NullSpaceEffect {
    private static final int MAX_EFFECT_SIZE = 5;

    private int currentEffectSize = 0;
    private LivingEntity effectCaster;
    private String casterId = "";
    private BlockPos casterPosition = BlockPos.ZERO;

    @Override
    public void tick(NullSpaceBlockEntity nullSpace, int tickCounter) {
        if (effectCaster != null) {
            updateEffectSize();
        }
    }

    private void updateEffectSize() {
        int distance = (int) effectCaster.position().distanceTo(Vec3.atCenterOf(casterPosition));
        currentEffectSize = Math.min(currentEffectSize, Math.max(0, MAX_EFFECT_SIZE - distance));
    }

    @Override
    public boolean isEffectValid(NullSpaceBlockEntity nullSpace, int tickCounter) {
        return effectCaster != null;
    }

    @Override
    public void onEffectCast(NullSpaceBlockEntity nullSpace, LivingEntity caster) {
        if (caster == null) {
            return;
        }
        this.effectCaster = caster;
        this.casterId = caster.getStringUUID();
        this.casterPosition = caster.blockPosition();
        this.currentEffectSize = MAX_EFFECT_SIZE;
    }

    @Override
    public boolean shouldShrink(NullSpaceBlockEntity nullSpace, int tickCounter) {
        return nullSpace.getSize() > currentEffectSize;
    }

    @Override
    public void save(NullSpaceBlockEntity nullSpace, CompoundTag tag) {
        super.save(nullSpace, tag);
        tag.putInt("currentEffectSize", currentEffectSize);
        tag.putString("casterId", casterId);
        tag.put("casterPosition", NbtUtils.writeBlockPos(casterPosition));
    }

    @Override
    public void read(NullSpaceBlockEntity nullSpace, CompoundTag tag) {
        super.read(nullSpace, tag);
        currentEffectSize = tag.getInt("currentEffectSize");
        casterId = tag.getString("casterId");
        casterPosition = NbtUtils.readBlockPos(tag.getCompound("casterPosition"));
        effectCaster = nullSpace.getLevel().getPlayerByUUID(UUID.fromString(casterId));
    }

    @Override
    protected NullSpaceType getType() {
        return ElemancyNullSpaceTypes.NEO;
    }
}