package org.madscientists.createelemancy.content.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyAttributes;

public class DoubleJumpAbility extends Ability {

    @Override
    public void onAdded(LivingEntity player) {
        player.getAttribute(ElemancyAttributes.EXTRA_JUMPS.get()).addTransientModifier(new AttributeModifier("basic_double_jump", 1, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void onAbilityRemove(LivingEntity player) {
        player.getAttribute(ElemancyAttributes.EXTRA_JUMPS.get()).removeModifier(new AttributeModifier("basic_double_jump", 1, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public boolean canAbilityBeAddedToEntity(LivingEntity entity) {
        return entity instanceof Player;
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.DOUBLE_JUMP;
    }

}
