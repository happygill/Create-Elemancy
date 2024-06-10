package org.madscientists.createelemancy.foundation.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.madscientists.createelemancy.content.ability.enchant.TempEnchantmentData.TEMP_ENCHANTS_TAGLIST;
import static org.madscientists.createelemancy.content.ability.enchant.TempEnchantmentData.tempEnchantItemTick;

@Mixin(Item.class)
public abstract class ItemMixin{
    @Inject(
            method = "inventoryTick",
            at = @At("HEAD")
    )
    private void tempEnchant(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected, CallbackInfo ci){
        CompoundTag tag = stack.getTag();
        if(tag != null && tag.contains(TEMP_ENCHANTS_TAGLIST)) {
            tempEnchantItemTick(level, stack);
        }
    }

}
