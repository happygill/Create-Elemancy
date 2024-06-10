package org.madscientists.createelemancy.content.procs.procs;

import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class ReactivityProc extends Proc {
    public ReactivityProc() {
        super(ElemancyElement.REACTIVITY);
        setDurationTicks(TickHelper.secondsToTicks(25));
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.REACTIVITY_PROC;
    }
}
