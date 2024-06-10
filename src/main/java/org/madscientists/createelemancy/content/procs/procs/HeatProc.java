package org.madscientists.createelemancy.content.procs.procs;

import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class HeatProc extends Proc {
    public HeatProc() {
        super(ElemancyElement.HEAT);
        setDurationTicks(TickHelper.secondsToTicks(45));
    }

    int ticks = 0;
    @Override
    public void tick(LivingEntity entity) {
        super.tick(entity);
        entity.setSecondsOnFire(3);
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.HEAT_PROC;
    }
}
