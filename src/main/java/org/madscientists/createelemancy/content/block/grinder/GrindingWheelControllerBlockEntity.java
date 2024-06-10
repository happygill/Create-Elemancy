package org.madscientists.createelemancy.content.block.grinder;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.madscientists.createelemancy.content.registry.ElemancyDamageSources;
import org.madscientists.createelemancy.content.registry.ElemancyRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GrindingWheelControllerBlockEntity extends SmartBlockEntity {
	private static final int PROCESSING_TIME = 320; //processing time divided by speed to get actual ticks

	public Entity processingEntity;
	public float grindingSpeed;
	private UUID entityUUID;
	protected boolean searchForEntity;

	public ProcessingInventory inventory;
	protected LazyOptional<IItemHandlerModifiable> handler = LazyOptional.of(() -> inventory);
	private final RecipeWrapper wrapper;


	public GrindingWheelControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new ProcessingInventory(this::itemInserted) {
			@Override
			public boolean isItemValid(int slot, ItemStack stack) {

				return super.isItemValid(slot, stack) && processingEntity == null;
			}
		};
		wrapper = new RecipeWrapper(inventory);
	}


	@Override
	public void tick() {
		super.tick();
		loadExistingEntity();

		if (!isOccupied())
			return;
		if (grindingSpeed == 0)
			return;

		if (level.isClientSide)
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::tickAudio);


		Vec3 centerPos = VecHelper.getCenterOf(worldPosition);
		Vec3 outPos = centerPos.add(0, -1f, 0f);

		if (!hasEntity()) {
			inventory.remainingTime -= grindingSpeed;
			spawnParticles(inventory.getStackInSlot(0));

			if (level.isClientSide)
				return;

			if (inventory.remainingTime < 20 && !inventory.appliedRecipe) {
				applyRecipe();
				inventory.appliedRecipe = true;
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);
				return;
			}

			if (inventory.remainingTime > 0) {
				return;
			}
			inventory.remainingTime = 0;

			BlockPos nextPos = worldPosition.offset(0, -1, 0);
			DirectBeltInputBehaviour behaviour =
					BlockEntityBehaviour.get(level, nextPos, DirectBeltInputBehaviour.TYPE);
			if (behaviour != null) {
				boolean changed = false;
				if (!behaviour.canInsertFromSide(Direction.DOWN))
					return;
				for (int slot = 1; slot < inventory.getSlots(); slot++) {
					ItemStack stack = inventory.getStackInSlot(slot);
					if (stack.isEmpty())
						continue;
					ItemStack remainder = behaviour.handleInsertion(stack, Direction.DOWN, false);
					if (remainder.equals(stack, false))
						continue;
					inventory.setStackInSlot(slot, remainder);
					changed = true;
				}
				if (changed) {
					setChanged();
					sendData();
				}
				return;
			}


			// Eject Items
			for (int slot = 1; slot < inventory.getSlots(); slot++) {
				ItemStack stack = inventory.getStackInSlot(slot);
				if (stack.isEmpty())
					continue;
				ItemEntity entityIn = new ItemEntity(level, outPos.x, outPos.y, outPos.z, stack);
				level.addFreshEntity(entityIn);
			}
			ItemStack stack = inventory.getStackInSlot(0).copy();
			inventory.clear();
			if(!stack.isEmpty()) {
				inventory.setStackInSlot(0, stack);
				itemInserted(inventory.getStackInSlot(0));
			}
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);
			return;
		}

		if (!processingEntity.isAlive() || !processingEntity.getBoundingBox()
				.intersects(new AABB(worldPosition).inflate(.5f))) {
			clear();
			return;
		}

		double xMotion = ((worldPosition.getX() + .5f) - processingEntity.getX()) / 2f;
		double zMotion = ((worldPosition.getZ() + .5f) - processingEntity.getZ()) / 2f;
		if (processingEntity.isShiftKeyDown())
			xMotion = zMotion = 0;
		processingEntity.setDeltaMovement(
				new Vec3(xMotion, -.5, zMotion)); // Or they'll only get their feet crushed.

		if (level.isClientSide)
			return;

		if (!(processingEntity instanceof ItemEntity)) {
			Vec3 entityOutPos = outPos.add(0,-.5,0);
			int crusherDamage = AllConfigs.server().kinetics.crushingDamage.get();

			if (processingEntity instanceof LivingEntity) {
				if ((((LivingEntity) processingEntity).getHealth() - crusherDamage <= 0) // Takes LivingEntity instances
						// as exception, so it can
						// move them before it would
						// kill them.
						&& (((LivingEntity) processingEntity).hurtTime <= 0)) { // This way it can actually output the items
					// to the right spot.
					processingEntity.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z);
				}
			}
			processingEntity.hurt(ElemancyDamageSources.grind(level), crusherDamage);
			if (!processingEntity.isAlive()) {
				processingEntity.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z);
			}
			return;
		}

		ItemEntity itemEntity = (ItemEntity) processingEntity;
		itemEntity.setPickUpDelay(20);
		if (processingEntity.getY()  < (centerPos.y - .25f)) {
				intakeItem(itemEntity);
		}

	}


	public void loadExistingEntity() {
		if (searchForEntity) {
			searchForEntity = false;
			List<Entity> search = level.getEntities((Entity) null, new AABB(getBlockPos()),
					e -> entityUUID.equals(e.getUUID()));
			if (search.isEmpty())
				clear();
			else
				processingEntity = search.get(0);
		}
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
	}


	public boolean isOccupied() {
		return hasEntity() || !inventory.isEmpty();
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
		grindingSpeed = tag.getFloat("GrindingSpeed");
		if (tag.contains("Entity") && !isOccupied()) {
			entityUUID = NbtUtils.loadUUID(NBTHelper.getINBT(tag, "Entity"));
			this.searchForEntity = true;
		}
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put("Inventory", inventory.serializeNBT());
		tag.putFloat("GrindingSpeed", grindingSpeed);
		if (hasEntity())
			tag.put("Entity", NbtUtils.createUUID(entityUUID));

	}

	public void startGrinding(Entity entityIn) {
		processingEntity = entityIn;
		entityUUID = entityIn.getUUID();
		notifyUpdate();
	}

	private void itemInserted(ItemStack stack) {
		inventory.remainingTime = PROCESSING_TIME;
		inventory.appliedRecipe = false;
		notifyUpdate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return handler.cast();
		return super.getCapability(cap, side);
	}

	public Optional<ProcessingRecipe<RecipeWrapper>> findRecipe() {
		Optional<ProcessingRecipe<RecipeWrapper>> grindingRecipe = ElemancyRecipes.GRINDING.find(wrapper, level);
		return grindingRecipe;
	}

	private void intakeItem(ItemEntity itemEntity) {
		inventory.clear();
		inventory.setStackInSlot(0, itemEntity.getItem()
				.copy());
		itemInserted(inventory.getStackInSlot(0));
		itemEntity.discard();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);
		notifyUpdate();
	}

	protected void spawnParticles(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		ParticleOptions particleData;
		if (stack.getItem() instanceof BlockItem)
			particleData = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock()
					.defaultBlockState());
		else
			particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);

		RandomSource r = level.random;
		for (int i = 0; i < 4; i++)
			level.addParticle(particleData, worldPosition.getX() + r.nextFloat(), worldPosition.getY() + r.nextFloat(),
					worldPosition.getZ() + r.nextFloat(), 0, 0, 0);
	}

	private void applyRecipe() {
		Optional<ProcessingRecipe<RecipeWrapper>> recipe = findRecipe();
		if (recipe.isPresent()) {
			ItemStack stack = inventory.getStackInSlot(0).copy();
			stack.shrink(1);
			inventory.setStackInSlot(0,stack);
			List<ItemStack> rolledResults = recipe.get()
						.rollResults();

			for (int slot = 0; slot < rolledResults.size() && slot + 1 < inventory.getSlots(); slot++)
				inventory.setStackInSlot(slot + 1, rolledResults.get(slot));
		} else {
			inventory.clear();
		}

	}


	public void clear() {
		processingEntity = null;
		entityUUID = null;
	}

	public boolean hasEntity() {
		return processingEntity != null;
	}

	@OnlyIn(Dist.CLIENT)
	public void tickAudio() {
		float pitch = Mth.clamp((grindingSpeed / 256f) + .45f, .85f, 1f);
		if (entityUUID == null && inventory.getStackInSlot(0)
				.isEmpty())
			return;
		SoundScapes.play(SoundScapes.AmbienceGroup.CRUSHING, worldPosition, pitch);
	}

}
