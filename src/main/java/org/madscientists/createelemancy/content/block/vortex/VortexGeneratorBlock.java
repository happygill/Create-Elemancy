package org.madscientists.createelemancy.content.block.vortex;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.HitResult;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;

public class VortexGeneratorBlock extends Block implements IBE<VortexGeneratorBlockEntity> {
	public VortexGeneratorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(SHAPE,Shape.SINGLE));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape",Shape.class );

	public enum Shape implements StringRepresentable {
		//1x1
		SINGLE,
		//1x3
		TOP, MIDDLE, BOTTOM,
		//3x1
		SINGLE_CENTER, SINGLE_MIDDLE_NORTH, SINGLE_MIDDLE_SOUTH, SINGLE_MIDDLE_EAST, SINGLE_MIDDLE_WEST,
		SINGLE_CORNER_NORTH_EAST, SINGLE_CORNER_NORTH_WEST, SINGLE_CORNER_SOUTH_EAST, SINGLE_CORNER_SOUTH_WEST,
		//3x3
		MULTI_TOP, MULTI_MIDDLE, MULTI_BOTTOM,MULTI_NORTH_BOTTOM, MULTI_SOUTH_BOTTOM,
		MULTI_NORTH_TOP, MULTI_SOUTH_TOP, MULTI_NORTH_MIDDLE, MULTI_SOUTH_MIDDLE,
		MULTI_EAST_BOTTOM, MULTI_WEST_BOTTOM, MULTI_EAST_TOP, MULTI_WEST_TOP,
		MULTI_EAST_MIDDLE, MULTI_WEST_MIDDLE,  MULTI_NORTH_EAST_BOTTOM, MULTI_NORTH_WEST_BOTTOM,
		MULTI_SOUTH_EAST_BOTTOM, MULTI_SOUTH_WEST_BOTTOM, MULTI_NORTH_EAST_TOP, MULTI_NORTH_WEST_TOP,
		MULTI_SOUTH_EAST_TOP, MULTI_SOUTH_WEST_TOP, MULTI_NORTH_EAST_MIDDLE, MULTI_NORTH_WEST_MIDDLE,
		MULTI_SOUTH_EAST_MIDDLE, MULTI_SOUTH_WEST_MIDDLE;

		@Override
		public String getSerializedName() {
			return Lang.asId(name());
		}
	}


	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return new ItemStack(AllBlocks.FLUID_TANK.get());
	}


	@Override
	public Class<VortexGeneratorBlockEntity> getBlockEntityClass() {
		return VortexGeneratorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends VortexGeneratorBlockEntity> getBlockEntityType() {
		return ElemancyBlockEntities.VORTEX_GENERATOR.get();
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(SHAPE);
		super.createBlockStateDefinition(pBuilder);
	}
}
