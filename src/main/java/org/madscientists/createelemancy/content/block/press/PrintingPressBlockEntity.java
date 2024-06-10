package org.madscientists.createelemancy.content.block.press;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.madscientists.createelemancy.content.insignia.InsigniaUtils;
import org.madscientists.createelemancy.content.registry.ElemancyItems;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;
import static org.madscientists.createelemancy.content.insignia.InsigniaUtils.isComplexInsignia;
import static org.madscientists.createelemancy.content.item.InsigniaGuideItem.INSIGNIA_NBT_KEY;

public class PrintingPressBlockEntity extends MechanicalPressBlockEntity {
    private static final int TANK_CAPACITY = 1000; //in milibuckets
    private static final int RUNIC_ELEMENT_COST = 10; //in milibuckets
    SmartFluidTankBehaviour internalTank;
    private FilteringBehaviour filtering;
    public PrintingPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static int getElementCount(ItemStack insigniaItem) {
        CompoundTag insigniaGuideTag = insigniaItem.getOrCreateTag();
        if (insigniaGuideTag.contains(INSIGNIA_NBT_KEY))
            return InsigniaUtils.loadLinesFromNBT(insigniaGuideTag.getCompound(INSIGNIA_NBT_KEY)).size();
        return 0;
    }

    public static ItemStack getRunicInsignia(ItemStack insigniaItem) {
        ItemStack rune = new ItemStack(ElemancyItems.RUNIC_INSIGNIA.get());
        CompoundTag runeTag = rune.getOrCreateTag();
        CompoundTag insigniaGuideTag = insigniaItem.getOrCreateTag();
        if (insigniaGuideTag.contains(INSIGNIA_NBT_KEY))
            runeTag.put(INSIGNIA_NBT_KEY, insigniaGuideTag.getCompound(INSIGNIA_NBT_KEY));
        else
            return ElemancyItems.RUNIC_SLATE.asStack();
        return rune;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        filtering = new FilteringBehaviour(this, new PressValueBox());
        behaviours.add(filtering);
        behaviours.add(internalTank = SmartFluidTankBehaviour.single(this, TANK_CAPACITY)
                .allowInsertion()
                .forbidExtraction());
        super.addBehaviours(behaviours);
    }

    @Override
    public boolean canProcessInBulk() {
        return false;
    }

    private boolean matchesElementInTank(ItemStack insigniaItem) {
        CompoundTag insigniaGuideTag = insigniaItem.getOrCreateTag();
        if (insigniaGuideTag.contains(INSIGNIA_NBT_KEY)) {
            ElemancyElement elemancyElement = ElemancyElement.getByInsigniaCode(InsigniaUtils.loadLinesFromNBT(insigniaGuideTag.getCompound(INSIGNIA_NBT_KEY)).get(0));
            return elemancyElement != null && elemancyElement.getLiquid().isSame(this.internalTank.getPrimaryHandler().getFluid().getFluid());
        }
        return false;
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        return (side.getAxis() != getBlockState().getValue(HORIZONTAL_FACING)
                .getAxis()) && isFluidHandlerCap(capability)
                ? internalTank.getCapability().cast() : super.getCapability(capability, side);
    }

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        if (!input.stack.is(ElemancyItems.RUNIC_SLATE.get()) && filtering.getFilter().is(ElemancyItems.INSIGNIA_GUIDE.get()))
            return false;
        if (isComplexInsignia(filtering.getFilter()))
            return false;
        if (!matchesElementInTank(filtering.getFilter()))
            return false;
        if (internalTank.getPrimaryHandler().getFluid().getAmount() < getElementCount(filtering.getFilter()) * RUNIC_ELEMENT_COST)
            return false;


        if (simulate)
            return true;
        pressingBehaviour.particleItems.add(input.stack);
        outputList.add(getRunicInsignia(filtering.getFilter()));
        internalTank.allowExtraction();
        internalTank.getPrimaryHandler().drain(getElementCount(filtering.getFilter()) * RUNIC_ELEMENT_COST, IFluidHandler.FluidAction.EXECUTE);
        internalTank.forbidExtraction();
        return true;
    }

    @Override
    public void onPressingCompleted() {
    }

    @Override
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        return false;
    }

    @Override
    public Optional<PressingRecipe> getRecipe(ItemStack item) {
        return Optional.empty();
    }

    // Disables Basin functionality from Mechanical Press
    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        return false;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        boolean fluid = containedFluidTooltip(tooltip, isPlayerSneaking, internalTank.getCapability()
                .cast()) && !internalTank.isEmpty();

        return added || fluid;
    }

    public SmartFluidTankBehaviour getInternalTank() {
        return internalTank;
    }

    static class PressValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 12, 16.05);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis()
                    .isHorizontal();
        }

    }
}
