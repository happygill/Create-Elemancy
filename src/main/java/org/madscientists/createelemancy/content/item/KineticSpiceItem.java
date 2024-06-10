package org.madscientists.createelemancy.content.item;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KineticSpiceItem extends Item implements IAdditionalCreativeItems{

    static final String LIST_NBT_KEY ="Kinetic_Blocks_List";
    static final String BLOCK_POS_NBT_KEY ="Kinetic_Block_Pos";
    static final String BLOCK_SPEED_NBT_KEY ="Kinetic_Block_Speed";


    public KineticSpiceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void addCreativeDevItems(List<ItemStack> pItems) {
        pItems.add(new ItemStack(this));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        tickKineticBlocks(pStack,pLevel);
    }

    private void tickKineticBlocks(ItemStack spice, Level pLevel) {
        CompoundTag tag=spice.getOrCreateTag();
        if(tag.contains(LIST_NBT_KEY)){
            ListTag list=tag.getList(LIST_NBT_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag kineticTag=list.getCompound(i);
                int speed=kineticTag.getInt(BLOCK_SPEED_NBT_KEY);
                BlockPos pos=NbtUtils.readBlockPos(kineticTag.getCompound(BLOCK_POS_NBT_KEY));
                if(pLevel.getBlockEntity(pos) instanceof KineticBlockEntity kb&&kb.getSpeed()!=speed)
                    kb.setSpeed(speed);
            }
        }

    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {

        BlockPos clicked=pContext.getClickedPos();
        ItemStack spice=pContext.getItemInHand();


        if(pContext.getLevel().getBlockEntity(clicked) instanceof KineticBlockEntity block){
            if(pContext.getPlayer().isShiftKeyDown()){
                removeBlock(spice,clicked);
                block.setSpeed(0);
                block.needsSpeedUpdate();
                block.networkDirty=true;
                return InteractionResult.SUCCESS;
            }

            if(pContext.getLevel().isClientSide())return InteractionResult.SUCCESS;

            if(hasBlock(spice,clicked)){
                reverseSpeed(spice,clicked);
            }else {
                addBlock(block, spice,32);
            }
            return InteractionResult.SUCCESS;

        }
        return super.useOn(pContext);
    }

    private void removeBlock(ItemStack spice, BlockPos clicked) {
        CompoundTag tag=spice.getOrCreateTag();
        if(tag.contains(LIST_NBT_KEY)){
            ListTag list=tag.getList(LIST_NBT_KEY, Tag.TAG_COMPOUND);
            int remove=-1;
            for (int i = 0; i < list.size(); i++) {
                CompoundTag kineticTag=list.getCompound(i);
                if(clicked.equals(NbtUtils.readBlockPos(kineticTag.getCompound(BLOCK_POS_NBT_KEY)))) {
                    remove=i;
                }
            }
            if(remove!=-1)
                list.remove(remove);
        }
    }

    private void addBlock(KineticBlockEntity block, ItemStack itemInHand, int speed) {
        CompoundTag tag=itemInHand.getOrCreateTag();
        ListTag list=new ListTag();
        if(tag.contains(LIST_NBT_KEY))
            list=tag.getList(LIST_NBT_KEY, Tag.TAG_COMPOUND);
        CompoundTag KineticTag=new CompoundTag();
        KineticTag.put(BLOCK_POS_NBT_KEY,NbtUtils.writeBlockPos(block.getBlockPos()));
        KineticTag.putInt(BLOCK_SPEED_NBT_KEY,speed);
        list.add(KineticTag);
        tag.put(LIST_NBT_KEY,list);
    }

    private void reverseSpeed(ItemStack spice, BlockPos clicked) {
        CompoundTag tag=spice.getOrCreateTag();
        if(tag.contains(LIST_NBT_KEY)){
            ListTag list=tag.getList(LIST_NBT_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag kineticTag=list.getCompound(i);
                if(clicked.equals(NbtUtils.readBlockPos(kineticTag.getCompound(BLOCK_POS_NBT_KEY))))
                    kineticTag.putInt(BLOCK_SPEED_NBT_KEY,-1*kineticTag.getInt(BLOCK_SPEED_NBT_KEY));
            }
        }
    }

    private boolean hasBlock(ItemStack spice, BlockPos clicked) {
        CompoundTag tag=spice.getOrCreateTag();
        if(tag.contains(LIST_NBT_KEY)){
            ListTag list=tag.getList(LIST_NBT_KEY, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                if(clicked.equals(NbtUtils.readBlockPos(list.getCompound(i).getCompound(BLOCK_POS_NBT_KEY))))
                    return true;
            }
        }
        return false;
    }

}
