package org.madscientists.createelemancy.content.block.elemental;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ElementalBlockEntity extends SmartBlockEntity {
    static String UUID_KEY = "elemental_wall_uuid";

    String spellCasterUUID = "";
    int durationTicks=-1;
    static String DURATION_KEY = "elemental_wall_duration";
    public ElementalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        spellCasterUUID = tag.getString(UUID_KEY);
        durationTicks = tag.getInt(DURATION_KEY);
        super.read(tag, clientPacket);

    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        tag.putString(UUID_KEY, spellCasterUUID);
        tag.putInt(DURATION_KEY, durationTicks);
        super.write(tag, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if(durationTicks>=0)
            durationTicks--;
        if(durationTicks==0)
            level.setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
    }

    public String getSpellCasterUUID() {
        return spellCasterUUID;
    }

    public void setSpellCasterUUID(String spellCasterUUID) {
        this.spellCasterUUID = spellCasterUUID;
    }

    public void setSpellCasterUUID(LivingEntity entity) {
        this.spellCasterUUID = entity.getStringUUID();
    }

    public boolean isSpellCaster(LivingEntity player) {
        return spellCasterUUID.equals(player.getStringUUID());
    }
    public int getDurationTicks() {
        return durationTicks;
    }

    public void setDurationTicks(int durationTicks) {
        this.durationTicks = durationTicks;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

}
