package org.madscientists.createelemancy.content.nullspace;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.item.IAdditionalCreativeItems;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceUtil;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;

import java.util.List;

public class NullSpaceBlock extends Block implements IBE<NullSpaceBlockEntity>, IAdditionalCreativeItems {

    public NullSpaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<NullSpaceBlockEntity> getBlockEntityClass() {
        return NullSpaceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends NullSpaceBlockEntity> getBlockEntityType() {
        return ElemancyBlockEntities.NULL_SPACE.get();
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        CompoundTag tag = pStack.getOrCreateTag();
        if(tag.contains("nullType"))
            withBlockEntityDo(pLevel, pPos, nullSpace -> nullSpace.addNullEffect(NBTHelper.readResourceLocation(tag, "nullType"),pPlacer));

    }

    @Override
    public void addCreativeDevItems(List<ItemStack> pItems) {
        NullSpaceUtil.getNullSpaceTypeIds().forEach(id->{
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = stack.getOrCreateTag();
            NBTHelper.writeResourceLocation(tag, "nullType", id);
            pItems.add(stack);
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains("nullType")){
            tooltip.add(Component.literal(tag.getString("nullType")));
        }
    }

}
