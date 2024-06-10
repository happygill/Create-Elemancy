package org.madscientists.createelemancy.content.procs.procs;

import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class FrigidProc extends Proc {
    public FrigidProc() {
        super(ElemancyElement.FRIGID);
        setDurationTicks(TickHelper.secondsToTicks(35));
    }

    int ticks = 0;
    @Override
    public void tick(LivingEntity entity) {
        super.tick(entity);
        ticks++;
        if(ticks>20) {
            entity.hurt(entity.damageSources().freeze(), 1);
            ticks = 0;
        }
    }
    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.FRIGID_PROC;
    }
}
