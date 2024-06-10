package org.madscientists.createelemancy.content.block.scone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.List;

public class SpiritSconeBlockItem extends BlockItem {
    public SpiritSconeBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        CompoundTag tag = pContext.getItemInHand().getOrCreateTag();
        InteractionResult result = super.place(pContext);
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof SpiritSconeBlockEntity scone) {
            if (tag.contains("Element"))
                scone.setElement(ElemancyElement.getByID(tag.getInt("Element")));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        CompoundTag tag = pStack.getOrCreateTag();
        if (tag.contains("Element")) {
            ElemancyElement elemancyElement = ElemancyElement.getByID(tag.getInt("Element"));
            pTooltip.add(Component.literal(elemancyElement + " Spirit").withStyle(elemancyElement.getColorFormatting()));
        } else
            pTooltip.add(Component.literal("Empty"));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}
