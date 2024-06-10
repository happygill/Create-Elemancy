package org.madscientists.createelemancy.content.block.vortex;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;

public class VortexMultiHelper {

    public static void updateMultiBlock(VCPBlockEntity vcp) {
        boolean top = vcp.getBlockState().getValue(VCPBlock.TOP);
        Direction facing = top ? Direction.DOWN : Direction.UP;
        if(vcp.hasExisitingVortex()){
            if(vcp.isValidVortexBE()) {
                if(!isValidSize(vcp,facing)){
                    removeMultiBlock(vcp.getLevel(), vcp.getPrimaryPos());
                    tryFormMultiBlock(vcp, facing);
                }
                else
                    vcp.getVortex().setVCP(vcp.getBlockPos(), vcp.getBlockState().getValue(VCPBlock.TOP));
            }
            else {
                removeMultiBlock(vcp.getLevel(), vcp.getPrimaryPos());
                vcp.removePrimary();
            }
        }
        else {
            if(vcp.getLevel().getBlockEntity(vcp.getBlockPos().relative(facing)) instanceof VortexGeneratorBlockEntity vg&&vg.getPrimaryVortexGenerator()!=null){
                vcp.setPrimaryPos(vg.getPrimaryPos());
                vg.getPrimaryVortexGenerator().setVCP(vcp.getBlockPos(), vcp.getBlockState().getValue(VCPBlock.TOP));
                vcp.size = vg.getPrimaryVortexGenerator().size;
                vcp.height = vg.getPrimaryVortexGenerator().height;
            }
            else
                tryFormMultiBlock(vcp, facing);
        }


    }

    private static boolean isValidSize(VCPBlockEntity vcp, Direction facing) {
        int height = 0;
        int size = 0;
        boolean multiBlock = false;
        for (int i = 1; i < 10; i++) {
            BlockPos pos = vcp.getBlockPos().relative(facing, i);
            if (vcp.getLevel().getBlockEntity(pos) instanceof VortexGeneratorBlockEntity||vcp.getLevel().getBlockEntity(pos) instanceof FluidTankBlockEntity) {
                if (height == 0 && isMultiBlock(vcp.getLevel(), pos)) {
                    multiBlock = true;
                }
                if (multiBlock) {
                    if (isMultiBlock(vcp.getLevel(), pos)) {
                        size += 9;
                    } else
                        break;
                } else
                    size++;
                height++;
            } else
                break;
        }
        if (size > 0 && height > 0) {
            return size == vcp.size && height == vcp.height;
        }
        return false;
    }


    private static void tryFormMultiBlock(VCPBlockEntity vcp, Direction facing) {
        int height = 0;
        int size = 0;
        boolean multiBlock = false;
        for (int i = 1; i < 10; i++) {
            BlockPos pos = vcp.getBlockPos().relative(facing, i);
            if(vcp.getLevel().getBlockEntity(pos) instanceof FluidTankBlockEntity) {
                if(height==0&& isFluidMultiBlock(vcp.getLevel(), pos)){
                    multiBlock = true;
                }
                if(multiBlock){
                    if(isFluidMultiBlock(vcp.getLevel(), pos)){
                        size+=9;
                    } else
                        break;
                } else
                    size++;
                height++;
            }
            else
                break;
        }
        if(size>0&&height>0){
            vcp.size = size;
            vcp.height = height;
            formMultiBlock(vcp, height, size, facing);
        }
    }

    private static void formMultiBlock(VCPBlockEntity vcp, int height, int size, Direction facing) {
        BlockPos primaryPos = facing==Direction.UP? vcp.getBlockPos().relative(facing):vcp.getBlockPos().relative(facing, height);
        Level level = vcp.getLevel();
        if(isSingleBlock(height, size))
            formSingleBlock(level, primaryPos);
        else if(isSingleTower(height, size))
            formSingleTower(level, primaryPos, height);
        else if(isSingleMultiblock(height, size))
            formSingleMultiblock(level, primaryPos);
        else if(isMultiBlockTower(height, size))
            formMultiBlockTower(level, primaryPos, height);
        vcp.setPrimaryPos(primaryPos);
        if(vcp.isValidVortexBE()) {
            vcp.getVortex().makePrimary(size, height);
            vcp.getVortex().setVCP(vcp.getBlockPos(), vcp.getBlockState().getValue(VCPBlock.TOP));
            vcp.size = size;
            vcp.height = height;
        }
    }

