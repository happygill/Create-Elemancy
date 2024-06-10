package org.madscientists.createelemancy.content.procs.composites;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class ScorchedEarthComposite extends Composite{
    BlockPos lastPos;
    @Override
    public void tick(LivingEntity entity) {
        super.tick(entity);
        if(lastPos==null)
            lastPos = entity.blockPosition();
        if(!entity.isCrouching()&&!entity.blockPosition().equals(lastPos)) {
            entity.hurt(entity.damageSources().inFire(), 1);
            entity.level().addParticle(ParticleTypes.FLAME, entity.getX(), entity.getY()+.01, entity.getZ(), 0, 0, 0);
            lastPos = entity.blockPosition();
        }
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.SCORCHED_EARTH_COMPOSITE;
    }
}
