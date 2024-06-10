package org.madscientists.createelemancy.content.registry;


import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.block.distiller.DistillerBlock;
import org.madscientists.createelemancy.content.block.elemental.ElementalWallBlock;
import org.madscientists.createelemancy.content.block.elemental.ScorchedFire;
import org.madscientists.createelemancy.content.block.grinder.GrindingWheelBlock;
import org.madscientists.createelemancy.content.block.grinder.GrindingWheelControllerBlock;
import org.madscientists.createelemancy.content.nullspace.NullSpaceBlock;
import org.madscientists.createelemancy.content.block.press.PrintingPressBlock;
import org.madscientists.createelemancy.content.block.scone.SpiritSconeBlock;
import org.madscientists.createelemancy.content.block.scone.SpiritSconeBlockItem;
import org.madscientists.createelemancy.content.block.vortex.VCPBlock;
import org.madscientists.createelemancy.content.block.vortex.VCPCogWheelBlock;
import org.madscientists.createelemancy.content.block.vortex.VCPLargeCogWheelBlock;
import org.madscientists.createelemancy.content.block.vortex.VortexGeneratorBlock;
import org.madscientists.createelemancy.content.insignia.InsigniaBlock;
import org.madscientists.createelemancy.content.item.DevBlockItem;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;
import static org.madscientists.createelemancy.Elemancy.LOGGER;
import static org.madscientists.createelemancy.Elemancy.asResource;

@SuppressWarnings({"unused", "removal"})
public class ElemancyBlocks {

	public static void init() {
		LOGGER.info("Registering blocks!");
	}

    private static final CreateRegistrate REGISTRATE = Elemancy.registrate();

    public static final BlockEntry<GrindingWheelBlock> GRINDING_WHEEL = REGISTRATE
            .block("grinding_wheel", GrindingWheelBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .transform(BlockStressDefaults.setImpact(8.0))
            .item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<ScorchedFire> SCORCHED_FIRE = REGISTRATE
			.block("scorched_fire", ScorchedFire::new)
			.initialProperties(()->Blocks.FIRE)
			.properties(BlockBehaviour.Properties::noLootTable)
			.blockstate(NonNullBiConsumer.noop())
			.addLayer(() -> RenderType::cutoutMipped)
			.item(DevBlockItem::new)
			.build()
			.register();



	public static final BlockEntry<VortexGeneratorBlock> VORTEX_GENERATOR = REGISTRATE
			.block("vortex_generator", VortexGeneratorBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.METAL))
			.properties(BlockBehaviour.Properties::noOcclusion)
			.loot((lt, b) -> lt.add(b,lt.createSingleItemTable(AllBlocks.FLUID_TANK.get())))
			.transform(pickaxeOnly())
			.addLayer(() -> RenderType::cutoutMipped)
			.blockstate(NonNullBiConsumer.noop())
			.transform(BlockStressDefaults.setCapacity(2048.0))
			.transform(BlockStressDefaults.setGeneratorSpeed(() -> Couple.create(0, 256)))
			.register();

