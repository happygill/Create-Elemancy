package org.madscientists.createelemancy.content.procs.procs;

import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class FeatherweightProc extends Proc {
    public FeatherweightProc() {
        super(ElemancyElement.FEATHERWEIGHT);
        setDurationTicks(TickHelper.secondsToTicks(40));
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.FEATHERWEIGHT_PROC;
    }
}
