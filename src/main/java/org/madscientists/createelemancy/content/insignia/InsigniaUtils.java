package org.madscientists.createelemancy.content.insignia;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.madscientists.createelemancy.content.item.InsigniaGuideItem.INSIGNIA_NBT_KEY;

public class InsigniaUtils {

    public static void init() {
        drawInit();
        renderInit();
        InsigniaEffect.init();
    }


    //Pattern Utils

    public static final List<InsigniaPattern> INSIGNIA_PATTERNS = new ArrayList<>();

    public static InsigniaPattern getRecipe(InsigniaContext pContext) {
        for (InsigniaPattern insigniaPattern : INSIGNIA_PATTERNS) {
            assert pContext.getInsignia() != null;
            if (insigniaPattern.matches(pContext.getInsignia().lineRenderingList)) {
                return insigniaPattern;
            }
        }
        return null;
    }


    //Drawing Utils
    static final String INSIGNIA_SIZE_KEY = "insignia_size";
    public static final String INSIGNIA_NAME_KEY = "insignia_name";

    public static final String INSIGNIA_DESCRIPTION_KEY = "insignia_description";

    static final String INSIGNIA_KEY = "insignia_line_";

    static final double TOLERANCE = .15;


    private record DrawingPoint(String point, double x, double z) {
    }



    public static final List<DrawingPoint> drawingPoints = new ArrayList<>();

    //TODO unify 5x5 system with 3x3
    public static final List<DrawingPoint> drawingPoints5x5 = new ArrayList<>();

    private record Line(String point1, String point2, String[] lineCodes) {
    }

    public static final List<Line> lines = new ArrayList<>();


    private static void drawInit() {
        initDrawingPoints();
        initLines();

    }

    public static String getDrawingPoint(Vec3 point) {
        double tolerance = 0;
        int index = 0;
        for (int i = 0; i < drawingPoints.size(); i++) {
            if (tolerance == 0 || Math.abs(drawingPoints.get(i).x - point.x) + Math.abs(drawingPoints.get(i).z - point.z) < tolerance) {
                tolerance = Math.abs(drawingPoints.get(i).x - point.x) + Math.abs(drawingPoints.get(i).z - point.z);
                index = i;
            }
        }
        return drawingPoints.get(index).point;
    }

    public static String getMatchingDrawingPoint(Vec3 point, String match) {
        double tolerance = 0;
        int index = 0;
        for (int i = 0; i < drawingPoints.size(); i++) {
            if (tolerance == 0 || Math.abs(drawingPoints.get(i).x - point.x) + Math.abs(drawingPoints.get(i).z - point.z) < tolerance) {
                if (validLine(drawingPoints.get(i).point, match)) {
                    tolerance = Math.abs(drawingPoints.get(i).x - point.x) + Math.abs(drawingPoints.get(i).z - point.z);
                    index = i;
                }
            }
        }
        return drawingPoints.get(index).point;
    }

    public static boolean validPoints(Vec3 pointA, Vec3 pointB) {
        String point1 = getDrawingPoint(pointA);
        String point2 = getMatchingDrawingPoint(pointB, point1);
        return point1 != null && point2 != null;
    }

    public static boolean validPoints5x5(Vec3 pointA, Vec3 pointB) {
        String point1 = getDrawingPoint5x5(pointA);
        String point2 = getMatchingDrawingPoint5x5(pointB, point1);
        return point1 != null && point2 != null;
    }

    public static String getDrawingPoint5x5(Vec3 point) {
        double tolerance = 0;
        int index = 0;
        for (int i = 0; i < drawingPoints5x5.size(); i++) {
            if (tolerance == 0 || Math.abs(drawingPoints5x5.get(i).x - point.x) + Math.abs(drawingPoints5x5.get(i).z - point.z) < tolerance) {
                tolerance = Math.abs(drawingPoints5x5.get(i).x - point.x) + Math.abs(drawingPoints5x5.get(i).z - point.z);
                index = i;
            }
        }
        return drawingPoints5x5.get(index).point;
    }

