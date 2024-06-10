package org.madscientists.createelemancy.content.ability.enchant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.enchant.TempEnchantmentData;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class TempEnchantAbility extends Ability {

    TempEnchantmentData data=new TempEnchantmentData();

    @Override
    public void onAdded(LivingEntity entity) {
        if(entity instanceof Player player)
            data.addTempEnchantToPlayer(player);
    }

    @Override
    public boolean canMultipleInstancesExist() {
        return true;
    }

    @Override
    public boolean canAbilityBeAddedToEntity(LivingEntity entity) {
        return entity instanceof Player;
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.TEMP_ENCHANT;
    }

    @Override
    public void save(CompoundTag tag) {
        super.save(tag);
        tag.put("data",data.serializeNBT());
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        data.deserializeNBT(tag.getCompound("data"));
    }

    public void setData(TempEnchantmentData data) {
        this.data = data;
    }

    public String getEffectName() {
        return data.getEffectName();
    }
}
