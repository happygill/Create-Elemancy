package org.madscientists.createelemancy.foundation.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.ability.capability.Abilities;
import org.madscientists.createelemancy.content.ability.capability.AbilityProvider;
import org.madscientists.createelemancy.content.insignia.InsigniaBlockEntity;
import org.madscientists.createelemancy.content.insignia.InsigniaPattern;

import static org.madscientists.createelemancy.content.insignia.InsigniaContext.getCasterNBTFromPlayer;

@Mod.EventBusSubscriber(modid = Elemancy.ID)
public class CommonEvents {
    public static void init() {
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(InsigniaPattern.ReloadListener.INSTANCE);
    }


    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player != null)
            InsigniaPattern.syncTo(player);
        else
            InsigniaPattern.syncToAll();

    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(Abilities.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            if (!event.getObject().getCapability(AbilityProvider.ABILITIES).isPresent()) {
                event.addCapability(Elemancy.asResource("ability_map"), new AbilityProvider());
            }
        }
    }


    @SubscribeEvent
    public static void leftClickInsignia(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().getBlockEntity(event.getPos()) instanceof InsigniaBlockEntity insignia && insignia.erase(event.getEntity()))
            event.setCanceled(true);
    }



    @SubscribeEvent
    public static void EncrustedIce(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            CompoundTag nbt = getCasterNBTFromPlayer(player);
            final String NBT_KEY = "Insignia_encrusted_ice";
            if (nbt.contains(NBT_KEY)) {
                if (nbt.getBoolean(NBT_KEY)) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 3));
                    nbt.putBoolean(NBT_KEY, false);
                }
                nbt.remove(NBT_KEY);
            }
        }
    }

    @SubscribeEvent
    public static void ArrowLightning(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof AbstractArrow arrow&&arrow.getOwner() instanceof Player player) {

            CompoundTag nbt = getCasterNBTFromPlayer(player);
            Level level = event.getEntity().level();
            if (nbt == null) return;

            final String NBT_KEY = "Insignia_Lightning_Count";
            if (nbt.contains(NBT_KEY)) {
                int count = nbt.getInt(NBT_KEY);
                if (count > 0) {
                    count--;
                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                    lightningbolt.moveTo(event.getRayTraceResult().getLocation());
                    lightningbolt.setCause(player instanceof ServerPlayer ? (ServerPlayer) player : null);
                    level.addFreshEntity(lightningbolt);
                    nbt.putInt(NBT_KEY, count);
                }
                if (count == 0)
                    nbt.remove(NBT_KEY);
            }

        }
    }

    @Mod.EventBusSubscriber(modid = Elemancy.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        }
    }
}
