package org.madscientists.createelemancy.content.procs.procs;

import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class NullProc extends Proc {
    public NullProc() {
        super(ElemancyElement.NULL);
        setDurationTicks(TickHelper.secondsToTicks(20));
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.NULL_PROC;
    }

    @Override
    public void onAbilityExpire(LivingEntity entity) {
        entity.hurt(entity.damageSources().magic(), 5);
    }

    @Override
    public void onAdded(LivingEntity entity) {
    }
}
