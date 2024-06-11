package org.madscientists.createelemancy.content.item;

import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.insignia.InsigniaBlockEntity;
import org.madscientists.createelemancy.content.insignia.InsigniaPattern;
import org.madscientists.createelemancy.content.insignia.InsigniaUtils;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.screen.InsigniaScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.madscientists.createelemancy.content.insignia.InsigniaUtils.*;


public class InsigniaGuideItem extends Item implements IAdditionalCreativeItems {
    public InsigniaGuideItem(Properties pProperties) {
        super(pProperties);
    }

    public static final String INSIGNIA_NBT_KEY = "insignia_pattern";

    private static List<String> allTestCodes() {
        List<String> lineRenderingList = new ArrayList<>();
        for (int i = 0; i < InsigniaUtils.lineMappings.keySet().toArray().length; i++) {
            lineRenderingList.add(InsigniaUtils.lineMappings.keySet().toArray()[i] + "" + 7);
        }
        return lineRenderingList;
    }



    public static List<String> testCodes5x5()
    {
        List<String> lineRenderingList = new ArrayList<>();
        for (int i = 0; i < InsigniaUtils.lineMappings.keySet().toArray().length; i++) {
            if(textureList5x5.contains(lineMappings.get(InsigniaUtils.lineMappings.keySet().toArray()[i])))
                lineRenderingList.add(InsigniaUtils.lineMappings.keySet().toArray()[i]+""+7);
        }
        lineRenderingList.add("CA7");
        return lineRenderingList;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand == InteractionHand.MAIN_HAND)
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> displayScreen(stack, player));

        return super.use(level, player, usedHand);
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(ItemStack stack, Player player) {
        InsigniaPattern pattern = InsigniaUtils.getPattern(stack);
        if (player instanceof LocalPlayer && pattern != null)
            ScreenOpener
                    .open(new InsigniaScreen(pattern));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        CompoundTag tag = pStack.getOrCreateTag();
        if (tag.contains(INSIGNIA_NBT_KEY)) {
            pTooltipComponents.add(Component.literal(tag.getCompound(INSIGNIA_NBT_KEY).getString(INSIGNIA_NAME_KEY)));
            if (isComplexInsignia(pStack))
                pTooltipComponents.add(Component.literal("Complex").withStyle(ChatFormatting.RED));

        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new RunicItemRenderer()));
    }

    public InteractionResultHolder<ItemStack> usex(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer != null && !pLevel.isClientSide()) {
            ItemStack guide = pPlayer.getItemInHand(pUsedHand);
            CompoundTag tag = guide.getOrCreateTag();
            if (tag.contains(INSIGNIA_NBT_KEY)) {
                pPlayer.sendSystemMessage(Component.literal(tag.getCompound(INSIGNIA_NBT_KEY).getString(INSIGNIA_NAME_KEY)));
                pPlayer.sendSystemMessage(Component.literal(InsigniaUtils.loadDescriptionFromNBT(tag.getCompound(INSIGNIA_NBT_KEY))));
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() == null) return super.useOn(pContext);
        CompoundTag tag = pContext.getItemInHand().getOrCreateTag();
        if (!tag.contains(INSIGNIA_NBT_KEY)) return super.useOn(pContext);
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof InsigniaBlockEntity)
            return super.useOn(pContext);
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos().above()) instanceof InsigniaBlockEntity)
            return super.useOn(pContext);
        BlockPos pointA = pContext.getClickedPos().above();
        pContext.getLevel().setBlockAndUpdate(pointA, ElemancyBlocks.INSIGNIA.getDefaultState());
        if (pContext.getLevel().getBlockEntity(pointA) instanceof InsigniaBlockEntity insignia) {
            insignia.setGuide(false); //TODO buggy code
            insignia.guideLineRenderingList = InsigniaUtils.loadLinesFromNBT(tag.getCompound(INSIGNIA_NBT_KEY));

            return InteractionResult.SUCCESS;
        }


        return super.useOn(pContext);
    }

    @Override
    public void addCreativeDevItems(List<ItemStack> pItems) {
        CompoundTag tag;

        ItemStack test = new ItemStack(this);
        tag = test.getOrCreateTag();
        tag.put(INSIGNIA_NBT_KEY, InsigniaUtils.saveLinesToNBT("FULL RENDERING TEST", allTestCodes()));
        pItems.add(test);

        ItemStack test5 = new ItemStack(this);
        tag = test5.getOrCreateTag();
        tag.put(INSIGNIA_NBT_KEY, InsigniaUtils.saveLinesToNBT("5X5 RENDERING TEST", testCodes5x5()));
        pItems.add(test5);
    }

    @Override
    public void addCreativeMainItems(List<ItemStack> pItems) {
        CompoundTag tag;
        ItemStack guide;
        for (InsigniaPattern insigniaPattern : InsigniaUtils.INSIGNIA_PATTERNS) {
            guide = new ItemStack(this);
            tag = guide.getOrCreateTag();
            tag.put(INSIGNIA_NBT_KEY, InsigniaUtils.savePatternToNBT(insigniaPattern));
            pItems.add(guide);
        }
    }
}
