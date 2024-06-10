package org.madscientists.createelemancy.content.ability;

import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.ability.api.AbilityUtils;
import org.madscientists.createelemancy.content.item.IAdditionalCreativeItems;

import java.util.List;

public class AbilityTotemItem extends Item implements IAdditionalCreativeItems {
    public AbilityTotemItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains("elemancy_ability")) {
            AbilityUtils.addPlayerAbility(player, NBTHelper.readResourceLocation(tag,"elemancy_ability"));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains("elemancy_ability")){
            ResourceLocation ability = NBTHelper.readResourceLocation(tag,"elemancy_ability");
            return Component.literal(Elemancy.toHumanReadable(ability.getPath()));
        }
        return super.getName(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains("elemancy_ability")) {
            AbilityUtils.addPlayerAbility(interactionTarget, NBTHelper.readResourceLocation(tag,"elemancy_ability"));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains("elemancy_ability")){
            ResourceLocation ability = NBTHelper.readResourceLocation(tag,"elemancy_ability");
            tooltipComponents.add(Component.literal(ability.toString()));
        }
    }

    @Override
    public void addCreativeDevItems(List<ItemStack> pItems) {
        for (ResourceLocation id : AbilityUtils.getAllAbilityTypeIds()) {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = stack.getOrCreateTag();
            NBTHelper.writeResourceLocation(tag,"elemancy_ability",id);
            pItems.add(stack);
        }
    }

}
