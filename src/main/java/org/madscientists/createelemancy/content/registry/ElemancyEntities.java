package org.madscientists.createelemancy.content.registry;

import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileRenderer;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.projectile.GarbledSpiceProjectile;



public class ElemancyEntities {
    public static void init() {
        Elemancy.LOGGER.info("Registering entities!");
    }

    public static final EntityEntry<GarbledSpiceProjectile> DILUTED_GARBLED_SPICE_PROJECTILE =
            register("diluted_garbled_spice_projectile", GarbledSpiceProjectile::new, () -> PotatoProjectileRenderer::new,
                    MobCategory.MISC, 4, 20, true, false, GarbledSpiceProjectile::build);


    private static <T extends Entity> EntityEntry<T> register(String name, EntityType.EntityFactory<T> factory, // that's a lot of parameters
                                                              NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                              MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
                                                              NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
        String id = Lang.asId(name);

        return Elemancy.registrate()
                .entity(id, factory, group)
                .properties(b -> b.setTrackingRange(range)
                        .setUpdateInterval(updateFrequency)
                        .setShouldReceiveVelocityUpdates(sendVelocity))
                .properties(propertyBuilder)
                .properties(b -> {
                    if (immuneToFire)
                        b.fireImmune();
                })
                .renderer(renderer)
                .register();
    }


}
