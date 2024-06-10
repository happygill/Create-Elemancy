package org.madscientists.createelemancy.content.procs.procs;

import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class StabilityProc extends Proc {
    public StabilityProc() {
        super(ElemancyElement.STABILITY);
        setDurationTicks(TickHelper.minutesToTicks(1));

    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.STABILITY_PROC;
    }
}
