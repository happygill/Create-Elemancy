package org.madscientists.createelemancy.content.procs.composites;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyAttributes;
import org.madscientists.createelemancy.content.registry.ElemancyComposites;

public class UntrueNullComposite extends Composite {

    @Override
    public void onAbilityExpire(LivingEntity entity) {
        entity.hurt(entity.damageSources().magic(),5);
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.UNTRUE_NULL_COMPOSITE;
    }
}