	public static final BlockEntry<VCPBlock> VCP = REGISTRATE
			.block("vortex_control_point", VCPBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.METAL))
			.properties(BlockBehaviour.Properties::noOcclusion)
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStatesExcept(state -> {

						String facing = state.getValue(VCPBlock.TOP) ? "top" : "bottom";
						String shaft = state.getValue(VCPBlock.VCP_SHAFT) ? "shaft_" : "";

						return ConfiguredModel.builder()
								.modelFile(p.models()
										.getExistingFile(asResource("block/vortex_control_point/vcp_"+shaft+facing)))
								.build();
					}, AXIS)
			)
			.transform(pickaxeOnly())
			.addLayer(() -> RenderType::cutoutMipped)
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<VCPLargeCogWheelBlock> VCP_LARGE_COG = REGISTRATE
			.block("vortex_control_point_large_cog", VCPLargeCogWheelBlock::new)
			.lang("Vortex Control Point")
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.METAL))
			.properties(BlockBehaviour.Properties::noOcclusion)
			.loot((lt, b) -> lt.add(b,lt.createSingleItemTable(ElemancyBlocks.VCP.get())))
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStatesExcept(state -> {

						String facing=state.getValue(VCPBlock.TOP)?"top":"bottom";
						if(state.getValue(AXIS) == Direction.Axis.X) {

							return ConfiguredModel.builder()
									.modelFile(p.models()
											.getExistingFile(asResource("block/vortex_control_point/vcp_large_" + facing)))
									.build();
						}
						return ConfiguredModel.builder()
								.modelFile(p.models()
										.getExistingFile(asResource("block/vortex_control_point/vcp_large_" + facing)))
								.rotationY(90)
								.build();

					}, VCPBlock.VCP_SHAFT,DirectionalKineticBlock.FACING)
			)
			.transform(pickaxeOnly())
			.addLayer(() -> RenderType::cutoutMipped)
			.register();

	public static final BlockEntry<VCPCogWheelBlock> VCP_SMALL_COG = REGISTRATE
			.block("vortex_control_point_small_cog", VCPCogWheelBlock::new)
			.lang("Vortex Control Point")
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.METAL))
			.properties(BlockBehaviour.Properties::noOcclusion)
			.loot((lt, b) -> lt.add(b,lt.createSingleItemTable(ElemancyBlocks.VCP.get())))
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStatesExcept(state -> {

						String facing=state.getValue(VCPBlock.TOP)?"top":"bottom";

						return ConfiguredModel.builder()
								.modelFile(p.models()
										.getExistingFile(asResource("block/vortex_control_point/vcp_small_" + facing)))
								.build();
					}, VCPBlock.VCP_SHAFT,DirectionalKineticBlock.FACING,AXIS)
			)
			.transform(pickaxeOnly())
			.addLayer(() -> RenderType::cutoutMipped)
			.register();


	public static final BlockEntry<GrindingWheelControllerBlock> GRINDING_WHEEL_CONTROLLER = REGISTRATE
			.block("grinding_wheel_controller", GrindingWheelControllerBlock::new)
			.properties(p -> p.mapColor(MapColor.STONE)
					.noOcclusion()
					.noLootTable()
					.air()
					.noCollission()
					.pushReaction(PushReaction.BLOCK))
			.blockstate((c, p) -> p.getVariantBuilder(c.get())
					.forAllStates(BlockStateGen.mapToAir(p)))
			.register();

	public static final BlockEntry<ElementalWallBlock> FRIGID_WALL = REGISTRATE
			.block("ice_wall", ElementalWallBlock::new)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.properties(BlockBehaviour.Properties::noLootTable)
			.item(DevBlockItem::new)
			.build()
			.addLayer(() -> RenderType::translucent)
			.register();

	public static final BlockEntry<ElementalWallBlock> STABILITY_WALL = REGISTRATE
			.block("stability_wall", ElementalWallBlock::new)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.properties(BlockBehaviour.Properties::noLootTable)
			.item(DevBlockItem::new)
			.build()
			.addLayer(() -> RenderType::translucent)
			.register();


	public static final BlockEntry<InsigniaBlock> INSIGNIA = REGISTRATE
			.block("insignia", InsigniaBlock::new)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.properties(BlockBehaviour.Properties::noLootTable)
			.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
			.addLayer(() -> RenderType::cutoutMipped)
			.register();

	public static final BlockEntry<NullSpaceBlock> NULL_SPACE = REGISTRATE
			.block("null_space", NullSpaceBlock::new)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.properties(BlockBehaviour.Properties::noLootTable)
			.addLayer(() -> RenderType::translucent)
			.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
			.item(DevBlockItem::new)
			.build()
			.register();
	public static final BlockEntry<DistillerBlock> DISTILLER = REGISTRATE
			.block("distiller", DistillerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.STONE))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .transform(axeOrPickaxe())
            .item(AssemblyOperatorBlockItem::new)
            .transform(customItemModel())
            .addLayer(() -> RenderType::cutoutMipped)
            .register();


	/* * * * * * * * * * */
	/* F A C T O R I E S */
	/* * * * * * * * * * */

	private static BlockEntry<Block> simple(String name) {
		return REGISTRATE.block(name, Block::new)
				.simpleItem()
				.register();
	}
}
