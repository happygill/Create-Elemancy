package org.madscientists.createelemancy.content.nullspace.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.content.nullspace.NullSpaceBlockEntity;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceType;
import org.madscientists.createelemancy.content.registry.ElemancyNullSpaceTypes;

public class SingularityNullSpaceEffect extends NullSpaceEffect {

    @Override
    public void tick(NullSpaceBlockEntity nullSpace, int tickCounter) {
        if(!nullSpace.isInverted()) {
            preventEntityEscape(nullSpace);
            attractEntitiesToCenter(nullSpace);
        }
        else
            repelEntitiesFromCenter(nullSpace);
    }

    @Override
    protected NullSpaceType getType() {
        return ElemancyNullSpaceTypes.SINGULARITY;
    }


    private void attractEntitiesToCenter(NullSpaceBlockEntity nullSpace) {
        nullSpace.getEntitiesOutside().stream()
                .filter(this::isAffected)
                .forEach(entity -> pullEntityTowardsCenter(entity, nullSpace));
    }

    private void repelEntitiesFromCenter(NullSpaceBlockEntity nullSpace) {
        nullSpace.getEntitiesInside().stream()
                .filter(this::isAffected)
                .forEach(entity -> pushEntitiesAwayFromCenter(entity, nullSpace));
        nullSpace.getEntitiesOutside().stream()
                .filter(this::isAffected)
                .forEach(entity -> pushEntitiesAwayFromCenter(entity, nullSpace));
    }

    private boolean isAffected(Entity entity) {
        return !(entity instanceof Player && ((Player) entity).isCreative());
    }

    private void pullEntityTowardsCenter(Entity entity, NullSpaceBlockEntity nullSpace) {
        Vec3 center = nullSpace.getBlockPos().getCenter();
        Vec3 direction = center.subtract(entity.position()).normalize();
        entity.setDeltaMovement(direction.scale(0.1f));
    }

    private void pushEntitiesAwayFromCenter(Entity entity, NullSpaceBlockEntity nullSpace) {
        Vec3 center = nullSpace.getBlockPos().getCenter();
        Vec3 direction = entity.position().subtract(center).normalize();
        entity.setDeltaMovement(direction.scale(0.1f));
    }

    private void preventEntityEscape(NullSpaceBlockEntity nullSpace) {
        for (Entity entity : nullSpace.getEntitiesInside()) {
            if (entity instanceof Player && ((Player) entity).isCreative()) continue;
            adjustEntityPosition(entity, nullSpace);
        }
    }

    private void adjustEntityPosition(Entity entity, NullSpaceBlockEntity nullSpace) {
        Vec3 entityPos = entity.position();
        Vec3 deltaMovement = entity.getDeltaMovement();
        Vec3 center = nullSpace.getBlockPos().getCenter();
        Vec3 updatedPos = entityPos.add(deltaMovement);
        boolean xOut = Math.abs(updatedPos.x - center.x) > nullSpace.getSize() / 2.5;
        boolean yOut = Math.abs(updatedPos.y - center.y) > nullSpace.getSize() / 2.75;
        boolean zOut = Math.abs(updatedPos.z - center.z) > nullSpace.getSize() / 2.5;
        boolean willBeInside = !xOut && !yOut && !zOut;
        if (!willBeInside) {
            entity.setDeltaMovement(deltaMovement.multiply(xOut ? 0 : 1, yOut ? 0 : 1, zOut ? 0 : 1));
            entity.setPos(xOut ? entity.xOld : entity.getX(), yOut ? entity.yOld : entity.getY(), zOut ? entity.zOld : entity.getZ());
        }
    }
}