    public static String getMatchingDrawingPoint5x5(Vec3 point, String match) {
        double tolerance = 0;
        int index = 0;
        for (int i = 0; i < drawingPoints5x5.size(); i++) {
            if (tolerance == 0 || Math.abs(drawingPoints5x5.get(i).x - point.x) + Math.abs(drawingPoints5x5.get(i).z - point.z) < tolerance) {
                if (validLine(drawingPoints5x5.get(i).point, match)) {
                    tolerance = Math.abs(drawingPoints5x5.get(i).x - point.x) + Math.abs(drawingPoints5x5.get(i).z - point.z);
                    index = i;
                }
            }
        }
        return drawingPoints5x5.get(index).point;
    }

    public static boolean validLine(String pointA, String pointB) {
        for (Line line : lines) {
            if (line.point1.equals(pointA) || line.point1.equals(pointB)) {
                if (line.point2.equals(pointA) || line.point2.equals(pointB)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String[] getLineCodes(Vec3 pointA, Vec3 pointB) {
        String point1 = getDrawingPoint(pointA);
        String point2 = getMatchingDrawingPoint(pointB, point1);
        for (Line line : lines) {
            if (line.point1.equals(point1) || line.point1.equals(point2)) {
                if (line.point2.equals(point1) || line.point2.equals(point2)) {
                    return line.lineCodes;
                }
            }
        }
        return new String[]{};
    }

    public static String[] getLineCodes5x5(Vec3 pointA, Vec3 pointB) {
        String point1 = getDrawingPoint5x5(pointA);
        String point2 = getMatchingDrawingPoint5x5(pointB, point1);
        for (Line line : lines) {
            if (line.point1.equals(point1) || line.point1.equals(point2)) {
                if (line.point2.equals(point1) || line.point2.equals(point2)) {
                    return line.lineCodes;
                }
            }
        }
        return new String[]{};
    }


    public static String[] getLinesClicked(Vec3 pointC, boolean isLarge) {
        for (Line line : lines) {
            if (inLine(getPoint(line.point1, isLarge), getPoint(line.point2, isLarge), pointC)) {
                System.out.println(line.lineCodes[0]);
                return line.lineCodes;
            }
        }
        return new String[]{};
    }


    ///TODO!!! better algorithm needed to identify lines based on clicked point.
    private static boolean inLine(DrawingPoint a, DrawingPoint b, Vec3 c) {
        if (a == null || b == null || c == null) return false;
        if (c.x < Math.min(a.x, b.x) || c.x > Math.max(a.x, b.x)) return false;
        if (c.z < Math.min(a.z, b.z) || c.z > Math.max(a.z, b.z)) return false;
        System.out.println(a.x * (b.z - c.z) + b.x * (c.z - a.z) + c.x * (a.z - b.z));
        return a.x * (b.z - c.z) + b.x * (c.z - a.z) + c.x * (a.z - b.z) < .05;
    }

    private static DrawingPoint getPoint(String pointCode, boolean isLarge) {
        if (isLarge)
            for (DrawingPoint point:drawingPoints5x5) {
                if(point.point.equals(pointCode))
                    return point;
            }
        else
            for (DrawingPoint point:drawingPoints) {
                if(point.point.equals(pointCode))
                    return point;
            }
        return null;
    }

    private static void initLines(){
        addLine("Q","U","SY");
        addLine("Q","5","SY","SS");

        addLine("X","T","DJ");
        addLine("X","4","DJ","DF");

        addLine("R","V","DI");
        addLine("R","6","DI","DG");


        addLine("W","S","SZ");
        addLine("W","3","SZ","SP");


        addLine("1","Q","SM");
        addLine("1","U","SM","SY");
        addLine("1","5","SM","SY","SS");

        addLine("5","U","SS");

        addLine("7","W","SV");
        addLine("7","S","SV","SZ");
        addLine("7","3","SV","SZ","SP");

        addLine("3","S","SP");


        addLine("8","0","SX");
        addLine("8","2","SN","SX");
        addLine("8","X","DH");
        addLine("8","T","DH","DJ");
        addLine("8","4","DH","DJ","DF");
        addLine("8","Z","SW");
        addLine("8","6","SW","SU");

        addLine("4","Y","SR");
        addLine("4","T","DF");
        addLine("4","9","SQ");
        addLine("4","6","SR","ST");
        addLine("6","V","DG");
        addLine("6","Y","ST");
        addLine("6","Z","SU");

        addLine("2","0","SN");
        addLine("2","R","DE");
        addLine("2","V","DE","DI");
        addLine("2","6","DE","DI","DG");
        addLine("2","9","SO");
        addLine("2","4","SO","SQ");

        addLine("2","4","SO","SQ");


        //5x5 insignia
        addLine("10","10","CA");

        addLine("A","E","SA","SY","SG");
        addLine("A","Q","SA");
        addLine("A","U","SA","SY");
        addLine("A","N","QA");
        addLine("A","P","QP");
        addLine("A","F","QP","QK");
        addLine("A","D","QF","QA");
        addLine("B","I","SB");
        addLine("B","H","SB","SL");
        addLine("B","R","DA");
        addLine("B","V","DA","DI");
        addLine("B","F","DA","DI","DC");
        addLine("B","J","SC");
        addLine("B","D","SC","SE");
        addLine("B","M","QB");
        addLine("B","G","QB","QM");
        addLine("B","N","QC");
        addLine("B","E","QC","QH");
        addLine("C","S","SD");
        addLine("C","W","SD","SZ");
        addLine("C","G","SD","SZ","SJ");
        addLine("C","M","QD");
        addLine("C","H","QD","QO");
        addLine("C","O","QE");
        addLine("C","F","QE","QJ");
        addLine("D","N","QF");
        addLine("D","J","SE");
        addLine("D","T","DB");
        addLine("D","X","DB","DJ");
        addLine("D","H","DB","DJ","DD");
        addLine("D","O","QG");
        addLine("D","G","QG","QL");
        addLine("D","K","SF");
        addLine("D","F","SF","SH");
        addLine("E","N","QH");
        addLine("E","U","SG");
        addLine("E","Q","SG","SY");
        addLine("E","P","QI");
        addLine("E","H","QI","QN");
        addLine("F","K","SH");
        addLine("F","O","QJ");
        addLine("F","V","DC");
        addLine("F","R","DC","DI");
        addLine("F","P","QK");
        addLine("F","L","SI");
        addLine("F","H","SI","SK");
        addLine("G","M","QM");
        addLine("G","W","SJ");
        addLine("G","S","SJ","SZ");
        addLine("G","O","QL");
        addLine("H","I","SL");
        addLine("H","M","QO");
        addLine("H","X","DD");
        addLine("H","T","DD","DJ");
        addLine("H","P","QN");
        addLine("H","L","SK");


    }


    private static void initDrawingPoints(){

        addDrawingPoint("1",0.0,-1.45);
        addDrawingPoint("3",1.45,0.0);
        addDrawingPoint("5",0.0,1.45);
        addDrawingPoint("7",-1.45,0.0);


        addDrawingPoint("0",0.0,-1.0);
        addDrawingPoint("9",1.0,0.0);
        addDrawingPoint("Y",0.0,1.0);
        addDrawingPoint("Z",-1.0,0.0);


        addDrawingPoint("Q",0.0,-.45);
        addDrawingPoint("S",.45,0.0);
        addDrawingPoint("U",0.0,.45);
        addDrawingPoint("W",-.45,0.0);


        addDrawingPoint("X",-.3,-.3);
        addDrawingPoint("R",.3,-.3);
        addDrawingPoint("T",.3,.3);
        addDrawingPoint("V",-.3,.3);


        addDrawingPoint("8",-1.0,-1.0);
        addDrawingPoint("4",1.0,1.0);
        addDrawingPoint("2",1.0,-1.0);
        addDrawingPoint("6",-1.0,1.0);

        addDrawingPoint5x5("G",-2.5,0);
        addDrawingPoint5x5("A",0,-2.5);
        addDrawingPoint5x5("C",2.5,0);
        addDrawingPoint5x5("E",0,2.5);

        addDrawingPoint5x5("H",-1.6,-1.6);
        addDrawingPoint5x5("B",1.6,-1.6);
        addDrawingPoint5x5("D",1.6,1.6);
        addDrawingPoint5x5("F",-1.6,1.6);

        addDrawingPoint5x5("L",-1.6,0);
        addDrawingPoint5x5("I",0,-1.6);
        addDrawingPoint5x5("J",1.6,0);
        addDrawingPoint5x5("K",0,1.6);

        addDrawingPoint5x5("P",-1,0);
        addDrawingPoint5x5("M",0,-1);
        addDrawingPoint5x5("N",1,0);
        addDrawingPoint5x5("O",0,1);


        addDrawingPoint5x5("10",0,0);


        addDrawingPoint5x5("Q",0.0,-.45);
        addDrawingPoint5x5("S",.45,0.0);
        addDrawingPoint5x5("U",0.0,.45);
        addDrawingPoint5x5("W",-.45,0.0);


        addDrawingPoint5x5("X",-.3,-.3);
        addDrawingPoint5x5("R",.3,-.3);
        addDrawingPoint5x5("T",.3,.3);
        addDrawingPoint5x5("V",-.3,.3);


    }




    public static CompoundTag saveLinesToNBT(String name, String[] lines) {
        CompoundTag newTag=new CompoundTag();
        newTag.putInt(INSIGNIA_SIZE_KEY,lines.length);
        newTag.putString(INSIGNIA_NAME_KEY,name);

        for (int i = 0; i < lines.length; i++) {
            newTag.putString(INSIGNIA_KEY+i,lines[i]);
        }
        return newTag;
    }
    public static CompoundTag saveLinesToNBT(String name,List<String> lines) {
        CompoundTag newTag = new CompoundTag();
        newTag.putInt(INSIGNIA_SIZE_KEY, lines.size());
        newTag.putString(INSIGNIA_NAME_KEY, name);

        for (int i = 0; i < lines.size(); i++) {
            newTag.putString(INSIGNIA_KEY + i, lines.get(i));
        }
        return newTag;
    }

    public static CompoundTag savePatternToNBT(InsigniaPattern pattern) {
        CompoundTag newTag = new CompoundTag();
        List<String> lines = pattern.getCodes();
        newTag.putInt(INSIGNIA_SIZE_KEY, lines.size());
        newTag.putString(INSIGNIA_NAME_KEY, pattern.getName());
        newTag.putString(INSIGNIA_DESCRIPTION_KEY, pattern.getDescription());

        for (int i = 0; i < lines.size(); i++) {
            newTag.putString(INSIGNIA_KEY + i, lines.get(i));
        }
        return newTag;
    }

    public static CompoundTag saveLinesToNBT(List<String> lines) {
        CompoundTag newTag = new CompoundTag();
        newTag.putInt(INSIGNIA_SIZE_KEY, lines.size());

        for (int i = 0; i < lines.size(); i++) {
            newTag.putString(INSIGNIA_KEY + i, lines.get(i));
        }
        return newTag;
    }

    public static String loadDescriptionFromNBT(CompoundTag tag) {
        List<String> lineRenderingList = new ArrayList<>();
        if (!tag.contains(INSIGNIA_DESCRIPTION_KEY)) return "";
        return tag.getString(INSIGNIA_DESCRIPTION_KEY);
    }

    public static List<String> loadLinesFromNBT(CompoundTag tag) {
        List<String> lineRenderingList = new ArrayList<>();
        if (!tag.contains(INSIGNIA_SIZE_KEY)) return lineRenderingList;

        int size = tag.getInt(INSIGNIA_SIZE_KEY);
        for (int i = 0; i < size; i++) {
            lineRenderingList.add(tag.getString(INSIGNIA_KEY + i));
        }
        return lineRenderingList;
    }


    private static void addDrawingPoint(String point, double x, double z)
    {
        drawingPoints.add(new DrawingPoint(point,x,z));
    }
    private static void addDrawingPoint5x5(String point, double x, double z)
    {
        drawingPoints5x5.add(new DrawingPoint(point,x,z));
    }

    private static void addLine(String point1, String point2,  String... lineCodes)
    {
        lines.add(new Line(point1,point2,lineCodes));
    }



    //Rendering Utils

    //Partial Models
    private static PartialModel insigniaModel(String path) {
        return new PartialModel(Elemancy.asResource("block/insignia/" + path));
    }

    public static boolean validCode(String code){
        return lineMappings.containsKey(code.substring(0,2));
    }
    public static final List<String> textureList= Arrays.asList("c1","c2","l1","l2","l3","l4","l5","l6");

    //rendering 5x5 as obj TODO unify system
    public static final List<String> textureList5x5= Arrays.asList("c3","l7","l8","l9","l10","l11","l12","l13","l14");

    public static final HashMap<String,String> lineMappings = new HashMap<>();
    private static final HashMap<String,PartialModel> insigniaModels = new HashMap<>();

    private static void renderInit(){

        lineMappings.put("CA","c1");
        lineMappings.put("DJ","l1");
        lineMappings.put("DI","l1");
        lineMappings.put("SY","l2");
        lineMappings.put("SZ","l2");

        lineMappings.put("CB","c2");
        lineMappings.put("DE","l3");
        lineMappings.put("DF","l3");
        lineMappings.put("DG","l3");
        lineMappings.put("DH","l3");

        lineMappings.put("SV","l4");
        lineMappings.put("SM","l4");
        lineMappings.put("SP","l4");
        lineMappings.put("SS","l4");
        lineMappings.put("SU","l5");
        lineMappings.put("SX","l5");
        lineMappings.put("SO","l5");
        lineMappings.put("SR","l5");
        lineMappings.put("SW","l6");
        lineMappings.put("ST","l6");
        lineMappings.put("SN","l6");
        lineMappings.put("SQ","l6");

        lineMappings.put("CC","c3");

        lineMappings.put("SF","l7");
        lineMappings.put("SI","l7");
        lineMappings.put("SL","l7");
        lineMappings.put("SC","l7");


        lineMappings.put("SA","l8");
        lineMappings.put("SD","l8");
        lineMappings.put("SG","l8");
        lineMappings.put("SJ","l8");

        lineMappings.put("SH","l13");
        lineMappings.put("SK","l13");
        lineMappings.put("SB","l13");
        lineMappings.put("SE","l13");


        lineMappings.put("QH","l9");
        lineMappings.put("QL","l9");
        lineMappings.put("QP","l9");
        lineMappings.put("QD","l9");

        lineMappings.put("QI","l12");
        lineMappings.put("QM","l12");
        lineMappings.put("QA","l12");
        lineMappings.put("QE","l12");

        lineMappings.put("QG","l10");
        lineMappings.put("QK","l10");
        lineMappings.put("QO","l10");
        lineMappings.put("QC","l10");

        lineMappings.put("QJ","l14");
        lineMappings.put("QN","l14");
        lineMappings.put("QB","l14");
        lineMappings.put("QF","l14");

        lineMappings.put("DB","l11");
        lineMappings.put("DC","l11");
        lineMappings.put("DD","l11");
        lineMappings.put("DA","l11");



        for (String texture : textureList) {
            //TODO pending textures
            insigniaModels.put(texture+ ElemancyElement.NULL.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.NULL.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.DENSITY.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.DENSITY.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.REACTIVITY.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.REACTIVITY.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.POWER.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.POWER.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.HEAT.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.HEAT.getInsigniaMapping()));

        }


        for (String texture : textureList5x5) {
            //TODO pending textures
            insigniaModels.put(texture+ ElemancyElement.NULL.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.NULL.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.DENSITY.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.DENSITY.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.REACTIVITY.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.REACTIVITY.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.POWER.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.POWER.getInsigniaMapping()));
            insigniaModels.put(texture+ ElemancyElement.HEAT.getInsigniaMapping(), insigniaModel(texture + ElemancyElement.HEAT.getInsigniaMapping()));

        }
    }

    public static PartialModel getModel(String code) {

        //TODO temp textures
        String lineMapping = code.substring(0, 2);
        int element = Integer.parseInt(code.substring(2));
        if (element != 5 && element != 7 && element != 8&&element!=3&&element!=1)
            element = 8;
        return insigniaModels.get(lineMappings.get(lineMapping) + element);
    }

    public static boolean isComplexInsignia(ItemStack insigniaItem) {
        CompoundTag insigniaGuideTag = insigniaItem.getOrCreateTag();
        if (insigniaGuideTag.contains(INSIGNIA_NBT_KEY)) {
            List<String> codes = InsigniaUtils.loadLinesFromNBT(insigniaGuideTag.getCompound(INSIGNIA_NBT_KEY));
            String element = codes.get(0).substring(2);
            for (String code : codes) {
                if (!code.substring(2).equals(element))
                    return true;
            }
            return false;
        }
        return false;
    }

    public static InsigniaPattern getPattern(ItemStack insigniaItem) {
        CompoundTag insigniaGuideTag = insigniaItem.getOrCreateTag();
        if (insigniaGuideTag.contains(INSIGNIA_NBT_KEY)) {
            for (int i = 0; i < InsigniaUtils.INSIGNIA_PATTERNS.size(); i++) {
                if (insigniaGuideTag.contains(INSIGNIA_NBT_KEY) &&
                        InsigniaUtils.INSIGNIA_PATTERNS.get(i).getName().equals(insigniaGuideTag.getCompound(INSIGNIA_NBT_KEY).getString(INSIGNIA_NAME_KEY))) {
                    return InsigniaUtils.INSIGNIA_PATTERNS.get(i);
                }
            }
        }
        return null;
    }


    //SuperByte Transformations
    public static SuperByteBuffer getSuperByte(String code, BlockState blockState) {
        return transformByte(code, CachedBufferer.partial(getModel(code), blockState));
    }

    private static SuperByteBuffer transformByte(String code, SuperByteBuffer byteBuffer) {
        byteBuffer.translate(0, .01, 0);

        if (textureList5x5.contains(lineMappings.get(code.substring(0, 2))))
            byteBuffer.translate(0.5, 0, 0.5);


        //l1
        if(code.startsWith("DI")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI /2);
        }

        //l2
        if(code.startsWith("SY")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI /2);
        }

        //l3
        if(code.startsWith("DH")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI /2);
        }
        if(code.startsWith("DF")){
            byteBuffer.rotateCentered(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("DG")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI);
        }

