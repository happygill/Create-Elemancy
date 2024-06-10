package org.madscientists.createelemancy.content.ability.capability;


import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityUtils;
import org.madscientists.createelemancy.content.procs.ProcUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
public class Abilities {

    final List<Ability> abilitiesList = new ArrayList<>();

    /**
     * Retrieves the abilities of an entity.
     *
     * @param entity The entity whose abilities are to be retrieved.
     * @return A list of abilities that the entity has.
     */
    public static List<Ability> getAbilities(LivingEntity entity) {
        return entity.getCapability(AbilityProvider.ABILITIES)
                .map(entityAbilities -> entityAbilities.abilitiesList)
                .orElse(Collections.emptyList());
    }

    /**
     * Adds an ability to an entity.
     * If the entity already has the ability, it is recast. Otherwise, it is added to the entity's abilities list.
     *
     * @param entity The entity to whom the ability is to be added.
     * @param ability The ability to be added.
     */
    public void addAbility(LivingEntity entity, Ability ability) {
        if(!ability.canAbilityBeAddedToEntity(entity)) return;

        Optional<Ability> existingAbility = abilitiesList.stream()
                .filter(a -> a.getType().equals(ability.getType()))
                .findFirst();

        if (existingAbility.isPresent()&&!ability.canMultipleInstancesExist())
            existingAbility.get().onRecast(entity, ability);
         else {
            abilitiesList.add(ability);
            ability.onAdded(entity);
        }
    }



    //Event Handlers
    @SubscribeEvent
    public static void entityTick(LivingEvent.LivingTickEvent event) {
        tick(event.getEntity());
    }

    private static void tick(LivingEntity entity) {
        //overkill check due to crashes
        if(entity.deathTime>0) return;
        if (entity.isDeadOrDying()) return;
        if(getAbilities(entity).isEmpty()) return;
        getAbilities(entity).stream().forEach(ability -> ability.tick(entity));
        getAbilities(entity).stream().filter(Ability::isAbilityComplete).forEach(ability -> ability.onAbilityExpire(entity));
        getAbilities(entity).stream().filter(Ability::isAbilityComplete).forEach(ability -> ability.onAbilityRemove(entity));
        ProcUtils.applyComposites(entity,getAbilities(entity));
        getAbilities(entity).removeIf(Ability::isAbilityComplete);
    }


    public static void handleKeyInput(Player entity, int key, int action, boolean clientSide) {
        getAbilities(entity).forEach(ability -> ability.onKeyInput(key, action, entity, clientSide));
    }

    @SubscribeEvent
    public static void onAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            getAbilities(attacker).forEach(ability -> ability.onAttack(event));
        }

    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        onEntityDeath(event.getEntity());
    }
    private static void onEntityDeath(LivingEntity entity) {
        entity.deathTime = 1;
        getAbilities(entity).forEach(ability -> ability.onDeath(entity));
    }

    @SubscribeEvent
    public static void onentityClone(PlayerEvent.Clone event) {
        onPlayerClone(event.getOriginal(), event.getEntity());
    }
    private static void onPlayerClone(Player entityOld, Player entityNew) {
        entityNew.getCapability(AbilityProvider.ABILITIES).ifPresent(store -> {
            CompoundTag nbt = entityOld.getPersistentData().getCompound("entityAbilitiesList");
            store.loadNBTData(nbt);
        });
    }

    private static void savePersistentData(LivingEntity entity) {
        entity.getCapability(AbilityProvider.ABILITIES).ifPresent(store -> {
            CompoundTag nbt = new CompoundTag();
            store.saveNBTData(nbt);
            entity.getPersistentData().put("entityAbilitiesList", nbt);
        });
    }


    /**
     * Saves the abilities of an entity to NBT data.
     *
     * @param nbt The NBT data where the abilities are to be saved.
     */
    void saveNBTData(CompoundTag nbt) {
        nbt.putInt("abilities_size", abilitiesList.size());
        int i = 0;
        for (Ability ability : abilitiesList) {
            CompoundTag tag = new CompoundTag();
            ability.save(tag);
            NBTHelper.writeResourceLocation(tag,"id",ability.getType().id());
            nbt.put("ability_" + i, tag);
            i++;
        }
    }

    /**
     * Loads the abilities of an entity from NBT data.
     *
     * @param nbt The NBT data from where the abilities are to be loaded.
     */
    void loadNBTData(CompoundTag nbt) {
        abilitiesList.clear();
        int size = nbt.getInt("abilities_size");
        for (int i = 0; i < size; i++) {
            CompoundTag tag = nbt.getCompound("ability_" + i);
            ResourceLocation id = NBTHelper.readResourceLocation(tag,"id");
            AbilityUtils.getAbility(id).ifPresent(ability -> {
                ability.read(tag);
                abilitiesList.add(ability);
            });
        }
    }


}
