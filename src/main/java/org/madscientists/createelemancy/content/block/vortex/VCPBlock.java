package org.madscientists.createelemancy.content.block.vortex;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;


public class VCPBlock extends KineticBlock implements IBE<VCPBlockEntity> {

	public static BooleanProperty VCP_SHAFT = BooleanProperty.create("shaft");
	public static BooleanProperty TOP = BooleanProperty.create("top");

	public VCPBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(VCP_SHAFT, Boolean.FALSE));
		this.registerDefaultState(this.defaultBlockState().setValue(TOP, Boolean.TRUE));
		this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));

	}


	public static Direction getFacingDirection(BlockState pstate) {
		return pstate.getValue(TOP)? Direction.UP : Direction.DOWN;
	}


	@Nullable @Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return pContext.getPlayer().isShiftKeyDown()
                ? ElemancyBlocks.VCP.getDefaultState().setValue(TOP, false)
                : super.getStateForPlacement(pContext);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(AXIS);
	}



	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == getRotationAxis(state);

	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target,
									   BlockGetter level, BlockPos pos, Player player) {
		return new ItemStack(ElemancyBlocks.VCP.get());
	}

	@Override
	public Class<VCPBlockEntity> getBlockEntityClass() {
		return VCPBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends VCPBlockEntity> getBlockEntityType() {
		return ElemancyBlockEntities.VCP.get();
	}



	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos,
								 Player player, InteractionHand hand, BlockHitResult hit) {

		if(state.getValue(VCP_SHAFT)){
			if (!player.getItemInHand(hand).getItem().equals(AllItems.WRENCH.get())
					|| !player.isShiftKeyDown())
				return super.use(state, level, pos, player, hand, hit);

			level.setBlockAndUpdate(pos, ElemancyBlocks.VCP.getDefaultState());
			return InteractionResult.CONSUME;
		}

		if(player.getItemInHand(hand).getItem().equals(AllBlocks.SHAFT.get().asItem())) {
			level.setBlockAndUpdate(pos, state.setValue(VCP_SHAFT, true));
			if (!player.isCreative())
				player.getItemInHand(hand).shrink(1);
			return InteractionResult.CONSUME;
		}

		if(player.getItemInHand(hand).getItem().equals(AllBlocks.COGWHEEL.get().asItem())) {
			level.setBlockAndUpdate(pos, ElemancyBlocks.VCP_SMALL_COG.getDefaultState().setValue(TOP, state.getValue(TOP)));
			if (!player.isCreative())
				player.getItemInHand(hand).shrink(1);
			return InteractionResult.CONSUME;
		}

		if (player.getItemInHand(hand).getItem().equals(AllBlocks.LARGE_COGWHEEL.get().asItem())) {
			level.setBlockAndUpdate(pos, ElemancyBlocks.VCP_LARGE_COG.getDefaultState().setValue(TOP, state.getValue(TOP)));
			if (!player.isCreative())
				player.getItemInHand(hand).shrink(1);
			return InteractionResult.CONSUME;
		}
		if (player.getItemInHand(hand).getItem().equals(AllItems.WRENCH.get()))
			return InteractionResult.CONSUME;
		return super.use(state, level, pos, player, hand, hit);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(VCP_SHAFT);
		builder.add(TOP);
		builder.add(AXIS);
		super.createBlockStateDefinition(builder);
	}
}
