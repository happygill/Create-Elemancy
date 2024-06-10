package org.madscientists.createelemancy.content.procs.composites;

import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.procs.ProcUtils;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class CleansingFireComposite extends Composite{

    @Override
    public void onAdded(LivingEntity entity) {
        entity.hurt(entity.damageSources().inFire(), 10);
        ProcUtils.removeAllProcs(entity);
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.CLEANSING_FIRE_COMPOSITE;
    }
}