        //l4
        if(code.startsWith("SM")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI /2);
        }
        if(code.startsWith("SS")){
            byteBuffer.rotateCentered(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("SV")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI);
        }

        //l5
        if(code.startsWith("SO")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI /2);
        }
        if(code.startsWith("SU")){
            byteBuffer.rotateCentered(Direction.UP, -Mth.PI /2);

        }
        if(code.startsWith("SX")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI);
        }
        //l6
        if(code.startsWith("SQ")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("SW")){
            byteBuffer.rotateCentered(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("SN")){
            byteBuffer.rotateCentered(Direction.UP, Mth.PI);
        }

        //l7
        if(code.startsWith("SC")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("SI")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("SL")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }

        //l8
        if(code.startsWith("SD")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("SJ")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("SA")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }

        //l9
        if(code.startsWith("QD")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("QL")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("QP")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }

        //l12
        if(code.startsWith("QE")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("QM")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("QA")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }

        //l13
        if(code.startsWith("SE")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("SK")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("SB")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }

        //l10
        if(code.startsWith("QC")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("QK")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("QO")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }

        //l14
        if(code.startsWith("QF")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("QN")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("QB")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }

        //l11
        if(code.startsWith("DA")){
            byteBuffer.rotate(Direction.UP, Mth.PI /2);

        }
        if(code.startsWith("DC")){
            byteBuffer.rotate(Direction.UP, -Mth.PI /2);
        }
        if(code.startsWith("DD")){
            byteBuffer.rotate(Direction.UP, Mth.PI);
        }
        return byteBuffer;
    }
}
