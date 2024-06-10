package org.madscientists.createelemancy.content.projectile;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.madscientists.createelemancy.content.item.GarbledSpiceItem;
import org.madscientists.createelemancy.content.registry.ElemancyEntities;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

public class PotatoGunHelper {
	public static GarbledSpiceProjectile createGarbledProjectile(PotatoProjectileEntity spice){
		GarbledSpiceProjectile projectile = ElemancyEntities.DILUTED_GARBLED_SPICE_PROJECTILE.create(spice.level());
		projectile.setItem(spice.getItem());

		projectile.setPos(spice.getX(),spice.getY(),spice.getZ());
		projectile.setDeltaMovement(spice.getDeltaMovement());

		projectile.setOwner(spice.getOwner());
		if(spice.getOwner() instanceof Player player) consumeDurability(player);

		return projectile;
	}
	private static int maxUses() {
		return AllConfigs.server().equipment.maxPotatoCannonShots.get();
	}

	private static void consumeDurability(Player player) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if(stack.getItem() instanceof PotatoCannonItem &&
				!BacktankUtil.canAbsorbDamage(player, maxUses()/9))
			stack.hurtAndBreak(9, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		else if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof PotatoCannonItem) {
			stack = player.getItemInHand(InteractionHand.OFF_HAND);
			if (!BacktankUtil.canAbsorbDamage(player, maxUses()/9))
				stack.hurtAndBreak(9, player, p -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
		}
	}

	public static void consumeAllDurability(Player player) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

		if(stack.getItem() instanceof PotatoCannonItem)
			stack.hurtAndBreak(stack.getMaxDamage(), player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));

		else if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof PotatoCannonItem) {
			stack = player.getItemInHand(InteractionHand.OFF_HAND);
			stack.hurtAndBreak(stack.getMaxDamage(), player, p -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
		}
	}

	public static void potatoCannonEvent(EntityJoinLevelEvent event) {
		if(event.getEntity() instanceof GarbledSpiceProjectile || event.getLevel().isClientSide)
			return;

		if (!(event.getEntity() instanceof PotatoProjectileEntity spice))
			return;

		if(spice.getItem().is(ElemancyItems.DILUTED_GARBLED_SPICE.get())) {
			GarbledSpiceProjectile projectile = PotatoGunHelper.createGarbledProjectile(spice);
			event.getLevel().addFreshEntity(projectile);
			event.setCanceled(true);
		}

		if(spice.getItem().is(ElemancyItems.GARBLED_SPICE.get())) {
			if(spice.getOwner() instanceof Player player)
				PotatoGunHelper.consumeAllDurability(player);
			event.getLevel().explode(null, spice.getX(),spice.getY(),spice.getZ(), GarbledSpiceItem.GARBLED_SPICE_EXPLOSION_POWER, true, Level.ExplosionInteraction.NONE);
			event.setCanceled(true);
		}
	}


}
