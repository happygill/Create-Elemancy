package org.madscientists.createelemancy.content.procs.composites;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.procs.ProcUtils;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class CombustionComposite extends Composite {

    @Override
    public void onAdded(LivingEntity entity) {
        if(entity==null) return;
        entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 2.0f, Level.ExplosionInteraction.NONE);
        ProcUtils.removeAllProcs(entity);
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.COMBUSTION_COMPOSITE;
    }
}
