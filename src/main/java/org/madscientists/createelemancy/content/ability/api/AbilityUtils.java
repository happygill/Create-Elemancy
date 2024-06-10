package org.madscientists.createelemancy.content.ability.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.ability.capability.Abilities;
import org.madscientists.createelemancy.content.ability.capability.AbilityProvider;
import java.util.*;
import java.util.function.Supplier;

public class AbilityUtils {
    private static final Map<ResourceLocation, Supplier<Ability>> ABILITY_TYPES =new HashMap<>();

    public static void registerAbility(AbilityType type) {
        if(ABILITY_TYPES.containsKey(type.id())) {
            Elemancy.LOGGER.error("Duplicate ability type: " + type.id());
            return;
        }

        ABILITY_TYPES.put(type.id(), type.factory());
    }
    public static Optional<Ability> getAbility(ResourceLocation id) {
        return Optional.ofNullable(ABILITY_TYPES.get(id)).map(Supplier::get);
    }

    public static List<ResourceLocation> getAllAbilityTypeIds() {
        return ABILITY_TYPES.keySet().stream().toList();
    }


    public static Ability getAbilityFromPlayer(LivingEntity player, AbilityType type) {
        return Abilities.getAbilities(player).stream().findFirst().filter(ability -> ability.getType().equals(type)).orElse(null);
    }
    public static Ability getAbilityFromPlayer(LivingEntity player, ResourceLocation id) {
        return Abilities.getAbilities(player).stream().findFirst().filter(ability -> ability.getType().id().equals(id)).orElse(null);
    }

    public static void addPlayerAbility(LivingEntity player, AbilityType type) {
        player.getCapability(AbilityProvider.ABILITIES).ifPresent(abilities -> {
            Ability ability = type.create();
            abilities.addAbility(player, ability);
        });
    }
    public static void addPlayerAbility(LivingEntity player, ResourceLocation id) {
        getAbility(id).ifPresent(ability -> addPlayerAbility(player, ability.getType()));
    }


    public static void addPlayerAbilityUses(LivingEntity player, AbilityType abilityType, int maxUses) {
        player.getCapability(AbilityProvider.ABILITIES).ifPresent(abilities -> {
            Ability ability = abilityType.create();
            ability.setCount(maxUses);
            abilities.addAbility(player, ability);
        });
    }
    
    public static void addPlayerAbilityDuration(LivingEntity player, AbilityType abilityType, int durationTicks) {
        player.getCapability(AbilityProvider.ABILITIES).ifPresent(abilities -> {
            Ability ability = abilityType.create();
            ability.setDurationTicks(durationTicks);
            abilities.addAbility(player, ability);
        });
    }
    
    public static void addPlayerAbilityUsesAndDuration(LivingEntity player, AbilityType abilityType, int maxUses, int durationTicks) {
        player.getCapability(AbilityProvider.ABILITIES).ifPresent(abilities -> {
            Ability ability = abilityType.create();
            ability.setCountAndDuration(maxUses, durationTicks);
            abilities.addAbility(player, ability);
        });
    }
}
