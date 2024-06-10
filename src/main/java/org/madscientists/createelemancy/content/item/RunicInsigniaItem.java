package org.madscientists.createelemancy.content.item;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.madscientists.createelemancy.content.insignia.InsigniaContext;
import org.madscientists.createelemancy.content.insignia.InsigniaUtils;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.madscientists.createelemancy.content.insignia.InsigniaUtils.INSIGNIA_NAME_KEY;

public class RunicInsigniaItem extends Item implements IAdditionalCreativeItems{
    public RunicInsigniaItem(Properties pProperties) {
        super(pProperties);
    }


    static final String INSIGNIA_NBT_KEY = "insignia_pattern";


    @Override
    public void addCreativeMainItems(List<ItemStack> pItems) {
        CompoundTag tag;
        ItemStack rune;
        for (int i = 0; i < InsigniaUtils.INSIGNIA_PATTERNS.size(); i++) {
            rune = new ItemStack(this);
            tag = rune.getOrCreateTag();
            tag.put(INSIGNIA_NBT_KEY, InsigniaUtils.saveLinesToNBT(InsigniaUtils.INSIGNIA_PATTERNS.get(i).getName(), InsigniaUtils.INSIGNIA_PATTERNS.get(i).getCodes()));
            pItems.add(rune);
        }
    }



    @Override
    public Component getName(ItemStack pStack) {
        CompoundTag tag = pStack.getOrCreateTag();
        if (tag.contains(INSIGNIA_NBT_KEY))
            return Component.literal("Runic " + tag.getCompound(INSIGNIA_NBT_KEY).getString(INSIGNIA_NAME_KEY));
        return super.getName(pStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        InsigniaContext context = new InsigniaContext(pLevel, pPlayer, pUsedHand,true);
        CompoundTag tag = pPlayer.getItemInHand(pUsedHand).getOrCreateTag();
        for (int i = 0; i < InsigniaUtils.INSIGNIA_PATTERNS.size(); i++) {
            if (tag.contains(INSIGNIA_NBT_KEY) &&
                    InsigniaUtils.INSIGNIA_PATTERNS.get(i).getName().equals(tag.getCompound(INSIGNIA_NBT_KEY).getString(INSIGNIA_NAME_KEY))) {
                InsigniaUtils.INSIGNIA_PATTERNS.get(i).applyEffect(context);
                if (pLevel.isClientSide) {
                    Minecraft.getInstance().particleEngine.createTrackingEmitter(pPlayer, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    Minecraft.getInstance().gameRenderer.displayItemActivation(pPlayer.getItemInHand(pUsedHand));
                }
                if(!pPlayer.isCreative()) {
                    if (pPlayer.getItemInHand(pUsedHand).getCount() > 1) {
                        pPlayer.getItemInHand(pUsedHand).shrink(1);
                        if(!pPlayer.addItem(ElemancyItems.RUNIC_SLATE.asStack()))
                            pPlayer.spawnAtLocation(ElemancyItems.RUNIC_SLATE.asStack());
                    }
                    else
                        pPlayer.setItemInHand(pUsedHand, ElemancyItems.RUNIC_SLATE.asStack());
                }
                return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        InsigniaContext context = new InsigniaContext(pContext, null,true);
        CompoundTag tag = pContext.getPlayer().getItemInHand(pContext.getHand()).getOrCreateTag();
        Player pPlayer= pContext.getPlayer();
        for (int i = 0; i < InsigniaUtils.INSIGNIA_PATTERNS.size(); i++) {
            if (tag.contains(INSIGNIA_NBT_KEY) &&
                    InsigniaUtils.INSIGNIA_PATTERNS.get(i).getName().equals(tag.getCompound(INSIGNIA_NBT_KEY).getString(INSIGNIA_NAME_KEY))) {
                InsigniaUtils.INSIGNIA_PATTERNS.get(i).applyEffect(context);
                if (pContext.getLevel().isClientSide) {
                    Minecraft.getInstance().particleEngine.createTrackingEmitter(pContext.getPlayer(), ParticleTypes.TOTEM_OF_UNDYING, 30);
                    Minecraft.getInstance().gameRenderer.displayItemActivation(pContext.getItemInHand());
                }
                if(pPlayer!=null&&!pPlayer.isCreative()) {
                    if (pPlayer.getItemInHand(pContext.getHand()).getCount() > 1) {
                        pPlayer.getItemInHand(pContext.getHand()).shrink(1);
                        if(!pPlayer.addItem(ElemancyItems.RUNIC_SLATE.asStack()))
                            pPlayer.spawnAtLocation(ElemancyItems.RUNIC_SLATE.asStack());
                    }
                    else
                        pPlayer.setItemInHand(pContext.getHand(), ElemancyItems.RUNIC_SLATE.asStack());
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(pContext);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new RunicItemRenderer()));
    }


}
