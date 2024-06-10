package org.madscientists.createelemancy.content.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.madscientists.createelemancy.content.block.distiller.DistillerBlockEntity;
import org.madscientists.createelemancy.content.block.distiller.DistillerInstance;
import org.madscientists.createelemancy.content.block.distiller.DistillerRenderer;
import org.madscientists.createelemancy.content.block.elemental.ElementalBlockEntity;
import org.madscientists.createelemancy.content.block.elemental.ElementalWallRenderer;
import org.madscientists.createelemancy.content.block.grinder.GrindingWheelControllerBlockEntity;
import org.madscientists.createelemancy.content.block.grinder.GrindingWheelInstance;
import org.madscientists.createelemancy.content.block.grinder.GrindingWheelRenderer;
import org.madscientists.createelemancy.content.block.grinder.GrindingWheelBlockEntity;
import org.madscientists.createelemancy.content.nullspace.NullSpaceBlockEntity;
import org.madscientists.createelemancy.content.nullspace.NullSpaceRenderer;
import org.madscientists.createelemancy.content.block.vortex.*;
import org.madscientists.createelemancy.content.insignia.InsigniaBlockEntity;
import org.madscientists.createelemancy.content.insignia.InsigniaRenderer;

import static org.madscientists.createelemancy.Elemancy.LOGGER;
import static org.madscientists.createelemancy.Elemancy.registrate;


public class ElemancyBlockEntities {
    public static final BlockEntityEntry<VortexGeneratorBlockEntity> VORTEX_GENERATOR = registrate()
            .blockEntity("vortex_generator", VortexGeneratorBlockEntity::new)
            .validBlocks(ElemancyBlocks.VORTEX_GENERATOR)
            .register();


    public static final BlockEntityEntry<GrindingWheelBlockEntity> GRINDING_WHEEL = registrate()
            .blockEntity("grinding_wheel", GrindingWheelBlockEntity::new)
            .instance(() -> GrindingWheelInstance::new)
            .validBlocks(ElemancyBlocks.GRINDING_WHEEL)
            .renderer(() -> GrindingWheelRenderer::new)
            .register();
    public static final BlockEntityEntry<VCPBlockEntity> VCP = registrate()
            .blockEntity("vortex_control_point", VCPBlockEntity::new)
            .validBlocks(ElemancyBlocks.VCP, ElemancyBlocks.VCP_SMALL_COG, ElemancyBlocks.VCP_LARGE_COG)
            .renderer(() -> VCPRenderer::new)
            .register();
    public static final BlockEntityEntry<GrindingWheelControllerBlockEntity> GRINDING_WHEEL_CONTROLLER = registrate()
            .blockEntity("grinding_wheel_controller", GrindingWheelControllerBlockEntity::new)
            .validBlocks(ElemancyBlocks.GRINDING_WHEEL_CONTROLLER)
            .register();
    public static final BlockEntityEntry<InsigniaBlockEntity> INSIGNIA = registrate()
            .blockEntity("insignia", InsigniaBlockEntity::new)
            .validBlocks(ElemancyBlocks.INSIGNIA)
            .renderer(() -> InsigniaRenderer::new)
            .register();

    public static final BlockEntityEntry<NullSpaceBlockEntity> NULL_SPACE = registrate()
            .blockEntity("null_space", NullSpaceBlockEntity::new)
            .validBlocks(ElemancyBlocks.NULL_SPACE)
            .renderer(() -> NullSpaceRenderer::new)
            .register();
    public static final BlockEntityEntry<ElementalBlockEntity> ELEMENTAL_WALL = registrate()
            .blockEntity("elemental_wall", ElementalBlockEntity::new)
            .validBlocks(ElemancyBlocks.FRIGID_WALL,ElemancyBlocks.STABILITY_WALL,ElemancyBlocks.SCORCHED_FIRE)
            .renderer(() -> ElementalWallRenderer::new)
            .register();

    public static final BlockEntityEntry<DistillerBlockEntity> DISTILLER = registrate()
            .blockEntity("distiller", DistillerBlockEntity::new)
            .instance(() -> DistillerInstance::new)
            .validBlocks(ElemancyBlocks.DISTILLER)
            .renderer(() -> DistillerRenderer::new)
            .register();

    public static void init() {
        LOGGER.info("Registering Block Entities!");
    }



}
