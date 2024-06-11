package org.madscientists.createelemancy.content.registry;


import com.google.common.collect.ImmutableSet;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.recipe.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public enum ElemancyRecipes implements IRecipeTypeInfo {
	GRINDING(GrindingRecipe::new),
	DISTILLING(DistillerRecipe::new),
	VORTEX_GEN(VortexGenRecipe::new),
	PRINTING_DEPLOY(PrintingDeployRecipe::new),
	PRINTING_PRESS(PrintingPressRecipe::new),
	PRINTING_FILL(PrintingFillRecipe::new),
	MILK_NULLIFY(MilkNullifyingRecipe::new);


	private final ResourceLocation id;
	private final RegistryObject<RecipeSerializer<?>> serializerObject;
	@Nullable
	private final RegistryObject<RecipeType<?>> typeObject;
	private final Supplier<RecipeType<?>> type;

	ElemancyRecipes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
		String name = Lang.asId(name());
		id = new ResourceLocation(Elemancy.ID, name);
		serializerObject = ElemancyRecipes.Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
		if (registerType) {
			typeObject = ElemancyRecipes.Registers.TYPE_REGISTER.register(name, typeSupplier);
			type = typeObject;
		} else {
			typeObject = null;
			type = typeSupplier;
		}
	}

	ElemancyRecipes(Supplier<RecipeSerializer<?>> serializerSupplier) {
		String name = Lang.asId(name());
		id = new ResourceLocation(Elemancy.ID, name);
		serializerObject = ElemancyRecipes.Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
		typeObject = ElemancyRecipes.Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
		type = typeObject;
	}

	ElemancyRecipes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
		this(() -> new ProcessingRecipeSerializer<>(processingFactory));
	}

	public static void register(IEventBus modEventBus) {
		ShapedRecipe.setCraftingSize(9, 9);
		ElemancyRecipes.Registers.SERIALIZER_REGISTER.register(modEventBus);
		ElemancyRecipes.Registers.TYPE_REGISTER.register(modEventBus);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RecipeSerializer<?>> T getSerializer() {
		return (T) serializerObject.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RecipeType<?>> T getType() {
		return (T) type.get();
	}

	public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
		return world.getRecipeManager()
				.getRecipeFor(getType(), inv, world);
	}

	public static final Set<ResourceLocation> RECIPE_DENY_SET =
			ImmutableSet.of(new ResourceLocation("occultism", "spirit_trade"), new ResourceLocation("occultism", "ritual"));

	public static boolean shouldIgnoreInAutomation(Recipe<?> recipe) {
		return RECIPE_DENY_SET.contains(RegisteredObjects.getKeyOrThrow(recipe.getSerializer())) || recipe.getId().getPath().endsWith("_manual_only");
	}

	private static class Registers {
		private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Elemancy.ID);
		private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, Elemancy.ID);
	}
}
