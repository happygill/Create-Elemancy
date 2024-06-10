package org.madscientists.createelemancy.content.spells;

import net.minecraft.world.entity.player.Player;
import org.madscientists.createelemancy.content.registry.ElemancyModules;

public class SpellData {

    private final EffectModule module;



    private Player player;
    int count=0;
    int range=0;
    int size=0;

    public SpellData(EffectModule module) {
        this.module = module;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if(module.modifierAllowed(ElemancyModules.COUNT))
            this.count = Math.min(count,module.getMaxValue(ElemancyModules.COUNT));
    }

    public void  increaseCount(){
        setCount(count+1);
    }

    public Player getCaster() {
        return player;
    }

    public void setCaster(Player player) {
        this.player = player;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        if(module.modifierAllowed(ElemancyModules.RANGE))
            this.range = Math.min(range,module.getMaxValue(ElemancyModules.RANGE));
    }

    public void  increaseRange(){
        setRange(range+1);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if(module.modifierAllowed(ElemancyModules.SIZE))
            this.size = Math.min(size,module.getMaxValue(ElemancyModules.SIZE));
    }

    public void  increaseSize(){
        setSize(size+1);
    }

}
