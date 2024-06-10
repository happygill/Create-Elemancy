package org.madscientists.createelemancy.content.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class HeightenedPhysiqueAbility extends Ability {
    int foodLevel = 0;
    float saturation = 0;
    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.HEIGHTENED_PHYSIQUE;
    }

    @Override
    public void tick(LivingEntity entity) {
        super.tick(entity);
        if(entity instanceof Player player) {
            if(player.getFoodData().getFoodLevel() < foodLevel)
                player.getFoodData().setFoodLevel(foodLevel);
            if(player.getFoodData().getSaturationLevel() < saturation)
                player.getFoodData().setSaturation(saturation);
        }
    }

    @Override
    public void save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("foodLevel", foodLevel);
        tag.putFloat("saturation", saturation);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        foodLevel = tag.getInt("foodLevel");
        saturation = tag.getFloat("saturation");
    }

    @Override
    public void onAdded(LivingEntity entity) {
        super.onAdded(entity);
        if(entity instanceof Player player) {
            foodLevel = player.getFoodData().getFoodLevel();
            saturation = player.getFoodData().getSaturationLevel();
        }
    }

    @Override
    public void onRecast(LivingEntity entity, Ability newAbility) {
        super.onRecast(entity, newAbility);
        if(entity instanceof Player player) {
            foodLevel = Math.max(player.getFoodData().getFoodLevel(),foodLevel);
            saturation = Math.max(player.getFoodData().getSaturationLevel(), saturation);
        }
    }
}
