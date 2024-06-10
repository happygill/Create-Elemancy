package org.madscientists.createelemancy.content.nullspace;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceType;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceUtil;
import org.madscientists.createelemancy.content.procs.ProcUtils;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyNullSpaceTypes;
import org.madscientists.createelemancy.foundation.util.TickHelper;

import javax.annotation.Nullable;
import java.util.*;

public class NullSpaceBlockEntity extends SmartBlockEntity {

    public static final int MIN_SIZE = 2;
    public static final int MAX_SIZE = 50;
    private int size = MAX_SIZE;
    private int tickCounter = 0;
    private final Map<ResourceLocation, NullSpaceEffect> nullSpaceEffects = new HashMap<>();
    private final List<Entity> entitiesOutside = new ArrayList<>();
    private final List<Entity> entitiesInside = new ArrayList<>();
    private BlockState state= Blocks.AIR.defaultBlockState();
    static final Set<BlockPos> ALL_NULL_SPACES = new HashSet<>();


    public NullSpaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void initialize() {
        super.initialize();
        ALL_NULL_SPACES.add(worldPosition);
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter++;
        updateEntities();
        shrinkIfNecessary();
        removeInvalidEffects();
        tickEffects();
        notifyUpdate();
    }


    private void updateEntities() {
        updateEntitiesInside();
        updateEntitiesOutside();
    }

    private void shrinkIfNecessary() {
        if (isTimeToShrink() && shouldShrink()) {
            if(size > MIN_SIZE)
                setSize(size - 1);
            else
                collapseNullSpace();
        }
    }

    //the standard decay rate is 1block per 50 seconds,
    // however every 5 additional blocks beyond 5 blocks in size,
    // will decrease the time by 5 seconds so at max size the decay rate is 1 block per 5 seconds
    private boolean isTimeToShrink() {
        int i = TickHelper.secondsToTicks(50) - (((size - 5)/5) * TickHelper.secondsToTicks(5));
        return tickCounter % i == 0;
    }

    public void setSize(int size) {
        this.size = Math.max(MIN_SIZE, Math.min(size, MAX_SIZE));
        notifyUpdate();
    }
    private void collapseNullSpace() {
        nullSpaceEffects.values().forEach(effect -> effect.onNullSpaceDestruction(this,tickCounter));
        level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 2, Level.ExplosionInteraction.NONE);
        level.setBlockAndUpdate(worldPosition, state);
    }


    private void updateEntitiesInside() {
        List<Entity> newEntitiesInside = level.getEntities(null, super.getRenderBoundingBox().inflate((size-1)/2.5));
        entitiesInside.stream().filter(entity -> !newEntitiesInside.contains(entity)).forEach(entity -> {
            if(entity instanceof LivingEntity living)
                ProcUtils.addProc(living, ElemancyAbilityTypes.NULL_PROC);
        });
        entitiesInside.clear();
        entitiesInside.addAll(newEntitiesInside);
    }

    private void updateEntitiesOutside() {
        entitiesOutside.clear();
        entitiesOutside.addAll(level.getEntities(null, super.getRenderBoundingBox().inflate((size-1)/2.0+10)));
        entitiesOutside.removeAll(entitiesInside);
    }

    public int getSize() {
        return size;
    }
    public List<Entity> getEntitiesInside() {
        return entitiesInside;
    }
    public List<Entity> getEntitiesOutside() {
        return entitiesOutside;
    }

    public boolean isInverted() {
        return nullSpaceEffects.containsKey(ElemancyNullSpaceTypes.INVERSION.id());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tag.getInt("size");
        tag.getInt("tickCounter");
        readEffectMap(tag.getCompound("nullMap"));
        state= NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),tag.getCompound("state"));

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("size", size);
        tag.putInt("tickCounter", tickCounter);
        tag.put("nullMap", writeEffectMap());
        tag.put("state",NbtUtils.writeBlockState(state));
    }


    public void addNullEffect(ResourceLocation nullType, @Nullable LivingEntity caster) {
        NullSpaceUtil.getNullSpaceEffect(nullType).ifPresent(iNullSpaceEffect -> {
            nullSpaceEffects.put(nullType, iNullSpaceEffect);
            iNullSpaceEffect.onEffectCast(this, caster);
        });
    }
    public void addNullEffect(NullSpaceType nullType, @Nullable LivingEntity caster) {
        addNullEffect(nullType.id(), caster);
    }

    public void setState(BlockState state) {
        this.state = state;
        notifyUpdate();
    }


    private boolean shouldShrink() {
        return nullSpaceEffects.values().stream().anyMatch(effect -> !effect.shouldShrink(this, tickCounter));
    }

    private void removeInvalidEffects() {
        Iterator<Map.Entry<ResourceLocation, NullSpaceEffect>> iterator = nullSpaceEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, NullSpaceEffect> entry = iterator.next();
            if (!entry.getValue().isEffectValid(this, tickCounter)) {
                entry.getValue().onEffectInvalidation(this, tickCounter);
                iterator.remove();
            }
        }
    }

    private void tickEffects() {
        nullSpaceEffects.values().forEach(effect -> effect.tick(this, tickCounter));
    }


    private CompoundTag writeEffectMap() {
        CompoundTag tag = new CompoundTag();
        for (ResourceLocation key : nullSpaceEffects.keySet()) {
            CompoundTag effectTag = new CompoundTag();
            nullSpaceEffects.get(key).save(this,effectTag);
            tag.put(key.toString(), effectTag);
        }
        return tag;
    }


    private void readEffectMap(CompoundTag tag) {
        if (tag.contains("nullMap")) {
            CompoundTag nullMap = tag.getCompound("nullMap");
            nullSpaceEffects.clear();
            for (String key : nullMap.getAllKeys()) {
                ResourceLocation id= ResourceLocation.tryParse(key);
                if(id==null)
                    continue;
                Optional<NullSpaceEffect> effect = NullSpaceUtil.getNullSpaceEffect(id);
                effect.ifPresent(iNullSpaceEffect -> {
                    iNullSpaceEffect.read(this,nullMap.getCompound(key));
                    nullSpaceEffects.put(ResourceLocation.tryParse(key), iNullSpaceEffect);
                });
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(size+1);
    }


    public void addNullEffect(NullSpaceType type, Player caster, int duration) {
        addNullEffect(type.id(), caster);
        nullSpaceEffects.get(type.id()).setDurationTicks(duration);
    }
}
