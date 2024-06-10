package org.madscientists.createelemancy.content.block.vortex;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import static org.madscientists.createelemancy.content.block.vortex.VortexMultiHelper.updateMultiBlock;

public class VCPBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {
	private float genSpeed=1;
	private float stressCapacity=128;
	int size;
	int height;
	private boolean hasVagueMultiBlock;



	BlockPos primaryPos=BlockPos.ZERO;

	public VCPBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		genSpeed = compound.getFloat("genSpeed");
		stressCapacity = compound.getFloat("stressCapacity");
		size = compound.getInt("size");
		height = compound.getInt("height");
		hasVagueMultiBlock = compound.getBoolean("hasVagueMultiBlock");
		primaryPos = NbtUtils.readBlockPos(compound.getCompound("primaryPos"));
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putFloat("genSpeed", genSpeed);
		compound.putFloat("stressCapacity", stressCapacity);
		compound.putInt("size", size);
		compound.putInt("height", height);
		compound.putBoolean("hasVagueMultiBlock", hasVagueMultiBlock);
		compound.put("primaryPos", NbtUtils.writeBlockPos(primaryPos));
	}

	@Override
	public void tick() {
		super.tick();
		updateMultiBlock(this);
		if(!level.isClientSide)
			checkforUpdate();
	}

	@Override
	public void initialize() {
		super.initialize();
		lazyTickCounter = lazyTickRate;
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}


	public void setGeneratedSpeed(float speed) {
		if(genSpeed == speed) return;
		genSpeed = speed;
		updateGeneratedRotation();
		notifyUpdate();
	}

	@Override
	public float getGeneratedSpeed() {
		return genSpeed;
	}

	@Override
	public float calculateAddedStressCapacity() {
		return Math.max(1,stressCapacity/32);
	}

	public void setStressCapacity(float stressCapacity) {
		if(this.stressCapacity == stressCapacity) return;
		this.stressCapacity = stressCapacity;
		notifyUpdate();
	}

	public void setPrimaryPos(BlockPos primaryPos) {
		if(this.primaryPos.equals(primaryPos)) return;
		this.primaryPos = primaryPos;
		updateMultiBlock(this);
		checkforUpdate();
		notifyUpdate();
	}

	@Override
	public AABB getRenderBoundingBox() {
		return super.getRenderBoundingBox().inflate(9);
	}

	private void checkforUpdate() {
		float stressCapacity = 0;
		float generatedSpeed = 0;

		if (isValidVortexBE()) {
			VortexGeneratorBlockEntity vortex = getVortex();
			stressCapacity = vortex.getSUOutput();
			generatedSpeed = vortex.getRPM();
		}

		setStressCapacity(stressCapacity);
		setGeneratedSpeed(generatedSpeed);
	}

	public VortexGeneratorBlockEntity getVortex() {
		if(isValidVortexBE())
			return (VortexGeneratorBlockEntity) level.getBlockEntity(primaryPos);
		return null;
	}

	public boolean isValidVortexBE() {
		return level.getBlockEntity(primaryPos) instanceof VortexGeneratorBlockEntity;
	}
	public boolean hasExisitingVortex() {
		return primaryPos!=null&&!primaryPos.equals(BlockPos.ZERO);
	}


	public BlockPos getPrimaryPos() {
		return primaryPos;
	}

	public void removePrimary() {
		primaryPos = BlockPos.ZERO;
		size = 0;
		height = 0;
		checkforUpdate();
	}
}
