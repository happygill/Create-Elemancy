package org.madscientists.createelemancy.content.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nullable;

public class ElemancyDamageSources {

	public static DamageSource grind(Level level) {
		return source(ElemancyDamageTypes.GRIND, level);
	}

	public static DamageSource forces(Level level) {return source(ElemancyDamageTypes.FORCES, level);}

	public static DamageSource nullified(Level level) {return source(ElemancyDamageTypes.NULLIFIED, level);}


	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
		Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(registry.getHolderOrThrow(key));
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity entity) {
		Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(registry.getHolderOrThrow(key), entity);
	}

	private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity causingEntity, @Nullable Entity directEntity) {
		Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(registry.getHolderOrThrow(key), causingEntity, directEntity);
	}
}
