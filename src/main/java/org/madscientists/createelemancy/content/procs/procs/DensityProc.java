package org.madscientists.createelemancy.content.procs.procs;

import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class DensityProc extends Proc {
    public DensityProc() {
        super(ElemancyElement.DENSITY);
        setDurationTicks(TickHelper.secondsToTicks(40));
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.DENSITY_PROC;
    }
}
