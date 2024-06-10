package org.madscientists.createelemancy.content.insignia;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.madscientists.createelemancy.content.ability.capability.AbilityProvider;
import org.madscientists.createelemancy.content.ability.enchant.TempEnchantAbility;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.api.AbilityUtils;
import org.madscientists.createelemancy.content.ability.capability.Abilities;
import org.madscientists.createelemancy.content.block.elemental.ElementalBlockEntity;
import org.madscientists.createelemancy.content.ability.enchant.TempEnchantmentData;
import org.madscientists.createelemancy.content.nullspace.NullSpaceHandler;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceEffect;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceType;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.registry.ElemancyNullSpaceTypes;
import org.madscientists.createelemancy.foundation.util.TickHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;


public class InsigniaEffect {

    private static final Map<String, Function<InsigniaContext,InsigniaContext.Result>> EFFECTS_MAP = new HashMap<>();

    public static void init() {
        addEffect("ice_wall", ICE_WALL);
        addEffect("stability_wall", STABILITY_WALL);
        addEffect("reactive_growth", REACTIVE_GROWTH);
        addEffect("spawn_point", SPAWN_POINT);
        addEffect("call_of_lighting", CALL_OF_LIGHTNING);
        addEffect("encrusted_ice", ENCRUSTED_ICE);

        addEffect("scorched_earth", playerAbilityCount(ElemancyAbilityTypes.SCORCHED_EARTH,3));
        addEffect("avian_wings", playerAbilityDuration(ElemancyAbilityTypes.AVIAN_WINGS, TickHelper.minutesToTicks(5)));
        addEffect("arian_ascensions", playerAbilityDuration(ElemancyAbilityTypes.DOUBLE_JUMP,TickHelper.minutesToTicks(5)));
        addEffect("lightweight_aura", playerAbilityDuration(ElemancyAbilityTypes.LIGHTWEIGHT_AURA,TickHelper.minutesToTicks(5)));
        addEffect("energized", playerAbilityDuration(ElemancyAbilityTypes.ENERGIZED, TickHelper.minutesToTicks(5)));
        addEffect("heightened_physique", playerAbilityDuration(ElemancyAbilityTypes.HEIGHTENED_PHYSIQUE, TickHelper.minutesToTicks(5)));

        addEffect("neo_null_space", nullSpaceEffect(ElemancyNullSpaceTypes.NEO, TickHelper.minutesToTicks(10), 3));
        addEffect("inverted_null_space", nullSpaceEffect(ElemancyNullSpaceTypes.INVERSION, -1, 6));
        addEffect("singularity_null_space", nullSpaceEffect(ElemancyNullSpaceTypes.SINGULARITY, -1, 6));
        addEffect("nullstar_null_space", nullSpaceEffect(ElemancyNullSpaceTypes.SIMPLE, -1, 25));

    }

    private static Function<InsigniaContext,InsigniaContext.Result> playerAbilityCount(AbilityType type,int count){
        return insigniaContext -> {
            if(!insigniaContext.getLevel().isClientSide&&insigniaContext.hasPlayer()){
                assert insigniaContext.getPlayer() != null;
                AbilityUtils.addPlayerAbilityUses(insigniaContext.getPlayer(), type,count);
            }
            return InsigniaContext.Result.FINISHED;
        };
    }

    private static Function<InsigniaContext,InsigniaContext.Result> playerAbilityDuration(AbilityType type,int duration){
        return insigniaContext -> {
            if(!insigniaContext.getLevel().isClientSide&&insigniaContext.hasPlayer()){
                assert insigniaContext.getPlayer() != null;
                AbilityUtils.addPlayerAbilityDuration(insigniaContext.getPlayer(),type,duration);
            }
            return InsigniaContext.Result.FINISHED;
        };
    }

    private static Function<InsigniaContext,InsigniaContext.Result> nullSpaceEffect(NullSpaceType type, int duration, int sizeChange) {
        return insigniaContext -> {
            if(!insigniaContext.getLevel().isClientSide&&insigniaContext.hasPlayer()){
                NullSpaceHandler.castNullEffect(insigniaContext.getPlayer(),insigniaContext.getPreferredCenter().below(),type,duration,sizeChange);
            }
            return InsigniaContext.Result.FINISHED;
        };
    }

    private static final Function<InsigniaContext,InsigniaContext.Result> NULL_STAR = insigniaContext -> {
        NullSpaceHandler.castNullEffect(insigniaContext.getPlayer(),insigniaContext.getPreferredCenter().below(),ElemancyNullSpaceTypes.SIMPLE,25);
        return InsigniaContext.Result.FINISHED;
    };

    private static final Function<InsigniaContext,InsigniaContext.Result> CALL_OF_LIGHTNING = insigniaContext -> {
        final String NBT_KEY ="Insignia_Lightning_Count";
        CompoundTag tag = insigniaContext.getCasterNBTData();
        if (tag!=null) {
            tag.putInt(NBT_KEY,5);
        }
        return InsigniaContext.Result.FINISHED;
    };

    private static final Function<InsigniaContext,InsigniaContext.Result> ENCRUSTED_ICE = insigniaContext -> {
        final String NBT_KEY ="Insignia_encrusted_ice";
        CompoundTag tag = insigniaContext.getCasterNBTData();
        if (tag!=null) {
            tag.putBoolean(NBT_KEY,true);
        }
        return InsigniaContext.Result.FINISHED;
    };


