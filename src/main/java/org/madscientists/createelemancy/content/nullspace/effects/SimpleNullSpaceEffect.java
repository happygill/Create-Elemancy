package org.madscientists.createelemancy.content.nullspace.effects;

import org.madscientists.createelemancy.content.nullspace.NullSpaceBlockEntity;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceType;
import org.madscientists.createelemancy.content.registry.ElemancyNullSpaceTypes;

public class SimpleNullSpaceEffect extends NullSpaceEffect {
    @Override
    public void tick(NullSpaceBlockEntity nullSpace, int tickCounter) {}

    @Override
    protected NullSpaceType getType() {
        return ElemancyNullSpaceTypes.SIMPLE;
    }


}
