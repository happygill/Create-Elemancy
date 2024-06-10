package org.madscientists.createelemancy.content.item;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.NetheriteDivingHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

public class GarbledSpiceItem extends Item {
	public GarbledSpiceItem(Properties pProperties) {
		super(pProperties);
	}

	public static final float GARBLED_SPICE_EXPLOSION_POWER = 3.0F;

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if(pEntity.isOnFire())return;
		if(pEntity instanceof Player player && hasNetheriteDivingSuit(player))return;

		pEntity.setSecondsOnFire(5);
	}

	@Override
	public void onDestroyed(ItemEntity pItemEntity) {
		if(!pItemEntity.level().isClientSide && pItemEntity.isAlive()) {
			pItemEntity.discard();
			pItemEntity.level().explode(null, pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), GARBLED_SPICE_EXPLOSION_POWER, true, Level.ExplosionInteraction.BLOCK);
		}
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, Player player) {
		return super.onDroppedByPlayer(item, player);
	}

	public static boolean hasNetheriteDivingSuit(Player player) {
		return  player.isCreative() ||
				(player.getInventory().armor.get(3).is(AllItems.NETHERITE_DIVING_HELMET.get()) &&
						player.getInventory().armor.get(2).is(AllItems.NETHERITE_BACKTANK.get()) &&
						NetheriteDivingHandler.isNetheriteArmor(player.getInventory().armor.get(1)) &&
						NetheriteDivingHandler.isNetheriteArmor(player.getInventory().armor.get(0)));

	}

	public static void garbledSpiceItemExpire(ItemExpireEvent event) {
		ItemEntity entity = event.getEntity();
		if(entity.getItem().getItem() instanceof GarbledSpiceItem) {
			entity.discard();
			entity.level().explode(null, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), GARBLED_SPICE_EXPLOSION_POWER, true, Level.ExplosionInteraction.BLOCK);
		}
	}
}
