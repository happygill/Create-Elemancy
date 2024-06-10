package org.madscientists.createelemancy.content.registry;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Color;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Flowing;
import org.joml.Vector3f;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.fluid.NullFluid;
import org.madscientists.createelemancy.foundation.config.ElemancyConfig;
import org.madscientists.createelemancy.foundation.network.ElemancyMessages;
import org.madscientists.createelemancy.foundation.network.PoofParticlesS2CPacket;

import java.util.function.Supplier;

import static org.madscientists.createelemancy.Elemancy.*;

@SuppressWarnings("unused")
public class ElemancyFluids {
	public static void init() {
		LOGGER.info("Registering fluids!");
	}

	public static final FluidEntry<Flowing> HEAT = register("heat", NoColorFluidAttributes::new);

	public static final FluidEntry<Flowing> FRIGID = register("frigid", NoColorFluidAttributes::new);

	public static final FluidEntry<Flowing> REACTIVITY = register("reactivity", NoColorFluidAttributes::new);

	public static final FluidEntry<Flowing> STABILITY = register("stability", NoColorFluidAttributes::new);

	public static final FluidEntry<Flowing> DENSITY = register("density", NoColorFluidAttributes::new);

	public static final FluidEntry<Flowing> FEATHERWEIGHT = register("featherweight", NoColorFluidAttributes::new);

	public static final FluidEntry<Flowing> POWER = register("power", NoColorFluidAttributes::new);

	public static final FluidEntry<Flowing> NULL = register("null", "null", NoColorFluidAttributes::new, NullFluid::new);

	public static final FluidEntry<VirtualFluid> GARBLED_SPICE = virtual("garbled_spice", "garbled_spice");

    public static final FluidEntry<VirtualFluid> DILUTED_GARBLED_SPICE = virtual("diluted_garbled_spice", "garbled_spice");

