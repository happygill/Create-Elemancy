package org.madscientists.createelemancy.content.block.scone;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.List;

public class SpiritSconeBlockEntity extends SmartBlockEntity {


    static final int PROCESSING_TIME = 100; //in ticks
    private static final int TANK_CAPACITY = 1000; //in milibuckets
    ElemancyElement elemancyElement;
    int processingTicks;
    boolean running;
    SmartFluidTankBehaviour internalTank;

    public SpiritSconeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        running = compound.getBoolean("Running");
        processingTicks = compound.getInt("Ticks");
        if (compound.contains("Element"))
            elemancyElement = ElemancyElement.getByID(compound.getInt("Element"));
        super.read(compound, clientPacket);

    }


    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putBoolean("Running", running);
        compound.putInt("Ticks", processingTicks);
        if (elemancyElement != null)
            compound.putInt("Element", elemancyElement.getId());
        super.write(compound, clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(internalTank = SmartFluidTankBehaviour.single(this, TANK_CAPACITY)
                .forbidExtraction()
                .allowInsertion());
    }

    public ElemancyElement getElement() {
        return elemancyElement;
    }

    public void setElement(ElemancyElement elemancyElement) {
        this.elemancyElement = elemancyElement;
    }

    public boolean isRunning() {
        return running;
    }
}