    private static final Function<InsigniaContext,InsigniaContext.Result> SPAWN_POINT = insigniaContext -> {
        if (insigniaContext.getLevel() instanceof ServerLevel level && insigniaContext.hasPlayer()) {
            ServerPlayer serverPlayer = level.getServer().getPlayerList().getPlayer(insigniaContext.getPlayer().getUUID());
            if (serverPlayer.getRespawnPosition() != null) {
                BlockPos respawn = serverPlayer.getRespawnPosition();
                serverPlayer.teleportTo(respawn.getX(), respawn.getY(), respawn.getZ());
            }

        }
        return InsigniaContext.Result.FINISHED;
    };

    private static final Function<InsigniaContext,InsigniaContext.Result> ICE_WALL = insigniaContext -> {
        Level level = insigniaContext.getLevel();
        BlockPos pos;

        for (int x = -2; x < 3; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = -2; z < 3; z++) {
                    if (Math.abs(x) > 1 || Math.abs(z) > 1) {
                        pos = insigniaContext.getPreferredCenter().offset(x, y, z);
                        level.setBlockAndUpdate(pos, ElemancyBlocks.FRIGID_WALL.getDefaultState());
                        if (level.getBlockEntity(pos) instanceof ElementalBlockEntity wall) {
                            if (insigniaContext.getPlayer() != null)
                                wall.setSpellCasterUUID(insigniaContext.getPlayer().getStringUUID());
                            wall.setDurationTicks(300);
                        }
                    }
                }

            }
        }
        return InsigniaContext.Result.FINISHED;
    };
    private static final Function<InsigniaContext,InsigniaContext.Result> STABILITY_WALL = insigniaContext -> {
        Level level = insigniaContext.getLevel();
        BlockPos pos;

        for (int x = -2; x < 3; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = -2; z < 3; z++) {
                    if (Math.abs(x) > 1 || Math.abs(z) > 1) {
                        pos = insigniaContext.getPreferredCenter().offset(x, y, z);
                        level.setBlockAndUpdate(pos, ElemancyBlocks.STABILITY_WALL.getDefaultState());
                        if (level.getBlockEntity(pos) instanceof ElementalBlockEntity wall) {
                            wall.setDurationTicks(300);
                        }
                    }
                }

            }
        }
        return InsigniaContext.Result.FINISHED;
    };


    private static final Function<InsigniaContext,InsigniaContext.Result> REACTIVE_GROWTH = insigniaContext -> {
        Level level = insigniaContext.getLevel();
        BlockPos pos;

        for (int x = -12; x < 13; x++) {
            for (int z = -12; z < 13; z++) {
                pos = insigniaContext.getPreferredCenter().offset(x, 0, z);
                if (level.getBlockState(pos).getBlock() instanceof CropBlock crop) {
                    crop.growCrops(level, pos, crop.getStateForAge(crop.getMaxAge()));
                    level.addParticle(ParticleTypes.HAPPY_VILLAGER, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        return InsigniaContext.Result.FINISHED;
    };



    public static void addEffect(String key, Function<InsigniaContext,InsigniaContext.Result> effect) {
        EFFECTS_MAP.put(key, effect);
    }

    public static void removeEffect(String key) {
        EFFECTS_MAP.remove(key);
    }

    public static Function<InsigniaContext,InsigniaContext.Result> getEffect(String key) {
        return EFFECTS_MAP.get(key);
    }

    public static Function<InsigniaContext,InsigniaContext.Result> getPotionEffect(String name, int duration, int amp) {
        return insigniaContext -> {
            if (insigniaContext.getPlayer() != null) {
                Optional<Holder.Reference<MobEffect>> effect = ForgeRegistries.MOB_EFFECTS.getDelegate(new ResourceLocation(name));
                effect.ifPresent(mobEffectReference -> insigniaContext.getPlayer().addEffect(new MobEffectInstance(mobEffectReference.get(), duration * 20, amp)));
            }
            return InsigniaContext.Result.FINISHED;
        };
    }

    public static Function<InsigniaContext,InsigniaContext.Result> getTempEnchantEffect(TempEnchantmentData data,int durationSeconds) {
        return insigniaContext -> {
            if (insigniaContext.getPlayer() != null) {
                TempEnchantAbility ability = new TempEnchantAbility();
                ability.setData(data);
                ability.setDurationTicks(TickHelper.secondsToTicks(durationSeconds));
                insigniaContext.getPlayer().getCapability(AbilityProvider.ABILITIES)
                        .ifPresent(abilities -> abilities.addAbility(insigniaContext.getPlayer(), ability));
            }
            return InsigniaContext.Result.FINISHED;

        };
    }



    public static Function<InsigniaContext,InsigniaContext.Result> getSummonEffect(String name, int count) {
        return insigniaContext -> {
            Optional<EntityType<?>> entityType = EntityType.byString(name);
            BlockPos pos = insigniaContext.getPreferredCenter();
            if (entityType.isPresent()) {
                for (int i = 0; i < count; i++) {
                    Entity entity = entityType.get().create(insigniaContext.getLevel());
                    entity.moveTo(pos, 0, 0);
                    insigniaContext.getLevel().addFreshEntity(entity);
                }
            }
            return InsigniaContext.Result.FINISHED;
        };
    }
}
