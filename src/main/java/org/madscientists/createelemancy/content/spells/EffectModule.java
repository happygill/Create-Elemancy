package org.madscientists.createelemancy.content.spells;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class EffectModule extends AbstractModule{

    public CastMethod getCastMethod() {
        return castMethod;
    }

    private final Map<ModifierModule, Integer> allowedModifiers=new HashMap<>();

    private  BiConsumer<SpellData, Entity> applyEntityEffect=(spellData, entity) -> {};
    private  BiConsumer<SpellData, BlockPos> applyBlockEffect=(spellData, blockPos) -> {};

    private final CastMethod castMethod;
    public EffectModule(String name, String description, CastMethod castMethod) {
        super(name, description, ModuleType.EFFECT);
        this.castMethod = castMethod;
    }

    public EffectModule addModifier(ModifierModule modifier, int maxValue){
        allowedModifiers.put(modifier,maxValue);
        return this;
    }

    public EffectModule addEntityEffect(BiConsumer<SpellData, Entity> applyEntityEffect){
        this.applyEntityEffect=applyEntityEffect;
        return this;
    }
    public EffectModule addBlockEffect(BiConsumer<SpellData, BlockPos> applyBlockEffect){
        this.applyBlockEffect=applyBlockEffect;
        return this;
    }

    public boolean modifierAllowed(ModifierModule modifier){
        return allowedModifiers.containsKey(modifier);
    }

    public EffectModule(String name, CastMethod castMethod) {
        super(name, name, ModuleType.EFFECT);
        this.castMethod = castMethod;
    }

    public int getMaxValue(ModifierModule modifier) {
        return allowedModifiers.getOrDefault(modifier,0);
    }

    public enum CastMethod{
        PROJECTILE,
        SELF,
        TOUCH,
        AOE

    }
}
