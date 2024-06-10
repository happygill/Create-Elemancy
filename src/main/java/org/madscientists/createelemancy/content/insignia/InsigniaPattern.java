package org.madscientists.createelemancy.content.insignia;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.network.NetworkEvent;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.ability.enchant.TempEnchantmentData;
import org.madscientists.createelemancy.foundation.network.ElemancyMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class InsigniaPattern {

    public String getName() {
        return name;
    }

    public List<String> getCodes() {
        return codes;
    }

    private String name;

    private List<String> codes = new ArrayList<>();

    private String description;

    public String getDescription() {
        return description;
    }
    private Function<InsigniaContext,InsigniaContext.Result> effect= insigniaContext -> InsigniaContext.Result.FINISHED;

    public boolean matches(List<String> drawnLines) {
        if (codes.size() > 1 && drawnLines.size() > 1) {
            return codes.stream().sorted().toList().equals(drawnLines.stream().sorted().toList());
        }
      return false;
    }

    public InsigniaPattern(String name, String description, List<String> pattern, Function<InsigniaContext,InsigniaContext.Result> effect){
        this.name = name;
        this.description = description;
        this.codes = pattern;
        this.effect = effect;
    }

    boolean valid(){
        return name!=null&& codes.size()>1;
    }

    public InsigniaContext.Result applyEffect(InsigniaContext pContext) {
        if(effect!=null)
            return effect.apply(pContext);
        return InsigniaContext.Result.FINISHED;
    }


    protected InsigniaPattern(){}
    public static InsigniaPattern fromJson(JsonObject object) {
        InsigniaPattern pattern = new InsigniaPattern();
        try {
            JsonElement pattern_code = object.get("pattern");
            if (pattern_code != null && pattern_code.isJsonArray()){
                for (JsonElement element : pattern_code.getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        JsonPrimitive primitive = element.getAsJsonPrimitive();
                        if (primitive.isString()) {
                            String code=primitive.getAsString();
                            if(InsigniaUtils.validCode(code))
                                pattern.codes.add(code);
                        }
                    }
                }
            }


            parseJsonPrimitive(object, "name", JsonPrimitive::isString, primitive -> pattern.name=primitive.getAsString());
            parseJsonPrimitive(object, "description", JsonPrimitive::isString, primitive -> pattern.description=primitive.getAsString());

            JsonElement result = object.get("result");
            if (result != null && result.isJsonArray())
                pattern.effect= effectFromJson(result,pattern);
        } catch (Exception e) {
            Elemancy.LOGGER.info("Insignia JSON Error");
        }
        return pattern;
    }


    //TODO code refactor
    private static Function<InsigniaContext,InsigniaContext.Result> effectFromJson(JsonElement result, InsigniaPattern pattern){
        for (JsonElement element : result.getAsJsonArray()) {
           if(element.getAsJsonObject().get("type").getAsString().equals("summon"))
               return InsigniaEffect.getSummonEffect(element.getAsJsonObject().get("name").getAsString(), element.getAsJsonObject().get("count").getAsInt());
            if(element.getAsJsonObject().get("type").getAsString().equals("potion effect"))
                return InsigniaEffect.getPotionEffect(element.getAsJsonObject().get("name").getAsString(),element.getAsJsonObject().get("seconds").getAsInt(),element.getAsJsonObject().get("amp").getAsInt());
            if(element.getAsJsonObject().get("type").getAsString().equals("builtin"))
                return InsigniaEffect.getEffect(element.getAsJsonObject().get("key").getAsString());
            if(element.getAsJsonObject().get("type").getAsString().equals("temp enchant"))
                return InsigniaEffect.getTempEnchantEffect(TempEnchantmentData.fromJson(pattern.getName(),element.getAsJsonObject()),element.getAsJsonObject().get("seconds").getAsInt());
        }
        return ((useOnContext -> InsigniaContext.Result.FINISHED));
    }

    public static void syncTo(ServerPlayer player) {
       ElemancyMessages.sendToPlayer(new SyncPacket(),player);
    }

    public static void syncToAll() {
        ElemancyMessages.sendToClients(new SyncPacket());
    }


    public static class ReloadListener extends SimpleJsonResourceReloadListener {

        private static final Gson GSON = new Gson();
        public static final ReloadListener INSTANCE = new ReloadListener();
        protected ReloadListener() {
            super(GSON, "insignia_patterns");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {

            for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
                JsonElement element = entry.getValue();
                if (element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    InsigniaPattern pattern= fromJson(object);
                    if(pattern.valid())
                        InsigniaUtils.INSIGNIA_PATTERNS.add(pattern);
                }
            }

        }

    }

    private static void parseJsonPrimitive(JsonObject object, String key, Predicate<JsonPrimitive> predicate, Consumer<JsonPrimitive> consumer) {
        JsonElement element = object.get(key);
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (predicate.test(primitive)) {
                consumer.accept(primitive);
            }
        }
    }



    public static void toBuffer(FriendlyByteBuf buffer) {
        buffer.writeVarInt(InsigniaUtils.INSIGNIA_PATTERNS.size());
        for (InsigniaPattern pattern : InsigniaUtils.INSIGNIA_PATTERNS) {
            buffer.writeUtf(pattern.name);
            buffer.writeUtf(pattern.description);
            buffer.writeVarInt(pattern.codes.size());
            for (int i = 0; i < pattern.codes.size(); i++) {
                buffer.writeUtf(pattern.codes.get(i));
            }
        }
    }

    public static void fromBuffer(FriendlyByteBuf buffer) {
        if(InsigniaUtils.INSIGNIA_PATTERNS.size()>0)return;
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            InsigniaPattern pattern=new InsigniaPattern();
            pattern.name= buffer.readUtf();
            pattern.description=buffer.readUtf();

            int lineSize= buffer.readVarInt();
            for (int j = 0; j < lineSize; j++) {
               pattern.codes.add(buffer.readUtf());
            }

            InsigniaUtils.INSIGNIA_PATTERNS.add(pattern);
        }
    }

    public static class SyncPacket extends SimplePacketBase {

        private FriendlyByteBuf buffer;

        public SyncPacket() {
        }

        public SyncPacket(FriendlyByteBuf buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            toBuffer(buffer);
        }

        @Override
        public boolean handle(NetworkEvent.Context context) {
            context.enqueueWork(() -> {
                fromBuffer(buffer);
            });
            return true;
        }

    }


}
