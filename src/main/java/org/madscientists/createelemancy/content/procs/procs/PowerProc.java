package org.madscientists.createelemancy.content.procs.procs;

import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class PowerProc extends Proc {
    public PowerProc() {
        super(ElemancyElement.POWER);
        setDurationTicks(TickHelper.minutesToTicks(2.5));
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.POWER_PROC;
    }

}
