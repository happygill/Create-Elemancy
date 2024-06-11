package org.madscientists.createelemancy.content.block.vortex;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.recipe.VortexGenRecipe;
import org.madscientists.createelemancy.content.registry.ElemancyRecipes;
import org.madscientists.createelemancy.foundation.network.MultiTankFluidSyncS2CPacket;

import java.util.List;
import java.util.Optional;

public class VortexGeneratorBlockEntity extends SmartBlockEntity implements MultiTankFluidSyncS2CPacket.FluidMultiTankSyncBlockEntity, IHaveGoggleInformation {
	private static final Object VortexRecipesKey = new Object();
	public int height;
	Couple<ElemancyFluidTank> inputs;
	ElemancyFluidTank output;
	protected LazyOptional<IFluidHandler> fluidCapability;
	int size=1;
	int burnTicks=0;
	BlockPos primaryPos=BlockPos.ZERO;
	BlockPos topVCP=BlockPos.ZERO;
	BlockPos bottomVCP=BlockPos.ZERO;
	 VortexGenRecipe recipe;


	public VortexGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

	}

	public int getSizeMultiplier() {
		return (int) Math.max(1, size * .7);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return super.getRenderBoundingBox().inflate(0, height, 0);
	}

	private void initTanks() {
		if (inputs == null || output == null) {
			inputs = Couple.create(new ElemancyFluidTank(1000 * size).forbidExtraction(), new ElemancyFluidTank(1000 * size).forbidExtraction());
			output = new ElemancyFluidTank(2000 * size).forbidInsertion();
			fluidCapability = LazyOptional.of(() -> new CombinedTankWrapper(inputs.getFirst(), inputs.getSecond(), output).enforceVariety());
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!isPrimary()) return;
		if(isInvalid())
			VortexMultiHelper.removeMultiBlock(level, getBlockPos());
		if(burnTicks>0) {
			burnTicks--;
		}
		if(burnTicks==0) {
			if(hasFuel()&&hasOutputSpace())
				consumeFuel();
			else
				turnOff();
		}
		notifyUpdate();
	}

	private boolean isInvalid() {
		if(level.getBlockEntity(topVCP) instanceof VCPBlockEntity vcp) {
			if(vcp.getPrimaryPos().equals(primaryPos)) {
				return false;
			}
		}
		if(level.getBlockEntity(bottomVCP) instanceof VCPBlockEntity vcp) {
			if(vcp.getPrimaryPos().equals(primaryPos)) {
				return false;
			}
		}
		return true;
	}


	public Optional<VortexGenRecipe> getMatchingRecipe() {
		List<Recipe<?>> list = RecipeFinder.get(getRecipeCacheKey(), level, recipe -> recipe.getType() == ElemancyRecipes.VORTEX_GEN.getType());
		return list.stream()
				.filter(recipe -> ((VortexGenRecipe) recipe).fuelMatches(inputs, getSizeMultiplier()))
				.findFirst()
				.map(recipe -> (VortexGenRecipe) recipe);
	}

	public int getVCPCount() {
		if(topVCP.equals(BlockPos.ZERO)&&bottomVCP.equals(BlockPos.ZERO)) return 0;
		int count = 0;
		if(level.getBlockEntity(topVCP) instanceof VCPBlockEntity topVCP&& topVCP.getPrimaryPos().equals(primaryPos)) {
			count++;
		}
		if(level.getBlockEntity(bottomVCP) instanceof VCPBlockEntity bottomVCP&&bottomVCP.getPrimaryPos().equals(primaryPos)) {
			count++;
		}
		return count;
	}

	public int getSUOutput() {
		if(recipe==null) return 0;
		return (recipe.suOutput * getSizeMultiplier() / getVCPCount());
	}

	public int getRPM() {
		if(recipe==null) return 0;
		return recipe.rpm * getSizeMultiplier();
	}


	private void turnOff() {
		burnTicks = 0;
		recipe = null;
	}

	// Refactor consumeFuel method
	private void consumeFuel() {
		if (burnTicks > 0) {
			return;
		}
		recipe = getMatchingRecipe().orElse(null);
		if (recipe == null) {
			return;
		}
		burnTicks = recipe.burnTicks;
		inputs.forEach(ElemancyFluidTank::allowExtraction);
		inputs.forEach(tank -> tank.drain(recipe.getFluidIngredients().get(0).getRequiredAmount() * getSizeMultiplier(), IFluidHandler.FluidAction.EXECUTE));
		inputs.forEach(ElemancyFluidTank::forbidExtraction);
		output.allowInsertion();
		output.fill(new FluidStack(recipe.getFluidResults().get(0), recipe.getFluidResults().get(0).getAmount() * getSizeMultiplier()), IFluidHandler.FluidAction.EXECUTE);
		output.forbidInsertion();
		notifyUpdate();
	}


	public boolean hasFuel() {
		return getMatchingRecipe().isPresent();
	}

	public boolean hasOutputSpace() {
		return output.getFluidAmount() < size * 2000;
	}

	public boolean isPrimary() {
		return primaryPos.equals(this.getBlockPos());
	}

	public void makePrimary(int size,int height) {
		setPrimary(getBlockPos());
		this.size=size;
		this.height=height;
		updateTank();
	}

	public void setPrimary(BlockPos pos) {
		primaryPos = pos;
	}

	public void setVCP(BlockPos pos,boolean top) {
		if(top) {
			topVCP = pos;
		} else {
			bottomVCP = pos;
		}
		notifyUpdate();
	}

	//network Syncing
	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		primaryPos = NbtUtils.readBlockPos(tag.getCompound("primaryPos"));
		topVCP = NbtUtils.readBlockPos(tag.getCompound("topVCP"));
		bottomVCP = NbtUtils.readBlockPos(tag.getCompound("bottomVCP"));
		size = tag.getInt("size");
		height = tag.getInt("height");
		readTank(tag);
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putInt("size", size);
		tag.put("primaryPos", NbtUtils.writeBlockPos(primaryPos));
		tag.put("topVCP", NbtUtils.writeBlockPos(topVCP));
		tag.put("bottomVCP", NbtUtils.writeBlockPos(bottomVCP));
		tag.putInt("height", height);
		writeTank(tag);
	}


	private void readTank(CompoundTag tag) {
		initTanks();
		inputs.getFirst().setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompound("input1")));
		inputs.getSecond().setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompound("input2")));
		output.setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompound("output")));
	}

	private void writeTank(CompoundTag tag) {
		initTanks();
		tag.put("input1", inputs.getFirst().writeToNBT(new CompoundTag()));
		tag.put("input2", inputs.getSecond().writeToNBT(new CompoundTag()));
		tag.put("output", output.writeToNBT(new CompoundTag()));
	}

	private void updateTank() {
		initTanks();
		inputs.getFirst().setCapacity(size * 1000);
		inputs.getSecond().setCapacity(size * 1000);
		output.setCapacity(size * 1000);
		fluidCapability.invalidate();
		fluidCapability = LazyOptional.of(() ->
				new CombinedTankWrapper(inputs.getFirst(), inputs.getSecond(), output).enforceVariety());
		notifyUpdate();
	}


	@Override
	public void invalidate() {
		super.invalidate();
		if (fluidCapability != null)
			fluidCapability.invalidate();
	}


	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap==ForgeCapabilities.FLUID_HANDLER&&getPrimaryVortexGenerator()!=null&&getPrimaryVortexGenerator().fluidCapability!=null) {
			return getPrimaryVortexGenerator().fluidCapability.cast();
		}
		return super.getCapability(cap, side);
	}

	VortexGeneratorBlockEntity getPrimaryVortexGenerator() {
		if(level.getBlockEntity(primaryPos) instanceof VortexGeneratorBlockEntity vge) {
			return vge;
		}
		return null;
	}


	public BlockPos getPrimaryPos() {
		return primaryPos;
    }


	protected Object getRecipeCacheKey() {
		return VortexRecipesKey;
	}

	@Override
	public void setFluid(int tank, FluidStack stack) {
		if (tank == 0)
			inputs.getFirst().setFluid(stack);
		if (tank == 1)
			inputs.getSecond().setFluid(stack);
		if (tank == 2)
			output.setFluid(stack);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (isPrimary()) {


			Lang.translate("gui.goggles.generator_stats")
					.forGoggles(tooltip);
			Lang.translate("tooltip.capacityProvided")
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			float stressTotal =  getSUOutput()*getVCPCount();

			Lang.number(stressTotal)
					.translate("generic.unit.stress")
					.style(ChatFormatting.AQUA)
					.space()
					.add(Lang.translate("gui.goggles.at_current_speed")
							.style(ChatFormatting.DARK_GRAY))
					.forGoggles(tooltip, 1);

			return containedFluidTooltip(tooltip, isPlayerSneaking, getPrimaryVortexGenerator().fluidCapability.cast());
		}
		if (getPrimaryVortexGenerator()!=null)
			return getPrimaryVortexGenerator().addToGoggleTooltip(tooltip, isPlayerSneaking);

		return false;
	}

}
