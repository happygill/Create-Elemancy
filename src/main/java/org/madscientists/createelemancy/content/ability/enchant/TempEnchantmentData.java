package org.madscientists.createelemancy.content.ability.enchant;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import org.madscientists.createelemancy.content.ability.capability.Abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TempEnchantmentData implements INBTSerializable<CompoundTag> {

    public static String TEMP_ENCHANTS_TAGLIST="tempEnchantList";

    public String getEffectName() {
        return effectName;
    }

    String effectName;
    List<Enchantment> enchantmentList=new ArrayList<>();
    int enchantmentLevel;

    public TempEnchantmentData(String effectName, int enchantmentLevel, Enchantment... enchantments) {
        this.effectName = effectName;
        this.enchantmentList = Arrays.stream(enchantments).toList();
        this.enchantmentLevel = enchantmentLevel;
    }

    public TempEnchantmentData() {}

    public static TempEnchantmentData fromJson(String effectName, JsonObject object){
        TempEnchantmentData data=new TempEnchantmentData();
        data.effectName=effectName;
        data.enchantmentLevel=object.get("level").getAsInt();
        JsonArray enchantArray=object.getAsJsonArray("enchants");
        for (int i = 0; i < enchantArray.size(); i++) {
            Enchantment enchant= ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantArray.get(i).getAsString()));
            if(enchant!=null)
                data.enchantmentList.add(enchant);
        }
        return data;
    }

    public void addTempEnchantToPlayer(Player player) {
        if(enchantmentList.isEmpty()) return;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            for (Enchantment enchant : enchantmentList) {
                if (canTempEnchant(item,enchant)) {
                    int level = 0;
                    if (item.getAllEnchantments().containsKey(enchant))
                        level = item.getAllEnchantments().get(enchant);
                    addNbtToItem(player,item,enchant,level);
                    Map<Enchantment, Integer> enchantsMap = EnchantmentHelper.getEnchantments(item);
                    enchantsMap.put(enchant, Math.min(level + enchantmentLevel,10));
                    EnchantmentHelper.setEnchantments(enchantsMap,item);
                }
            }
        }
    }

    private void addNbtToItem(Player player, ItemStack item, Enchantment enchant, int prevLevel) {
        CompoundTag itemTag=item.getOrCreateTag();
        ListTag list = new ListTag();
        if(itemTag.contains(TEMP_ENCHANTS_TAGLIST))
            list = itemTag.getList(TEMP_ENCHANTS_TAGLIST, Tag.TAG_COMPOUND);
        CompoundTag enchantNBT= new CompoundTag();
        enchantNBT.putUUID("casterUUID",player.getUUID());
        enchantNBT.putString("effectName",effectName);
        enchantNBT.putInt("prevLevel", prevLevel);
        enchantNBT.putString("enchantId",EnchantmentHelper.getEnchantmentId(enchant).toString());
        list.add(enchantNBT);
        itemTag.put(TEMP_ENCHANTS_TAGLIST,list);
    }

    private boolean canTempEnchant(ItemStack item,Enchantment enchantment){

        if(!enchantment.canEnchant(item)&&!enchantExceptions(item,enchantment)) return false;
        CompoundTag itemTag=item.getOrCreateTag();
        if(!itemTag.contains(TEMP_ENCHANTS_TAGLIST)) return true;
        ListTag list=itemTag.getList(TEMP_ENCHANTS_TAGLIST, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            if(list.getCompound(i).getString("enchantId").equals(EnchantmentHelper.getEnchantmentId(enchantment).toString()))
                return false;
        }
        return true;
    }

    private boolean enchantExceptions(ItemStack item, Enchantment enchantment) {
        if(item.getItem() instanceof TridentItem&&(enchantment.equals(Enchantments.SHARPNESS)||enchantment.equals(Enchantments.FIRE_ASPECT)))
            return true;
        return item.getItem() instanceof AxeItem && (enchantment.equals(Enchantments.FIRE_ASPECT));
    }

    public static void tempEnchantItemTick(Level pLevel, ItemStack pStack) {
        if(pLevel.isClientSide())return;
        CompoundTag itemTag=pStack.getOrCreateTag();
        ListTag list=itemTag.getList(TEMP_ENCHANTS_TAGLIST, Tag.TAG_COMPOUND);

        EnchantmentHelper.getEnchantments(pStack).forEach(((enchantment, integer) -> {
            for (int i = 0; i < list.size(); i++) {
                CompoundTag enchantNBT= list.getCompound(i);

                if(!enchantNBT.getString("enchantId").equals(EnchantmentHelper.getEnchantmentId(enchantment).toString())) break;

                Player player = pLevel.getPlayerByUUID(enchantNBT.getUUID("casterUUID"));
                if (player != null && hasEffect(player, enchantNBT.getString("effectName"))) return;

                int prevLevel = enchantNBT.getInt("prevLevel");
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(pStack);
                if (prevLevel == 0)
                    enchants.remove(enchantment);
                else
                    enchants.replace(enchantment, prevLevel);
                EnchantmentHelper.setEnchantments(enchants, pStack);
                list.remove(enchantNBT);
            }
        }));
        itemTag.put(TEMP_ENCHANTS_TAGLIST,list);

    }

    private static boolean hasEffect(Player player, String effectName) {
        return Abilities.getAbilities(player).stream()
                .filter(TempEnchantAbility.class::isInstance)
                .map(TempEnchantAbility.class::cast)
                .anyMatch(tempEnchantAbility -> tempEnchantAbility.getEffectName().equals(effectName));
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag TempEnchantData=new CompoundTag();
        TempEnchantData.putString("effectName",effectName);
        TempEnchantData.putInt("enchantmentLevel",enchantmentLevel);

        CompoundTag enchantList=new CompoundTag();
        enchantList.putInt("size",enchantmentList.size());
        for (int i = 0; i < enchantmentList.size(); i++) {
            enchantList.putString("enchant"+i,EnchantmentHelper.getEnchantmentId(enchantmentList.get(i)).toString());
        }
        TempEnchantData.put("enchantList",enchantList);
        return TempEnchantData;
    }

    @Override
    public void deserializeNBT(CompoundTag TempEnchantData) {
        effectName= TempEnchantData.getString("effectName");
        enchantmentLevel=TempEnchantData.getInt("enchantmentLevel");
        enchantmentList.clear();
        CompoundTag enchantList=TempEnchantData.getCompound("enchantList");
        for (int i = 0; i < enchantList.getInt("size"); i++) {
            Enchantment enchant= ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantList.getString("enchant"+i)));
            if(enchant!=null)
                enchantmentList.add(enchant);
        }
    }



}
