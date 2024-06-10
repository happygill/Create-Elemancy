package org.madscientists.createelemancy.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.madscientists.createelemancy.content.insignia.InsigniaBlockEntity;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.registry.ElemancyElement;


public class ChalkItem extends Item {

	private final ElemancyElement elemancyElement;
	public ChalkItem(Properties pProperties, ElemancyElement elemancyElement) {
		super(pProperties);
		this.elemancyElement = elemancyElement;

	}

	private static BlockPos getCenterBetween(BlockPos A, BlockPos B) {
		return new BlockPos((A.getX() + B.getX()) / 2, A.getY(), (B.getZ() + A.getZ()) / 2);
	}

	public static BlockPos getPointA(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("pointA")) {
			return NbtUtils.readBlockPos(stack.getTag().getCompound("pointA"));
		}
		return BlockPos.ZERO;
	}

	public static void setPointA(ItemStack stack, BlockPos pointA) {
		stack.getOrCreateTag().put("pointA", NbtUtils.writeBlockPos(pointA));
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
		if(pIsSelected&&pEntity instanceof Player player) {

			if (pLevel.getBlockEntity(BlockPos.containing(player.pick(10,0,false).getLocation())) instanceof InsigniaBlockEntity insignia&&insignia.hasCenter()) {
					insignia.getCenterInsignia().chalkHover(player.pick(10.0D, 0.0F, false).getLocation(), elemancyElement);
					return;
			}

			if (getPointA(pStack).equals(BlockPos.ZERO)) return;

			if (pLevel.getBlockEntity(getPointA(pStack)) instanceof InsigniaBlockEntity insignia) {
				insignia.chalkHover(player.pick(10.0D, 0.0F, false).getLocation(), elemancyElement);
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
		if (pLevel.getBlockEntity(getPointA(itemInHand)) instanceof InsigniaBlockEntity insignia)
			if (!insignia.isGhost() && insignia.hasCenter()) {
				insignia.getCenterInsignia().clearPoint();
				insignia.getCenterInsignia().ghostLineRenderingList.clear();
				return InteractionResultHolder.success(itemInHand);
			}
		return super.use(pLevel, pPlayer, pUsedHand);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		if (pContext.getPlayer() == null) return super.useOn(pContext);
		ItemStack stack = pContext.getItemInHand();
		if (pContext.getPlayer().isShiftKeyDown()) {
			if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof InsigniaBlockEntity)
				return InteractionResult.FAIL;
			setPointA(stack,pContext.getClickedPos().above());
			pContext.getLevel().setBlockAndUpdate(getPointA(stack), ElemancyBlocks.INSIGNIA.getDefaultState());
			if (pContext.getLevel().getBlockEntity(getPointA(stack)) instanceof InsigniaBlockEntity insignia) {
				insignia.setGhost(true);
			}
			return InteractionResult.SUCCESS;
		}

		if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof InsigniaBlockEntity insignia) {
			if (!insignia.isGhost() && insignia.hasCenter()) {
				setPointA(stack,pContext.getClickedPos());
				pContext.getItemInHand().hurtAndBreak(insignia.getCenterInsignia().chalkClick(pContext, elemancyElement), pContext.getPlayer(), (player -> {
				}));
				return InteractionResult.SUCCESS;
			}
		} else {
			if (pContext.getLevel().getBlockEntity(getPointA(stack)) instanceof InsigniaBlockEntity insignia)
				if (!insignia.isGhost() && insignia.hasCenter()) {
					insignia.getCenterInsignia().clearPoint();
					insignia.getCenterInsignia().ghostLineRenderingList.clear();
					return InteractionResult.SUCCESS;
				}
		}


		if (getPointA(stack) != BlockPos.ZERO && getPointA(stack).equals(pContext.getClickedPos())) {
			if (pContext.getLevel().getBlockEntity(getPointA(stack)) instanceof InsigniaBlockEntity insignia) {
				if (insignia.isGhost() || !insignia.hasCenter()) {
					insignia.createInsigniaCircle(InsigniaBlockEntity.InsigniaSize.SMALL, elemancyElement, getPointA(stack));
					pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), (player -> {
					}));
				}
			}
			return InteractionResult.SUCCESS;
		}

		if(getPointA(stack)!=BlockPos.ZERO&&(Math.abs(getPointA(stack).getX()-pContext.getClickedPos().getX())==2&&Math.abs(getPointA(stack).getZ()-pContext.getClickedPos().getZ())==2)) {
			if (pContext.getLevel().getBlockEntity(getPointA(stack)) instanceof InsigniaBlockEntity insignia) {
				if (insignia.isGhost() || !insignia.hasCenter()) {
					insignia.createInsigniaCircle(InsigniaBlockEntity.InsigniaSize.MEDIUM, elemancyElement, getCenterBetween(getPointA(stack), pContext.getClickedPos()));
					pContext.getItemInHand().hurtAndBreak(3, pContext.getPlayer(), (player -> {
					}));
				}
			}
			return InteractionResult.SUCCESS;
		}


		if(getPointA(stack)!=BlockPos.ZERO&&(Math.abs(getPointA(stack).getX()-pContext.getClickedPos().getX())==4&&Math.abs(getPointA(stack).getZ()-pContext.getClickedPos().getZ())==4)) {
			if (pContext.getLevel().getBlockEntity(getPointA(stack)) instanceof InsigniaBlockEntity insignia) {
				if (insignia.isGhost() || !insignia.hasCenter()) {
					insignia.createInsigniaCircle(InsigniaBlockEntity.InsigniaSize.LARGE, elemancyElement, getCenterBetween(getPointA(stack), pContext.getClickedPos()));
					pContext.getItemInHand().hurtAndBreak(5, pContext.getPlayer(), (player -> {
					}));
				}
			}
			return InteractionResult.SUCCESS;
		}
		return super.useOn(pContext);
	}
}
