package org.madscientists.createelemancy.content.item;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.insignia.InsigniaUtils;
import org.madscientists.createelemancy.content.registry.ElemancyElement;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

import java.util.List;

import static org.madscientists.createelemancy.content.insignia.InsigniaUtils.INSIGNIA_NAME_KEY;
import static org.madscientists.createelemancy.content.item.InsigniaGuideItem.INSIGNIA_NBT_KEY;

public class IncompletePrintingItem extends Item implements IAdditionalCreativeItems {
    public IncompletePrintingItem(Properties properties) {
        super(properties);
    }

    public static FluidIngredient setFluid(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        ElemancyElement elemancyElement = ElemancyElement.getByInsigniaCode(InsigniaUtils.loadLinesFromNBT(tag.getCompound(RunicInsigniaItem.INSIGNIA_NBT_KEY)).get(0));
        if (elemancyElement == null) return null;
        return FluidIngredient.FluidStackIngredient.fromFluid(elemancyElement.getLiquid(), Math.max(1000, InsigniaUtils.loadLinesFromNBT(tag.getCompound(RunicInsigniaItem.INSIGNIA_NBT_KEY)).size() * 100));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(INSIGNIA_NBT_KEY)) {
            tooltipComponents.add(Component.literal(tag.getCompound(INSIGNIA_NBT_KEY).getString(INSIGNIA_NAME_KEY)));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    public static ItemStack applyDeployer(ItemStack guide, ItemStack stack) {
        if (!guide.is(ElemancyItems.INSIGNIA_GUIDE.get()))
            return stack;
        CompoundTag guideTag = guide.getOrCreateTag().copy();
        ItemStack result = stack.copy();
        if (stack.is(Items.PAPER)) {
            result = new ItemStack(ElemancyItems.INCOMPLETE_INSIGNIA_GUIDE);
        }
        if (stack.is(ElemancyItems.RUNIC_SLATE.get())) {
            result = new ItemStack(ElemancyItems.INCOMPLETE_RUNIC_SLATE);
        }
        result.setTag(guideTag);
        return result;
    }


    public static ItemStack applyPress(ItemStack stack) {
        if (stack.is(ElemancyItems.INCOMPLETE_INSIGNIA_GUIDE.get())) {
            ItemStack result = new ItemStack(ElemancyItems.INSIGNIA_GUIDE.get());
            result.setTag(stack.getOrCreateTag());
            return result;
        }
        if (stack.is(ElemancyItems.INCOMPLETE_RUNIC_SLATE.get())) {
            ItemStack result = new ItemStack(ElemancyItems.RUNIC_INSIGNIA.get());
            result.setTag(stack.getOrCreateTag());
            return result;
        }
        return stack;
    }


    public static ItemStack applyFill(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("filled", true);
        return stack;
    }
}
