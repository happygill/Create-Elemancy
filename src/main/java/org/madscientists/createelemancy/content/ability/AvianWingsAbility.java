package org.madscientists.createelemancy.content.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class AvianWingsAbility extends Ability {

    ItemStack stack=new ItemStack(Items.ELYTRA);

    @Override
    public boolean isAbilityComplete() {
        return stack.getDamageValue() >= stack.getMaxDamage() || super.isAbilityComplete();
    }

    @Override
    public void onRecast(LivingEntity player, Ability newAbility) {
        super.onRecast(player, newAbility);
        stack = ((AvianWingsAbility) newAbility).stack;
    }

    @Override
    public void save(CompoundTag tag) {
        super.save(tag);
        tag.put("stack",stack.save(new CompoundTag()));
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        stack=ItemStack.of(tag.getCompound("stack"));
    }

    @Override
    public boolean canAbilityBeAddedToEntity(LivingEntity entity) {
        return entity instanceof Player;
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.AVIAN_WINGS;
    }

}
