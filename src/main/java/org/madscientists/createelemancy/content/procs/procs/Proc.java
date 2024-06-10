package org.madscientists.createelemancy.content.procs.procs;

import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

public abstract class Proc extends Ability {

    ElemancyElement elemancyElement;

    public Proc(ElemancyElement elemancyElement) {
        this.elemancyElement = elemancyElement;
    }

    public ElemancyElement getElement() {
        return elemancyElement;
    }

    @Override
    public void onAdded(LivingEntity entity) {
       entity.hurt(entity.damageSources().magic(), 2);
    }
}
