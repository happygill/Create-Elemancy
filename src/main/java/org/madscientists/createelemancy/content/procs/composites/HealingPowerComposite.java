package org.madscientists.createelemancy.content.procs.composites;

import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.procs.ProcUtils;

public class HealingPowerComposite extends Composite{
    @Override
    public void onAdded(LivingEntity entity) {
        ProcUtils.removeAllProcs(entity);
    }

    @Override
    public AbilityType getType() {
        return null;
    }
}
