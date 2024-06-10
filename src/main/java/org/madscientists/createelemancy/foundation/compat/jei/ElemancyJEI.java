package org.madscientists.createelemancy.foundation.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllFluids;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.foundation.compat.jei.category.DistillingCategory;
import org.madscientists.createelemancy.foundation.compat.jei.category.GrindingCategory;
import org.madscientists.createelemancy.content.recipe.GrindingRecipe;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.registry.ElemancyItems;
import org.madscientists.createelemancy.content.registry.ElemancyRecipes;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.simibubi.create.compat.jei.CreateJEI.*;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class ElemancyJEI implements IModPlugin {
	private static final ResourceLocation ID = Elemancy.asResource("jei_plugin");

	private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
	private IIngredientManager ingredientManager;

	private void loadCategories() {
		allCategories.clear();

		CreateRecipeCategory<?>

				crushing = builder(GrindingRecipe.class)
				.addTypedRecipes(ElemancyRecipes.GRINDING)
				.catalyst(ElemancyBlocks.GRINDING_WHEEL::get)
				.doubleItemIcon(ElemancyBlocks.GRINDING_WHEEL.get(), ElemancyItems.MIXED_SPICE.get())
				.emptyBackground(177, 100)
				.build("grinding", GrindingCategory::new),

				distilling = builder(BasinRecipe.class)
						.addTypedRecipes(ElemancyRecipes.DISTILLING)
						.catalyst(ElemancyBlocks.DISTILLER::get)
						.catalyst(AllBlocks.BASIN::get)
						.doubleItemIcon(ElemancyBlocks.DISTILLER.get(), AllBlocks.BASIN.get())
						.emptyBackground(177, 100)
						.build("distilling", DistillingCategory::new);
	}
	private <T extends Recipe<?>> ElemancyJEI.CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
		return new ElemancyJEI.CategoryBuilder<>(recipeClass);
	}
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		loadCategories();
		registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
	}


	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ingredientManager = registration.getIngredientManager();

		allCategories.forEach(c -> c.registerRecipes(registration));

		registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		allCategories.forEach(c -> c.registerCatalysts(registration));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new BlueprintTransferHandler(), RecipeTypes.CRAFTING);
	}

	@Override
	public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
		PotionFluidSubtypeInterpreter interpreter = new PotionFluidSubtypeInterpreter();
		PotionFluid potionFluid = AllFluids.POTION.get();
		registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getSource(), interpreter);
		registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, potionFluid.getFlowing(), interpreter);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new SlotMover());

		registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler());
		registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler());
	}


	@Override @Nonnull
	public ResourceLocation getPluginUid() {
		return ID;
	}


	private class CategoryBuilder<T extends Recipe<?>> {
		private final Class<? extends T> recipeClass;
		private Predicate<CRecipes> predicate = cRecipes -> true;

		private IDrawable background;
		private IDrawable icon;

		private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
		private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

		public CategoryBuilder(Class<? extends T> recipeClass) {
			this.recipeClass = recipeClass;
		}

		public ElemancyJEI.CategoryBuilder<T> enableIf(Predicate<CRecipes> predicate) {
			this.predicate = predicate;
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> enableWhen(Function<CRecipes, ConfigBase.ConfigBool> configValue) {
			predicate = c -> configValue.apply(c).get();
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
			recipeListConsumers.add(consumer);
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
			return addRecipeListConsumer(recipes -> recipes.addAll(collection.get()));
		}

		public ElemancyJEI.CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred) {
			return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
				if (pred.test(recipe)) {
					//noinspection unchecked
					recipes.add((T) recipe);
				}
			}));
		}

		public ElemancyJEI.CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred, Function<Recipe<?>, T> converter) {
			return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
				if (pred.test(recipe)) {
					recipes.add(converter.apply(recipe));
				}
			}));
		}

		public ElemancyJEI.CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
			return addTypedRecipes(recipeTypeEntry::getType);
		}

		public ElemancyJEI.CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
			return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
		}

		public ElemancyJEI.CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType, Function<Recipe<?>, T> converter) {
			return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipe -> recipes.add(converter.apply(recipe)), recipeType.get()));
		}

		public ElemancyJEI.CategoryBuilder<T> addTypedRecipesIf(Supplier<RecipeType<? extends T>> recipeType, Predicate<Recipe<?>> pred) {
			return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipe -> {
				if (pred.test(recipe)) {
					recipes.add(recipe);
				}
			}, recipeType.get()));
		}

		public ElemancyJEI.CategoryBuilder<T> addTypedRecipesExcluding(Supplier<RecipeType<? extends T>> recipeType,
																	   Supplier<RecipeType<? extends T>> excluded) {
			return addRecipeListConsumer(recipes -> {
				List<Recipe<?>> excludedRecipes = getTypedRecipes(excluded.get());
				CreateJEI.<T>consumeTypedRecipes(recipe -> {
					for (Recipe<?> excludedRecipe : excludedRecipes) {
						if (doInputsMatch(recipe, excludedRecipe)) {
							return;
						}
					}
					recipes.add(recipe);
				}, recipeType.get());
			});
		}

		public ElemancyJEI.CategoryBuilder<T> removeRecipes(Supplier<RecipeType<? extends T>> recipeType) {
			return addRecipeListConsumer(recipes -> {
				List<Recipe<?>> excludedRecipes = getTypedRecipes(recipeType.get());
				recipes.removeIf(recipe -> {
					for (Recipe<?> excludedRecipe : excludedRecipes) {
						if (doInputsMatch(recipe, excludedRecipe)) {
							return true;
						}
					}
					return false;
				});
			});
		}

		public ElemancyJEI.CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
			catalysts.add(supplier);
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
			return catalystStack(() -> new ItemStack(supplier.get()
					.asItem()));
		}

		public ElemancyJEI.CategoryBuilder<T> icon(IDrawable icon) {
			this.icon = icon;
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> itemIcon(ItemLike item) {
			icon(new ItemIcon(() -> new ItemStack(item)));
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
			icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> background(IDrawable background) {
			this.background = background;
			return this;
		}

		public ElemancyJEI.CategoryBuilder<T> emptyBackground(int width, int height) {
			background(new EmptyBackground(width, height));
			return this;
		}

		public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
			Supplier<List<T>> recipesSupplier;
			if (predicate.test(AllConfigs.server().recipes)) {
				recipesSupplier = () -> {
					List<T> recipes = new ArrayList<>();
					for (Consumer<List<T>> consumer : recipeListConsumers)
						consumer.accept(recipes);
					return recipes;
				};
			} else {
				recipesSupplier = Collections::emptyList;
			}

			CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
					new mezz.jei.api.recipe.RecipeType<>(Elemancy.asResource(name), recipeClass),
					Lang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts);
			CreateRecipeCategory<T> category = factory.create(info);
			allCategories.add(category);
			return category;
		}
	}
}
