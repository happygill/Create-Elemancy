package org.madscientists.createelemancy.content.projectile;

import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonProjectileType;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

@SuppressWarnings("unused")
public class ElemancyPotatoProjectileTypes {
	public static final PotatoCannonProjectileType
			DILUTED_GARBLED_SPICE_PROJECTILE = new PotatoCannonProjectileType.Builder(Elemancy.asResource(("diluted_garbled_spice_type")))
			.damage(15)
			.reloadTicks(20)
			.knockback(0.3f)
			.velocity(1.5f)
			.renderTumbling()
			.soundPitch(1.0f)
			.registerAndAssign(ElemancyItems.DILUTED_GARBLED_SPICE.get());
	public static final PotatoCannonProjectileType
			GARBLED_SPICE_PROJECTILE = new PotatoCannonProjectileType.Builder(Elemancy.asResource(("garbled_spice_type")))
			.registerAndAssign(ElemancyItems.GARBLED_SPICE.get());
	public static void init() {
		Elemancy.LOGGER.info("Registering Projectiles!");
	}

}
