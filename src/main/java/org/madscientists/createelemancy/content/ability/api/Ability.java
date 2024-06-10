package org.madscientists.createelemancy.content.ability.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class Ability {

    int count=1;
    int durationTicks=-1;

    public void setCountAndDuration(int count, int durationTicks) {
        this.count = count;
        this.durationTicks = durationTicks;
    }
    public void setCount(int count) {
        this.count = count;
        this.durationTicks =-1;
    }

    public void setDurationTicks(int durationTicks) {
        this.durationTicks = durationTicks;
        this.count =-1;
    }

    public int getCount() {
        return count;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    /**
     * Called every player tick
     *
     * @param entity The entity with this ability.
     */
    public void tick(LivingEntity entity){
        if(durationTicks>0)
            durationTicks--;
    }

    public void markUsed(){
        if(count>0)
            count--;
    }
    boolean sync = false;
    public void syncWithClient() {
        sync = true;
    }
    public boolean needsSyncWithClient() {
        return sync;
    }

    /**
     * Determines if this ability should be removed when the entity dies.
     *
     * @return True if the ability should be removed on death, false otherwise.
     */
    public boolean shouldRemoveOnDeath(){
        return true;
    }


    /**
     * Checks if this ability is complete and can be removed from entity.
     *
     * @return True if the ability is complete, false otherwise.
     */
    public boolean isAbilityComplete(){
        return count == 0 || durationTicks == 0;
    }

    /**
     * Called when this ability is removed from either expiration, use or death.
     *
     * @param entity The entity from whom the ability is being removed.
     */
    public void onAbilityRemove(LivingEntity entity) {
    }

    /**
     * Called when this ability duration has expired.
     *
     * @param entity The entity from whom the ability is being removed.
     */
    public void onAbilityExpire(LivingEntity entity) {
    }

    /**
     * Handles key input on the server side for this ability.
     *
     * @param inputKey The key that was pressed.
     * @param action The action performed with the key.
     * @param player The player who pressed the key.
     * @see com.mojang.blaze3d.platform.InputConstants
     */
    public void onKeyInput(int inputKey, int action, Player player, boolean clientSide) {
    }

    /**
     * Called when the player with this ability dies.
     *
     * @param entity The entity that died.
     */
    public void onPlayerDeath(LivingEntity entity){
        if(shouldRemoveOnDeath())
            onAbilityRemove(entity);
    }

    public void save(CompoundTag tag) {
        tag.putInt("count",count);
        tag.putInt("durationTicks",durationTicks);
    }

    public void read(CompoundTag tag) {
        tag.getInt("count");
        tag.getInt("durationTicks");
    }

    public abstract AbilityType getType();

    public void onRecast(LivingEntity entity, Ability newAbility) {
        count = newAbility.count;
        durationTicks = newAbility.durationTicks;
    }

    public void onAdded(LivingEntity entity) {
    }

    /**
     * Determines if the ability can be added to the given entity.
     *
     * @param entity The LivingEntity to which the ability is to be added.
     * @return true if the ability can be added to the entity, false otherwise.
     */
    public boolean canAbilityBeAddedToEntity(LivingEntity entity) {
        return true;
    }

    /**
     * Checks if multiple instances of this ability can exist simultaneously.
     *
     * @return false if multiple instances of this ability cannot exist, true otherwise.
     */
    public boolean canMultipleInstancesExist(){
        return false;
    }
    public void onAttack(LivingHurtEvent event) {
    }
}