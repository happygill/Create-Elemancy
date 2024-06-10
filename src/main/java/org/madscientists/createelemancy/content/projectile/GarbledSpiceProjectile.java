package org.madscientists.createelemancy.content.projectile;

import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class GarbledSpiceProjectile extends PotatoProjectileEntity {
	public GarbledSpiceProjectile(EntityType<? extends PotatoProjectileEntity> type, Level world) {
		super(type, world);
	}


	@Override
	protected void onHit(HitResult pResult) {
		super.onHit(pResult);
		if (!this.level().isClientSide)
			this.level().explode(this, this.getX(), this.getY(), this.getZ(),
					5.0f, true, Level.ExplosionInteraction.BLOCK);
	}
}
