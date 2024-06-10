package org.madscientists.createelemancy.content.block.distiller;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.madscientists.createelemancy.content.item.MixedSpiceItem;
import org.madscientists.createelemancy.content.registry.ElemancyFluids;
import org.madscientists.createelemancy.content.registry.ElemancyItems;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


public class DistillerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

	public int processingTicks;
	public boolean running;

	//TODO rewrite as data driven recipe
	//Garbled spice overcomplicated
	public boolean garbledRecipe;
	public boolean garbledLiquid;

	SmartFluidTankBehaviour internalTank;
	private int processingIndex;


	static final int PROCESSING_TIME = 100; //in ticks

	private static final int TANK_CAPACITY = 1000; //in milibuckets

	public DistillerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(internalTank = SmartFluidTankBehaviour.single(this, TANK_CAPACITY)
				.allowExtraction()
				.forbidInsertion());
	}



	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		running = compound.getBoolean("Running");
		processingTicks = compound.getInt("Ticks");
		super.read(compound, clientPacket);

		if (clientPacket && hasLevel())
			getBasin().ifPresent(bte -> bte.setAreFluidsMoving(running && processingTicks >0));
	}


	protected Optional<BasinBlockEntity> getBasin() {
		if (level == null)
			return Optional.empty();
		BlockEntity basinTE = level.getBlockEntity(worldPosition.below(2));
		return basinTE instanceof BasinBlockEntity ? Optional.of((BasinBlockEntity) basinTE) : Optional.empty();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putBoolean("Running", running);
		compound.putInt("Ticks", processingTicks);
		super.write(compound, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null)
			return;

		Optional<BasinBlockEntity> basin = getBasin();
		if(basin.isEmpty() || creative(basin.get()) ||
				!recipePresent(basin.get()) || !hasSpace(basin.get()))
			return;

		if(!running) {
			processingTicks = PROCESSING_TIME;
			running = true;
		}

		processingTicks--;
		if (processingTicks == 0) {
			applyBasinRecipe(basin.get());
			sendData();
			running = false;
		}
	}

	private boolean hasSpace(BasinBlockEntity basin) {
		if(getInternalTank().getPrimaryHandler().isEmpty()) {
			return true;
		}
		ItemStack spice = basin.getInputInventory().getItem(processingIndex);
		return getInternalTank()
				.getPrimaryHandler()
				.getFluid()
				.getFluid()
				.isSame(MixedSpiceItem.getDistillResults(spice).getFluid())
				&& getInternalTank()
				.getPrimaryHandler()
				.getFluidAmount() < TANK_CAPACITY;
	}

	public FluidStack getFluid(BasinBlockEntity basin) {
		ItemStack item = basin.getInputInventory().getItem(processingIndex);
		Fluid fluid = MixedSpiceItem.getDistillResults(item).getFluid();
		return new FluidStack(fluid, 1000);
	}

	private boolean recipePresent(BasinBlockEntity basin) {
		BlazeBurnerBlock.HeatLevel heat = BasinBlockEntity.getHeatLevelOf(basin.getLevel()
				.getBlockState(basin.getBlockPos()
						.below(1)));

		if (garbledSpiceRecipe(basin, heat)) return true;

		if (heat != BlazeBurnerBlock.HeatLevel.SEETHING)
			return false;

		if (basin.getInputInventory().getItem(processingIndex).is(ElemancyItems.MIXED_SPICE.get()))
			return true;

		for (int i = 0; i < basin.getInputInventory().getSlots(); i++) {
			if (basin.getInputInventory().getItem(i).is(ElemancyItems.MIXED_SPICE.get())) {
				processingIndex = i;
				return true;
			}
		}
		return false;
	}

	private boolean garbledSpiceRecipe(BasinBlockEntity basin, BlazeBurnerBlock.HeatLevel heat) {
		if (garbledRecipe && basin.getInputInventory().getItem(processingIndex).is(ElemancyItems.DILUTED_GARBLED_SPICE.get()) && heat == BlazeBurnerBlock.HeatLevel.SEETHING)
			return true;

		for (int i = 0; i < basin.getInputInventory().getSlots(); i++) {
			if (basin.getInputInventory().getItem(i).is(ElemancyItems.DILUTED_GARBLED_SPICE.get()) && heat == BlazeBurnerBlock.HeatLevel.SEETHING) {
				processingIndex = i;
				garbledRecipe = true;
				return true;
			}
		}

		if (garbledLiquid && getFluidInBasin(basin, processingIndex).isSame(ElemancyFluids.DILUTED_GARBLED_SPICE.get()) && heat.isAtLeast(BlazeBurnerBlock.HeatLevel.SMOULDERING))
			return true;

		for (int i = 0; i < 2; i++) {
			if (getFluidInBasin(basin, i).isSame(ElemancyFluids.DILUTED_GARBLED_SPICE.get()) && heat.isAtLeast(BlazeBurnerBlock.HeatLevel.SMOULDERING) && hasFluidInBasin(basin, i)) {
				garbledLiquid = true;
				processingIndex = i;
				return true;
			}
		}

		return false;
	}

	private Fluid getFluidInBasin(BasinBlockEntity basin, int tank) {
		return basin.getTanks().getFirst().getCapability().resolve().get().getFluidInTank(tank).getFluid();
	}

	private boolean hasFluidInBasin(BasinBlockEntity basin, int tank) {
		return basin.getTanks().getFirst().getCapability().resolve().get().getFluidInTank(tank).getAmount() >= 1000;
	}

	private boolean creative(BasinBlockEntity basin) {
		if (!internalTank.isEmpty() || !basin.getInputInventory().getItem(0).is(ElemancyItems.CREATIVE_SPICE.get()))
			return false;

		if (!basin.getFilter().getFilter().isEmpty()) {
			for (int i = 0; i < ElemancyElement.getLength(); i++) {
				if (basin.getFilter().getFilter().is(ElemancyElement.values()[i].getSpice())) {
					processingIndex = i;
				}
			}
		}
		internalTank.allowInsertion()
				.getPrimaryHandler().setFluid(new FluidStack(ElemancyElement.values()[processingIndex].getLiquid(),1000));
		internalTank.forbidInsertion();

		processingIndex++;
		if(processingIndex == ElemancyElement.getLength())
			processingIndex = 0;
		return true;
	}

	protected void applyBasinRecipe(BasinBlockEntity basin) {
		if (garbledRecipe || garbledLiquid) {
			if (garbledRecipe) {
				ItemStack spice = basin.getInputInventory().getItem(processingIndex);
				spice.shrink(1);
				basin.getInputInventory().setItem(processingIndex, spice);
			}
			if (garbledLiquid) {
				basin.getTanks().getFirst().getCapability().resolve().get().getFluidInTank(processingIndex).shrink(1000);
				ItemStack spice = basin.getInputInventory().getItem(processingIndex);
				spice.shrink(1);
				basin.getInputInventory().setItem(processingIndex, spice);
			}
			RandomSource randomGen = RandomSource.create();
			boolean random = randomGen.nextInt(10) == 1;
			if (random)
				internalTank.allowInsertion()
						.getPrimaryHandler().fill(new FluidStack(ElemancyElement.getByID(randomGen.nextInt(ElemancyElement.PRIMARY_ELEMENT_LENGTH)).getLiquid(), 100), IFluidHandler.FluidAction.EXECUTE);
			internalTank.forbidInsertion();
			return;
		}

		ItemStack spice = basin.getInputInventory().getItem(processingIndex);
		internalTank.allowInsertion()
				.getPrimaryHandler().fill(MixedSpiceItem.getDistillResults(spice), IFluidHandler.FluidAction.EXECUTE);
		internalTank.forbidInsertion();

		spice = MixedSpiceItem.distill(spice);
		basin.getInputInventory().setItem(processingIndex, spice);
	}




	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		return (side != Direction.DOWN) && isFluidHandlerCap(capability)
				? internalTank.getCapability().cast()
				: super.getCapability(capability, side);
	}

	public SmartFluidTankBehaviour getInternalTank() {
		return internalTank;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return containedFluidTooltip(tooltip, isPlayerSneaking, internalTank.getCapability()
				.cast()) && !internalTank.isEmpty();
	}
}
