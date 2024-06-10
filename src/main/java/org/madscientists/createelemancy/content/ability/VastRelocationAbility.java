package org.madscientists.createelemancy.content.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

import java.util.List;

public class VastRelocationAbility extends Ability {
    boolean isComplete = false;
    BlockPos posA = BlockPos.ZERO;
    BlockPos posB = BlockPos.ZERO;


    public void setPos(BlockPos pos) {
        if(posA.equals(BlockPos.ZERO))
            posA=pos;
        else
            posB=pos;
    }

    @Override
    public void tick(LivingEntity player) {
        if(!posA.equals(BlockPos.ZERO)&&!posB.equals(BlockPos.ZERO)&&!player.level().isClientSide){
            Level level = player.level();
            List<Entity> posAList = level.getEntities((Entity) null, new AABB(posA).inflate(2,1,2),
                    (entity -> entity instanceof LivingEntity));
            List<Entity> posBList = level.getEntities((Entity) null, new AABB(posB).inflate(2,1,2),
                    (entity -> entity instanceof LivingEntity));
            posAList.forEach(entity -> entity.moveTo(posB, entity.getYRot(), entity.getXRot()));
            posBList.forEach(entity -> entity.moveTo(posA, entity.getYRot(), entity.getXRot()));
            isComplete=true;
        }
    }

    @Override
    public boolean shouldRemoveOnDeath() {
        return false;
    }

    @Override
    public boolean isAbilityComplete() {
        return isComplete;
    }

    @Override
    public void save(CompoundTag tag) {
        tag.putBoolean("isComplete", isComplete);
        tag.put("posA", NbtUtils.writeBlockPos(posA));
    }

    @Override
    public void read(CompoundTag tag) {
        isComplete = tag.getBoolean("isComplete");
        posA = NbtUtils.readBlockPos(tag.getCompound("posA"));
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.VAST_RELOCATION;
    }

    @Override
    public void onAdded(LivingEntity player) {
        if(posA.equals(BlockPos.ZERO))
            setPos(player.blockPosition());
    }

    @Override
    public void onRecast(LivingEntity player, Ability newAbility) {
        if(posB.equals(BlockPos.ZERO))
            setPos(player.blockPosition());
    }

    @Override
    public boolean canAbilityBeAddedToEntity(LivingEntity entity) {
        return entity instanceof Player;
    }
}
