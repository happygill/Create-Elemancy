package org.madscientists.createelemancy.content.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.procs.ProcUtils;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyAttributes;
import org.madscientists.createelemancy.foundation.util.TickHelper;

public class EnergizedAbility extends Ability {

    public EnergizedAbility() {
        super();
        setCount(-1);
        setDurationTicks(TickHelper.minutesToTicks(1));
    }

    private Vec3 lastPos=Vec3.ZERO;
    static final int MIN_HUNGER_LEVEL =6;
    float charge = 0;

    static final AttributeModifier SPEED2= new AttributeModifier("energized_speed_2",0.1,AttributeModifier.Operation.ADDITION);
    static final AttributeModifier SPEED3= new AttributeModifier("energized_speed_3",0.1,AttributeModifier.Operation.ADDITION);
    static final AttributeModifier EXTRA_JUMP= new AttributeModifier("energized_jump",1,AttributeModifier.Operation.ADDITION);

    @Override
    public void save(CompoundTag tag) {
        super.save(tag);
        tag.putFloat("charge", charge);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        charge = tag.getFloat("charge");
    }

    @Override
    public void tick(LivingEntity entity) {
        super.tick(entity);
        if(entity instanceof Player player&&!player.level().isClientSide()) {
            applyCharge(player);
            applyHungerBars(player);
            applySpeed(player);
            applyJump(player);
        }
    }


    private void applyJump(Player player) {
        AttributeInstance jump = player.getAttribute(ElemancyAttributes.EXTRA_JUMPS.get());
        if(jump==null)
            return;
        if (charge>= 5 && !jump.hasModifier(EXTRA_JUMP)) {
            jump.addTransientModifier(EXTRA_JUMP);
        }
        if (charge < 5 && jump.hasModifier(EXTRA_JUMP))
            jump.removeModifier(EXTRA_JUMP);
    }

    private void applyCharge(Player player) {
        if (player.isSprinting() && charge < 10)
            charge+=0.05f;
        if (player.position().equals(lastPos) && charge > 0) {
            charge -= 0.05f;
            player.hurt(player.level().damageSources().generic(),.05f);
        }
        if (getDurationTicks() < 5)
            charge = 0;
        lastPos = player.position();
    }
    private void applyHungerBars(Player player) {
        if(player.getFoodData().getFoodLevel()< MIN_HUNGER_LEVEL)
            player.getFoodData().setFoodLevel(MIN_HUNGER_LEVEL);
    }

    private void applySpeed(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if(speed==null)
            return;

        if (charge >= 1 && !speed.hasModifier(SPEED2))
            speed.addTransientModifier(SPEED2);

        if (charge>= 9 && !speed.hasModifier(SPEED3))
            speed.addTransientModifier(SPEED3);;

        if (charge < 8 && speed.hasModifier(SPEED3))
            speed.removeModifier(SPEED3);

        if (charge < 1 && speed.hasModifier(SPEED2))
            speed.removeModifier(SPEED2);
    }

    @Override
    public void onAttack(LivingHurtEvent event) {
        if(event.getSource().getEntity() instanceof Player) {
            if (charge > 0) {
                event.setAmount(event.getAmount() + charge);
                ProcUtils.addProc(event.getEntity(),ElemancyAbilityTypes.REACTIVITY_PROC);
                charge = 0;
            }
        }
    }

    @Override
    public boolean canAbilityBeAddedToEntity(LivingEntity entity) {
        return entity instanceof Player;
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.ENERGIZED;
    }
}