	private static final FluidInteractionRegistry.FluidInteraction POOF = (level, pos1, pos2, state) -> {
		ElemancyMessages.sendToClients(new PoofParticlesS2CPacket(pos1));
		level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 1 | 2); // fixme only removes one fluid
		level.setBlock(pos2, Blocks.AIR.defaultBlockState(), 1 | 2);
	};

	private static final FluidInteractionRegistry.FluidInteraction NULL_POOF = (level, pos1, pos2, state) -> {
		ElemancyMessages.sendToClients(new PoofParticlesS2CPacket(pos1));
		if (level.getFluidState(pos1).getFluidType() != NULL.getType())
			level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 1 | 2);
		if (level.getFluidState(pos2).getFluidType() != NULL.getType())
			level.setBlock(pos2, Blocks.AIR.defaultBlockState(), 1 | 2);
	};

	private static FluidInteractionRegistry.HasFluidInteraction is(FluidType ft) {
		return (level, currentPos, relativePos, currentState) -> level.getFluidState(relativePos).getFluidType() == ft;
	}

	public static void registerFluidInteractions() {
		LOGGER.info("Registering interactions");
		// opposite values go boom/poof
		for (ElemancyElement elemancyElement : ElemancyElement.values()) {
			FluidType fluidType = elemancyElement.getLiquid().getFluidType();
			FluidType opposite = elemancyElement.getOpposite().getLiquid().getFluidType();

			if (elemancyElement == ElemancyElement.NULL || elemancyElement == ElemancyElement.POWER) {
				FluidInteractionRegistry.addInteraction(fluidType,
						new InteractionInformation(is(opposite), oppositeInteraction(() -> 10.0f * ElemancyConfig.server().explosionPowerModifier.get())));
				continue;
			}
			FluidInteractionRegistry.addInteraction(fluidType,
					new InteractionInformation(is(opposite), oppositeInteraction(() -> 4.0f * ElemancyConfig.server().explosionPowerModifier.get())));

			FluidInteractionRegistry.addInteraction(NULL.getType(),
					new InteractionInformation(is(fluidType), NULL_POOF));

			FluidInteractionRegistry.addInteraction(fluidType,
					new InteractionInformation(is(NULL.getType()), NULL_POOF));
		}

		final FluidType WATER = ForgeMod.WATER_TYPE.get();
		final FluidType LAVA = ForgeMod.LAVA_TYPE.get();

		FluidInteractionRegistry.addInteraction(HEAT.getType(),
				new InteractionInformation(is(WATER), POOF));

		registerInteraction(HEAT.getType(), LAVA, AllPaletteStoneTypes.CRIMSITE);
		registerInteraction(FEATHERWEIGHT.getType(), WATER, Blocks.SNOW);
		registerInteraction(FRIGID.getType(), WATER, Blocks.ICE);
		registerInteraction(DENSITY.getType(), HEAT.getType(), Blocks.DEEPSLATE);
		registerInteraction(HEAT.getType(), DENSITY.getType(), Blocks.DEEPSLATE);
		registerInteraction(DENSITY.getType(), AllFluids.HONEY.getType(), Blocks.HONEY_BLOCK);
		registerInteraction(FRIGID.getType(), DENSITY.getType(), Blocks.BLUE_ICE);
		registerInteraction(DENSITY.getType(), FRIGID.getType(), Blocks.BLUE_ICE);
		registerInteraction(FRIGID.getType(), LAVA, AllPaletteStoneTypes.ASURINE);
		registerInteraction(DENSITY.getType(), LAVA, AllPaletteStoneTypes.VERIDIUM);
		registerInteraction(POWER.getType(), LAVA, AllPaletteStoneTypes.OCHRUM);
		registerInteraction(HEAT.getType(), LAVA, AllPaletteStoneTypes.CRIMSITE);
		registerInteraction(FEATHERWEIGHT.getType(), LAVA, AllPaletteStoneTypes.LIMESTONE);
		registerInteraction(REACTIVITY.getType(), LAVA, AllPaletteStoneTypes.SCORIA);
		registerInteraction(STABILITY.getType(), LAVA, AllPaletteStoneTypes.SCORCHIA);
		registerInteraction(STABILITY.getType(), HEAT.getType(), Blocks.MAGMA_BLOCK);
		registerInteraction(HEAT.getType(), STABILITY.getType(), Blocks.MAGMA_BLOCK);
		registerInteraction(FEATHERWEIGHT.getType(), FRIGID.getType(), Blocks.POWDER_SNOW);
		registerInteraction(FRIGID.getType(), FEATHERWEIGHT.getType(), Blocks.POWDER_SNOW);
		registerInteraction(POWER.getType(), AllFluids.HONEY.getType(), Blocks.GLOWSTONE);
	}

	private static FluidInteractionRegistry.FluidInteraction oppositeInteraction(Supplier<Float> explosionPower) {
		return (level, pos1, pos2, state) -> {

			boolean shouldExplode = level.getFluidState(pos2).isSource() || level.getFluidState(pos1).isSource();
			level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 1 | 2);
			level.setBlock(pos2, Blocks.AIR.defaultBlockState(), 1 | 2);

			if (shouldExplode)
				level.explode(null, pos1.getX(), pos1.getY(), pos1.getZ(), explosionPower.get(), Level.ExplosionInteraction.BLOCK);

			else ElemancyMessages.sendToClients(new PoofParticlesS2CPacket(pos1));

		};
	}

	private static void registerInteraction(FluidType f1, FluidType f2, Block result) {
		FluidInteractionRegistry.addInteraction(f1,new InteractionInformation(is(f2),elementInteraction(result)));
	}

	private static FluidInteractionRegistry.FluidInteraction elementInteraction(AllPaletteStoneTypes result) {
		return elementInteraction(result.baseBlock.get());
	}

	private static FluidInteractionRegistry.FluidInteraction elementInteraction(Block result) {
		return (level, pos1, pos2, state) -> {

			boolean source = level.getFluidState(pos1).isSource();
			if(source) {
				level.setBlock(pos1, result.defaultBlockState(), 1 | 2);
			}

		};
	}

	/* * * * * * * * * * */
	/* F A C T O R I E S */
	/* * * * * * * * * * */

	@SuppressWarnings("SameParameterValue")
	private static FluidEntry<Flowing> register(String name, String resourceLocation, FluidBuilder.FluidTypeFactory typeFactory,
																  NonNullFunction<ForgeFlowingFluid.Properties, ? extends ForgeFlowingFluid> customSource,
																  int viscosity, int density, int levelDecreasePerBlock,
																  int tickRate, int slopeFindDistance, float explosionResistance) {

		return registrate().fluid(name,
						Elemancy.asResource("fluid/%s_still".formatted(resourceLocation)),
						Elemancy.asResource("fluid/%s_flow".formatted(resourceLocation)),
						typeFactory)
				.lang(Character.toUpperCase(name.charAt(0)) + name.substring(1))
				.properties(b -> b.viscosity(viscosity).density(density))
				.fluidProperties(p ->
						p.levelDecreasePerBlock(levelDecreasePerBlock)
								.tickRate(tickRate)
								.slopeFindDistance(slopeFindDistance)
								.explosionResistance(explosionResistance)
				)
				.source(customSource)
				.bucket()
				.build()
				.register();
	}

	private static FluidEntry<Flowing> register(String name, String resourceLocation, FluidBuilder.FluidTypeFactory typeFactory) {
		return register(name, resourceLocation, typeFactory, ForgeFlowingFluid.Source::new);
	}

	private static FluidEntry<Flowing> register(String name, String resourceLocation, FluidBuilder.FluidTypeFactory typeFactory,
																  NonNullFunction<ForgeFlowingFluid.Properties, ? extends ForgeFlowingFluid> customSource) {
		return register(name, resourceLocation, typeFactory, customSource, 2000, 1400, 2, 25, 3, 100f);
	}

	private static FluidEntry<Flowing> register(String name, FluidBuilder.FluidTypeFactory typeFactory) {
		return register(name, name, typeFactory);
	}

	private static FluidEntry<Flowing> register(String name, String resourceLocation, int color) {
		return register(name, resourceLocation,
				SolidRenderedPlaceableFluidType.create(color,()->1f/32f));
	}

	private static FluidEntry<Flowing> register(String name, int color) {
		return register(name, name, color);
	}

	private static FluidEntry<VirtualFluid> virtual(String name, String resourceLocation) {
		return virtual(name, toHumanReadable(name), resourceLocation);
	}

	private static FluidEntry<VirtualFluid> virtual(String name, String langName, String resourceLocation) {
		return virtual(name, langName, resourceLocation, CreateRegistrate::defaultFluidType, VirtualFluid::new);
	}

	private static FluidEntry<VirtualFluid> virtual(String name, String resourceLocation, FluidBuilder.FluidTypeFactory typeFactory,
                                                    NonNullFunction<ForgeFlowingFluid.Properties, VirtualFluid> factory) {
        return virtual(name, toHumanReadable(name), resourceLocation, typeFactory, factory);
    }

    private static FluidEntry<VirtualFluid> virtual(String registryName, String langName, String resourceLocation, FluidBuilder.FluidTypeFactory typeFactory,
                                                    NonNullFunction<ForgeFlowingFluid.Properties, VirtualFluid> factory) {
        return registrate().virtualFluid(registryName, Elemancy.asResource("fluid/%s_still".formatted(resourceLocation)),
                        Elemancy.asResource("fluid/%s_still".formatted(resourceLocation)), typeFactory, factory)
                .lang(langName)
                .register();
    }


    private static void registerInteraction(FluidType f1, FluidType f2, Block flowingResult, Block sourceResult) {
        FluidInteractionRegistry.addInteraction(f1,
                new InteractionInformation(f2, fluidState ->
                        fluidState.isSource()
                                ? sourceResult.defaultBlockState()
                                : flowingResult.defaultBlockState()));
		FluidInteractionRegistry.addInteraction(f2,
				new InteractionInformation(f1, fluidState ->
						fluidState.isSource()
								? sourceResult.defaultBlockState()
								: flowingResult.defaultBlockState()));
    }



	private static void registerInteraction(FluidType f1, FluidType f2, AllPaletteStoneTypes result) {
		registerInteraction(f1, f2, result.getBaseBlock().get());
	}

	//end of factories



	private static class NoColorFluidAttributes extends AllFluids.TintedFluidType {
		public NoColorFluidAttributes(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
			super(properties, stillTexture, flowingTexture);
		}

		@Override
		protected int getTintColor(FluidStack stack) {
			return NO_TINT;
		}

		@Override
		public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
			return 0x00ffffff;
		}
	}

	private static class ColorFluidAttributes extends AllFluids.TintedFluidType {
		private final int color;

		public ColorFluidAttributes(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int color) {
			super(properties, stillTexture, flowingTexture);
			this.color = color;
		}

		@Override
		protected int getTintColor(FluidStack stack) {
			return color;
		}

		@Override
		public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
			return color;
		}
	}

	private static class SolidRenderedPlaceableFluidType extends AllFluids.TintedFluidType {

		private Vector3f fogColor;
		private Supplier<Float> fogDistance;

		public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
			return (p, s, f) -> {
				SolidRenderedPlaceableFluidType fluidType = new SolidRenderedPlaceableFluidType(p, s, f);
				fluidType.fogColor = new Color(fogColor, false).asVectorF();
				fluidType.fogDistance = fogDistance;
				return fluidType;
			};
		}

		private SolidRenderedPlaceableFluidType(Properties properties, ResourceLocation stillTexture,
												ResourceLocation flowingTexture) {
			super(properties, stillTexture, flowingTexture);
		}

		@Override
		protected int getTintColor(FluidStack stack) {
			return NO_TINT;
		}

		/*
		 * Removing alpha from tint prevents optifine from forcibly applying biome
		 * colors to modded fluids (this workaround only works for fluids in the solid
		 * render layer)
		 */
		@Override
		public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
			return 0x00ffffff;
		}

		@Override
		protected Vector3f getCustomFogColor() {
			return fogColor;
		}

		@Override
		protected float getFogDistanceModifier() {
			return fogDistance.get();
		}

	}

}