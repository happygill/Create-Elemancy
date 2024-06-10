package org.madscientists.createelemancy.content.spells;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Spell {
    List<SpellData> spellSets=new ArrayList<>();

    public void formSpellData(EffectModule effect, Map<ModifierModule,Integer> modifiers){
        SpellData data=new SpellData(effect);
        modifiers.forEach((modifierModule, integer) -> modifierModule.applyModifier(data,integer));
        spellSets.add(data);
    }

    public void castProjectile(Player player){

    }

    public void addSpellData(SpellData spellData){
        spellSets.add(spellData);
    }

}
