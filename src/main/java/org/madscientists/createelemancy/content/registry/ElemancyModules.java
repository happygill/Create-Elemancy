package org.madscientists.createelemancy.content.registry;

import org.madscientists.createelemancy.content.spells.EffectModule;
import org.madscientists.createelemancy.content.spells.ModifierModule;
import org.madscientists.createelemancy.content.spells.SpellData;

public class ElemancyModules {
    public static final ModifierModule COUNT=new ModifierModule("Split",SpellData::setCount);
    public static final ModifierModule RANGE=new ModifierModule("Range", SpellData::setRange);
    public static final ModifierModule SIZE=new ModifierModule("Size", SpellData::setSize);


    public static final EffectModule FIRE_BALL=new EffectModule("Fireball","Fireball Projectile", EffectModule.CastMethod.PROJECTILE)
            .addEntityEffect((spellData, entity) -> entity.setSecondsOnFire(5))
            .addModifier(COUNT,3)
            .addModifier(RANGE,64)
            .addModifier(SIZE,3);

}
