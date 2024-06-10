package org.madscientists.createelemancy.content.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

import java.util.UUID;

public class LightWeightAuraAbility extends Ability {
    int airDuration = 0;
    boolean complete = false;
    private static final UUID SLOW_FALLING_ID = UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E712E6ABA");
    AttributeModifier SLOW_FALLING = new AttributeModifier(SLOW_FALLING_ID, "Slow falling acceleration reduction", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public void tick(LivingEntity player) {
        if(!player.onGround()&&airDuration<200){
            player.setDeltaMovement(player.getDeltaMovement().multiply(1,0,1));
            airDuration++;
        }
        AttributeInstance gravity = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        boolean flag = player.getDeltaMovement().y < 0.0;
        if (flag) {
            if (!gravity.hasModifier(SLOW_FALLING)) {
                gravity.addTransientModifier(SLOW_FALLING);
            }
        } else if (gravity.hasModifier(SLOW_FALLING)) {
            gravity.removeModifier(SLOW_FALLING);
        }
        if(player.onGround()&&airDuration>0){
            airDuration=0;
            complete = true;
        }
    }




    @Override
    public void onAbilityRemove(LivingEntity player) {
        AttributeInstance gravity = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravity.hasModifier(SLOW_FALLING)) {
            gravity.removeModifier(SLOW_FALLING);
        }
    }

    @Override
    public void save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("airDuration",airDuration);
        tag.putBoolean("complete",complete);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        airDuration = tag.getInt("airDuration");
        complete = tag.getBoolean("complete");
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.LIGHTWEIGHT_AURA;
    }

    @Override
    public boolean isAbilityComplete() {
        return complete;
    }


}
