package org.madscientists.createelemancy.content.spells;

import java.util.function.BiConsumer;

public class ModifierModule extends AbstractModule{

    private final BiConsumer<SpellData,Integer> modifySpell;

    public ModifierModule(String name, String description, BiConsumer<SpellData, Integer> modifySpell) {
        super(name, description, ModuleType.MODIFIER);
        this.modifySpell = modifySpell;
    }
    public ModifierModule(String name, BiConsumer<SpellData, Integer> modifySpell) {
        this(name, name, modifySpell);
    }


    public boolean isApplicable(EffectModule effectModule){
        return effectModule.modifierAllowed(this);
    }

    public void applyModifier(SpellData data, int value){
        modifySpell.accept(data,value);
    }
}
