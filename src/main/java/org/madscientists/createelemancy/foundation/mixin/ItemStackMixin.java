package org.madscientists.createelemancy.foundation.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.madscientists.createelemancy.content.ability.AvianWingsAbility;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityUtils;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {
    @Override
    public boolean canElytraFly(LivingEntity entity) {
        if(entity instanceof Player player) {
            Ability ability = AbilityUtils.getAbilityFromPlayer(player, ElemancyAbilityTypes.AVIAN_WINGS);
            if(ability instanceof AvianWingsAbility avianWingsAbility && !avianWingsAbility.isAbilityComplete()) {
                return avianWingsAbility.getStack().getItem().canElytraFly(avianWingsAbility.getStack(), entity);
            }
        }
        return ((ItemStack)(Object)this).getItem().canElytraFly(((ItemStack)(Object)this),entity);
    }

    @Override
    public boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
        if(entity instanceof Player player) {
            Ability ability = AbilityUtils.getAbilityFromPlayer(player, ElemancyAbilityTypes.AVIAN_WINGS);
            if(ability instanceof AvianWingsAbility avianWingsAbility && !avianWingsAbility.isAbilityComplete()) {
                return avianWingsAbility.getStack().getItem().elytraFlightTick(avianWingsAbility.getStack(), entity,flightTicks);
            }
        }
        return ((ItemStack)(Object)this).getItem().elytraFlightTick(((ItemStack)(Object)this),entity,flightTicks);
    }
}
