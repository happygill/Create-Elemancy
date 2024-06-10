package org.madscientists.createelemancy.content.nullspace.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.nullspace.NullSpaceBlockEntity;

public abstract class NullSpaceEffect {


    private int duration = -1;

    public void setDurationTicks(int duration) {
        this.duration = duration;
    }

    /**
     * This method is called every tick by a NullSpaceBlockEntity.
     *
     * @param nullSpace   The NullSpaceBlockEntity that this effect is applied to.
     * @param tickCounter The current tick count.
     */
    public void tick(NullSpaceBlockEntity nullSpace, int tickCounter) {
        if(duration > 0)
            duration--;
    }

    /**
     * Determines whether the Null Space should shrink.
     *
     * @param nullSpace The NullSpaceBlockEntity that this effect is applied to.
     * @param tickCounter The current tick count.
     * @return true by default, return false if the null space should not shrink.
     */
    public boolean shouldShrink(NullSpaceBlockEntity nullSpace, int tickCounter){
        return true;
    }

    /**
     * Checks if the effect is still valid for the Null Space.
     *
     * @param nullSpace The NullSpaceBlockEntity that this effect is applied to.
     * @param tickCounter The current tick count.
     * @return true by default, return false if the effect should be removed.
     */
    public boolean isEffectValid(NullSpaceBlockEntity nullSpace, int tickCounter){
        return duration != 0;
    }

    /**
     * Called when the effect is added to a Null Space by a LivingEntity.
     *
     * @param nullSpace The NullSpaceBlockEntity that this effect is applied to.
     * @param caster The LivingEntity that cast the effect.
     */
    public void onEffectCast(NullSpaceBlockEntity nullSpace, LivingEntity caster){};

    /**
     * This method is called when the effect is invalidated by isEffectValid and removed from a Null Space.
     *
     * @param nullSpace The NullSpaceBlockEntity from which this effect is being removed.
     * @param tickCounter The current tick count at the time of removal.
     */
    public void onEffectInvalidation(NullSpaceBlockEntity nullSpace, int tickCounter){};


    /**
     * This method is called when the Null Space is destroyed or collapses.
     *
     * @param nullSpace The NullSpaceBlockEntity that this effect is applied to.
     * @param tickCounter The current tick count at the time of collapse.
     */
    public void onNullSpaceDestruction(NullSpaceBlockEntity nullSpace, int tickCounter){};


    /**
     * Saves the state of the effect to a CompoundTag.
     *
     * @param nullSpace The NullSpaceBlockEntity that this effect is applied to.
     * @param tag The CompoundTag to save the state to.
     */
    public void save(NullSpaceBlockEntity nullSpace, CompoundTag tag){
        tag.putInt("duration", duration);
    };

    /**
     * Reads the state of the effect from a CompoundTag.
     *
     * @param nullSpace The NullSpaceBlockEntity that this effect is applied to.
     * @param tag The CompoundTag to read the state from.
     */
    public void read(NullSpaceBlockEntity nullSpace, CompoundTag tag){
        duration = tag.getInt("duration");
    };

    protected abstract NullSpaceType getType();

}
