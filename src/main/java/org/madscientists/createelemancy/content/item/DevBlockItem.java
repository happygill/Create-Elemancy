package org.madscientists.createelemancy.content.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DevBlockItem extends BlockItem implements IAdditionalCreativeItems{
    public DevBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void addCreativeDevItems(List<ItemStack> pItems) {
        if(this.getBlock() instanceof IAdditionalCreativeItems block)
            block.addCreativeDevItems(pItems);
        else
            pItems.add(new ItemStack(this));
    }

    @Override
    public void addCreativeMainItems(List<ItemStack> pItems) {
        if(this.getBlock() instanceof IAdditionalCreativeItems block)
            block.addCreativeMainItems(pItems);

    }
}