    private static boolean isFluidMultiBlock(Level level, BlockPos pos) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if(!isFluidTank(level, pos.offset(i,0,j)))
                    return false;
            }
        }
        return true;
    }

    private static boolean isMultiBlock(Level level, BlockPos pos) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if(!isValidBlock(level, pos.offset(i,0,j)))
                    return false;
            }
        }
        return true;
    }
    private static void formSingleBlock(Level level, BlockPos primaryPos){
        setVGandUpdate(level, primaryPos, VortexGeneratorBlock.Shape.SINGLE, primaryPos);
    }

    private static void formSingleTower(Level level, BlockPos primaryPos, int height){
        for (int i = 0; i < height; i++) {
            if(i==0)
                setVGandUpdate(level, primaryPos.above(i), VortexGeneratorBlock.Shape.BOTTOM, primaryPos);
            else if(i==height-1)
                setVGandUpdate(level, primaryPos.above(i), VortexGeneratorBlock.Shape.TOP, primaryPos);
            else
                setVGandUpdate(level, primaryPos.above(i), VortexGeneratorBlock.Shape.MIDDLE, primaryPos);
        }
    }

    private static void formSingleMultiblock(Level level, BlockPos primaryPos){
        setVGandUpdate(level, primaryPos, VortexGeneratorBlock.Shape.SINGLE_CENTER, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.NORTH), VortexGeneratorBlock.Shape.SINGLE_MIDDLE_NORTH, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.SOUTH), VortexGeneratorBlock.Shape.SINGLE_MIDDLE_SOUTH, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.EAST), VortexGeneratorBlock.Shape.SINGLE_MIDDLE_EAST, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.WEST), VortexGeneratorBlock.Shape.SINGLE_MIDDLE_WEST, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.NORTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.SINGLE_CORNER_NORTH_EAST, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.NORTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.SINGLE_CORNER_NORTH_WEST, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.SOUTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.SINGLE_CORNER_SOUTH_EAST, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.SOUTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.SINGLE_CORNER_SOUTH_WEST, primaryPos);
    }


    private static void formMultiBlockTower(Level level, BlockPos primaryPos, int height){
        setVGandUpdate(level, primaryPos, VortexGeneratorBlock.Shape.MULTI_BOTTOM, primaryPos);
        setVGandUpdate(level, primaryPos.above(height-1), VortexGeneratorBlock.Shape.MULTI_TOP, primaryPos);
        for (int i = 1; i < height-1; i++) {
            setVGandUpdate(level, primaryPos.above(i), VortexGeneratorBlock.Shape.MULTI_MIDDLE, primaryPos);
        }
        setVGandUpdate(level, primaryPos.relative(Direction.NORTH), VortexGeneratorBlock.Shape.MULTI_NORTH_BOTTOM, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.SOUTH), VortexGeneratorBlock.Shape.MULTI_SOUTH_BOTTOM, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_EAST_BOTTOM, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_WEST_BOTTOM, primaryPos);

        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.NORTH), VortexGeneratorBlock.Shape.MULTI_NORTH_TOP, primaryPos);
        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.SOUTH), VortexGeneratorBlock.Shape.MULTI_SOUTH_TOP, primaryPos);
        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_EAST_TOP, primaryPos);
        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_WEST_TOP, primaryPos);

        for (int i = 1; i < height-1; i++) {
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.NORTH), VortexGeneratorBlock.Shape.MULTI_NORTH_MIDDLE, primaryPos);
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.SOUTH), VortexGeneratorBlock.Shape.MULTI_SOUTH_MIDDLE, primaryPos);
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_EAST_MIDDLE, primaryPos);
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_WEST_MIDDLE, primaryPos);
        }

        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.NORTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_NORTH_EAST_TOP, primaryPos);
        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.NORTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_NORTH_WEST_TOP, primaryPos);
        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.SOUTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_SOUTH_EAST_TOP, primaryPos);
        setVGandUpdate(level, primaryPos.above(height-1).relative(Direction.SOUTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_SOUTH_WEST_TOP, primaryPos);

        setVGandUpdate(level, primaryPos.relative(Direction.NORTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_NORTH_EAST_BOTTOM, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.NORTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_NORTH_WEST_BOTTOM, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.SOUTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_SOUTH_EAST_BOTTOM, primaryPos);
        setVGandUpdate(level, primaryPos.relative(Direction.SOUTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_SOUTH_WEST_BOTTOM, primaryPos);

        for (int i = 1; i < height -1; i++) {
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.NORTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_NORTH_EAST_MIDDLE, primaryPos);
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.NORTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_NORTH_WEST_MIDDLE, primaryPos);
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.SOUTH).relative(Direction.EAST), VortexGeneratorBlock.Shape.MULTI_SOUTH_EAST_MIDDLE, primaryPos);
            setVGandUpdate(level, primaryPos.above(i).relative(Direction.SOUTH).relative(Direction.WEST), VortexGeneratorBlock.Shape.MULTI_SOUTH_WEST_MIDDLE, primaryPos);
        }

    }

    private static void setVGandUpdate(Level level, BlockPos pos, VortexGeneratorBlock.Shape shape, BlockPos primaryPos){
        level.setBlockAndUpdate(pos, ElemancyBlocks.VORTEX_GENERATOR.get().defaultBlockState().setValue(VortexGeneratorBlock.SHAPE, shape));
        VortexGeneratorBlockEntity vgb = (VortexGeneratorBlockEntity) level.getBlockEntity(pos);
        vgb.setPrimary(primaryPos);
    }

    private static boolean isFluidTank(Level level, BlockPos pos){
        return level.getBlockEntity(pos) instanceof FluidTankBlockEntity;
    }

    private static boolean isValidBlock(Level level, BlockPos pos){
        return level.getBlockEntity(pos) instanceof FluidTankBlockEntity||level.getBlockEntity(pos) instanceof VortexGeneratorBlockEntity;
    }

    private static boolean isSingleTower(int height, int size){
        return height==size&&height>1;
    }
    private static boolean isSingleBlock(int height, int size){
        return height==1&&size==1;
    }

    private static boolean isSingleMultiblock(int height, int size){
        return size>1&&height==1;
    }

    private static boolean isMultiBlockTower(int height, int size){
        return size>1&&height>1;
    }


    public static void removeMultiBlock(Level level, BlockPos primaryPos) {
        for (int y = 0; y < 10; y++) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos pos = primaryPos.offset(x, y, z);
                    if (level.getBlockEntity(pos) instanceof VortexGeneratorBlockEntity vg) {
                        if(vg.getPrimaryPos().equals(primaryPos))
                            level.setBlockAndUpdate(pos, AllBlocks.FLUID_TANK.getDefaultState());
                    }
                }
            }
        }

    }


    public static boolean needsTurbine(VortexGeneratorBlockEntity blockEntity) {
        return blockEntity.getPrimaryVortexGenerator() != null
                && blockEntity.getPrimaryVortexGenerator().getBlockPos().getX() == blockEntity.getBlockPos().getX()
                && blockEntity.getPrimaryVortexGenerator().getBlockPos().getZ() == blockEntity.getBlockPos().getZ();
    }

    public static float getTurbineOffset(VortexGeneratorBlockEntity blockEntity) {
        if (blockEntity.getPrimaryVortexGenerator() == null)
            return 0;
        if(blockEntity.isPrimary())
            return .1f;
        if(!blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().above()).is(ElemancyBlocks.VORTEX_GENERATOR.get()))
            return -.1f;
        return 0;
    }

}
