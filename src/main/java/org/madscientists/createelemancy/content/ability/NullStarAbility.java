package org.madscientists.createelemancy.content.ability;

import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class NullStarAbility extends Ability {
    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.NULL_STAR;
    }
}
