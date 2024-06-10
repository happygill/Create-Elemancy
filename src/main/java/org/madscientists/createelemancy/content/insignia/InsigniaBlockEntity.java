package org.madscientists.createelemancy.content.insignia;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsigniaBlockEntity extends SmartBlockEntity {

    //Compound Tags Keys
    private static final String LINES_NBT_KEY = "insignia_lines";
    private static final String GUIDE_LINES_NBT_KEY = "insignia_guide_lines";
    private static final String CENTER_NBT_KEY = "insignia_center";
    public List<String> lineRenderingList = new ArrayList<>();
    public List<String> ghostLineRenderingList = new ArrayList<>();
    public List<String> guideLineRenderingList = new ArrayList<>();
    BlockPos center;
    Vec3 pointA;
    Vec3 ghostOffset;
    boolean ghost = false;
    int delay;
    boolean guide = false;
    boolean activated = false;
    InsigniaPattern pattern;
    UseOnContext context;

    public InsigniaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setGuide(boolean guide) {
        this.guide = guide;
    }

    public void clearPoint() {
        pointA = null;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        if (tag.contains(CENTER_NBT_KEY))
            center = NbtUtils.readBlockPos(tag.getCompound(CENTER_NBT_KEY));
        if (tag.contains(LINES_NBT_KEY))
            lineRenderingList = InsigniaUtils.loadLinesFromNBT(tag.getCompound(LINES_NBT_KEY));
        if (tag.contains(GUIDE_LINES_NBT_KEY))
            guideLineRenderingList = InsigniaUtils.loadLinesFromNBT(tag.getCompound(GUIDE_LINES_NBT_KEY));

        super.read(tag, clientPacket);

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        if (!lineRenderingList.isEmpty())
            tag.put(LINES_NBT_KEY, InsigniaUtils.saveLinesToNBT(lineRenderingList));
        if (!guideLineRenderingList.isEmpty())
            tag.put(GUIDE_LINES_NBT_KEY, InsigniaUtils.saveLinesToNBT(guideLineRenderingList));
        if (center != null)
            tag.put(CENTER_NBT_KEY, NbtUtils.writeBlockPos(center));
        super.write(tag, clientPacket);
    }

    public int chalkClick(UseOnContext pContext, ElemancyElement elemancyElement) {
        int startCount = lineRenderingList.size();
        if (activated) return 0;

        if (guide)
            addCode(InsigniaUtils.getLinesClicked(offset(pContext.getClickLocation()), isLarge()), false, elemancyElement);
        else {
            if (pointA == null) {
                pointA = pContext.getClickLocation();
                return 0;
            }

            if (isLarge() && InsigniaUtils.validPoints5x5(offset(pointA), offset(pContext.getClickLocation())))
                addCode(InsigniaUtils.getLineCodes5x5(offset(pointA), offset(pContext.getClickLocation())), false, elemancyElement);


            if (!isLarge() && InsigniaUtils.validPoints(offset(pointA), offset(pContext.getClickLocation())))
                addCode(InsigniaUtils.getLineCodes(offset(pointA), offset(pContext.getClickLocation())), false, elemancyElement);

        }
        clearPoint();
        pattern = InsigniaUtils.getRecipe(new InsigniaContext(pContext, this,true));
        if (pattern != null) {
            activated = true;
            this.context = pContext;
        }
        notifyUpdate();
        return lineRenderingList.size() - startCount;
    }

    public void chalkHover(Vec3 point, ElemancyElement elemancyElement) {

        if (activated) return;
        if (ghost) {
            processGhostCircle(point, elemancyElement);
            return;
        }
        ghostLineRenderingList.clear();

        if (guide)
            addCode(InsigniaUtils.getLinesClicked(offset(point), isLarge()), true, elemancyElement);

        if (pointA == null || guide) return;

        if (isLarge() && InsigniaUtils.validPoints5x5(offset(pointA), offset(point)))
            addCode(InsigniaUtils.getLineCodes5x5(offset(pointA), offset(point)), true, elemancyElement);

        if (!isLarge() && InsigniaUtils.validPoints(offset(pointA), offset(point)))
            addCode(InsigniaUtils.getLineCodes(offset(pointA), offset(point)), true, elemancyElement);

    }

    public boolean erase(Player pPlayer) {
        if (!pPlayer.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || isGhost() || !hasCenter()) return false;

        return getCenterInsignia().removeCode(InsigniaUtils.getLinesClicked(offset(pPlayer.pick(10.0D, 0.0F, false).getLocation()), isLarge()));
    }

    private boolean isLarge() {
        for (String code : lineRenderingList) {
            if (code.startsWith("CC"))
                return true;
        }
        for (String code : guideLineRenderingList) {
            if (code.startsWith("CC"))
                return true;
        }
        return false;
    }

    private boolean isMedium() {
        for (String code : lineRenderingList) {
            if (code.startsWith("CB"))
                return true;
        }
        for (String code : guideLineRenderingList) {
            if (code.startsWith("CB"))
                return true;
        }
        return false;
    }

    private Vec3 offset(Vec3 point) {
        return point.subtract(Vec3.atCenterOf(getBlockPos()));
    }

    private void processGhostCircle(Vec3 point, ElemancyElement elemancyElement) {
        BlockPos pointPos = new BlockPos((int) point.x(), (int) point.y(), (int) point.z());
        if (getBlockPos().equals(pointPos)) {
            addCode("CA", true, elemancyElement);
            ghostOffset = new Vec3(0, 0, 0);
        } else if (Math.abs(getBlockPos().getX() - Math.floor(point.x)) == 2 && Math.abs(getBlockPos().getZ() - Math.floor(point.z)) == 2) {
            addCode("CB", true, elemancyElement);
            ghostOffset = new Vec3((Math.floor(point.x) - getBlockPos().getX()) / 2, 0, (Math.floor(point.z) - getBlockPos().getZ()) / 2);
        } else if (Math.abs(getBlockPos().getX() - Math.floor(point.x)) == 4 && Math.abs(getBlockPos().getZ() - Math.floor(point.z)) == 4) {
            addCode("CC", true, elemancyElement);
            ghostOffset = new Vec3((Math.floor(point.x) - getBlockPos().getX()) / 2, 0, (Math.floor(point.z) - getBlockPos().getZ()) / 2);
        } else
            ghostLineRenderingList.clear();
    }
    InsigniaContext.Result result;
    @Override
    public void tick() {
        super.tick();
        if (activated) {
            if(pattern!=null&&context!=null&&(result==null||result== InsigniaContext.Result.CONTINUE))
               result= pattern.applyEffect(new InsigniaContext(context, this,result==null));
            if (delay % 15 == 0) {
                level.addParticle(ParticleTypes.FIREWORK, (double) getBlockPos().getX() + 0.5D, (double) getBlockPos().getY() + 0.25D, (double) getBlockPos().getZ() + 0.5D, .1D, 0.0D, 0.0D);
                level.addParticle(ParticleTypes.FIREWORK, (double) getBlockPos().getX() + 0.5D, (double) getBlockPos().getY() + 0.25D, (double) getBlockPos().getZ() + 0.5D, -.1D, 0.0D, 0.0D);
                level.addParticle(ParticleTypes.FIREWORK, (double) getBlockPos().getX() + 0.5D, (double) getBlockPos().getY() + 0.25D, (double) getBlockPos().getZ() + 0.5D, 0.0D, 0.0D, .1D);
                level.addParticle(ParticleTypes.FIREWORK, (double) getBlockPos().getX() + 0.5D, (double) getBlockPos().getY() + 0.25D, (double) getBlockPos().getZ() + 0.5D, 0.0D, 0.0D, -.1D);
            }
            delay++;
            if (delay > 50&&result== InsigniaContext.Result.FINISHED)
                level.setBlockAndUpdate(getCenter(), Blocks.AIR.defaultBlockState());
        }
    }


    public Vec3 getGhostOffset() {
        return ghostOffset;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    private void createLarge(BlockPos center) {
        for (int i = -2; i < 3; i++)
            for (int j = -2; j < 3; j++)
                addInsigniaPart(i, j, center);
    }

    private void createMedium(BlockPos center) {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                addInsigniaPart(i, j, center);
    }

    public void removeInsigniaPart(BlockPos center) {
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                if (center.offset(i, 0, j).equals(getBlockPos())) break;

                if (getLevel().getBlockEntity(center.offset(i, 0, j)) instanceof InsigniaBlockEntity block) {
                    if(block.hasCenter()&&block.getCenter().equals(center))
                        getLevel().setBlockAndUpdate(center.offset(i, 0, j), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private void addInsigniaPart(int xOffset, int yOffset, BlockPos center) {
        getLevel().setBlockAndUpdate(center.offset(xOffset, 0, yOffset), ElemancyBlocks.INSIGNIA.getDefaultState());
        if (getLevel().getBlockEntity(center.offset(xOffset, 0, yOffset)) instanceof InsigniaBlockEntity te) {
            te.setCenter(getCenter());
            te.setGuide(te.getCenterInsignia().guide);
        }
    }

    public void createInsigniaCircle(InsigniaSize size, ElemancyElement elemancyElement, BlockPos center) {
        setGhost(false);
        setCenter(center);
        if (size.equals(InsigniaSize.SMALL)) {
            getCenterInsignia().addCode("CA", false, elemancyElement);
        }
        if (size.equals(InsigniaSize.MEDIUM)) {
            createMedium(center);
            getCenterInsignia().addCode("CB", false, elemancyElement);
        }
        if (size.equals(InsigniaSize.LARGE)) {
            createLarge(center);
            getCenterInsignia().addCode("CC", false, elemancyElement);
        }
        ghostLineRenderingList.clear();

    }

    public void createFromGuide() {
        setGhost(false);
        setCenter(getBlockPos());
        setGuide(true);
        if (isMedium())
            createMedium(center);
        if (isLarge()) {
            createLarge(center);
        }
        ghostLineRenderingList.clear();

    }

    public BlockPos getCenter() {
        return center;
    }

    private void addCode(String code, boolean ghost, ElemancyElement elemancyElement) {
        String eCode = code + elemancyElement.getInsigniaMapping();
        if (ghost) {
            if (!ghostLineRenderingList.contains(eCode))
                ghostLineRenderingList.add(eCode);
            return;
        }
        ghostLineRenderingList.remove(eCode);
        if (!lineRenderingList.contains(eCode))
            lineRenderingList.add(eCode);
    }

    private void addCode(List<String> codes, boolean ghost, ElemancyElement elemancyElement) {
        for (String code : codes) {
            String eCode = code + elemancyElement.getInsigniaMapping();
            if (ghost) {
                if (!ghostLineRenderingList.contains(eCode))
                    ghostLineRenderingList.add(eCode);
            } else {
                ghostLineRenderingList.remove(eCode);
                guideLineRenderingList.remove(eCode);
                if (!lineRenderingList.contains(eCode))
                    lineRenderingList.add(eCode);
            }
        }

    }

    //fixme temp fix, insignia Drawing System refactor needed
    private static final String[] SMALL_INSIGNIA_CODES= {"DI","DJ","SY","SZ"};


    private void addCode(String[] codes, boolean ghost, ElemancyElement elemancyElement) {
        for (String code : codes) {
            String eCode = code + elemancyElement.getInsigniaMapping();

            //fixme temp fix, insignia Drawing System refactor needed
            if(!isLarge()&&!isMedium()){
                if(!Arrays.stream(SMALL_INSIGNIA_CODES).toList().contains(code))
                    break;
            }

            if (ghost) {
                if (!ghostLineRenderingList.contains(eCode) && !guide)
                    ghostLineRenderingList.add(eCode);
            } else {
                if (!containsCode(eCode) && (guideLineRenderingList.isEmpty() || guideLineRenderingList.contains(eCode)))
                    lineRenderingList.add(eCode);
                ghostLineRenderingList.remove(eCode);
                guideLineRenderingList.remove(eCode);
            }
        }

    }

    private boolean containsCode(String eCode) {
        for (String code : lineRenderingList)
            if (code.startsWith(eCode.substring(0, 2)))
                return true;
        return false;
    }

    private boolean removeCode(String[] codes) {
        int i = lineRenderingList.size();
        for (String code : codes) {
            lineRenderingList.removeIf(eCode -> eCode.startsWith(code.substring(0, 2)));
        }
        return i != lineRenderingList.size();
    }

    public boolean isCenter() {
        return hasCenter() && center.equals(getBlockPos());
    }

    public void setCenter(BlockPos center) {
        this.center = center;
    }

    public boolean hasCenter() {
        if (center == null) return false;
        return level.getBlockEntity(center) instanceof InsigniaBlockEntity;
    }

    public InsigniaBlockEntity getCenterInsignia() {
        return level.getBlockEntity(center) instanceof InsigniaBlockEntity insignia ? insignia : this;
    }

    public boolean isGhost() {
        return ghost;
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
    }

    public enum InsigniaSize {
        SMALL, MEDIUM, LARGE
    }

}
