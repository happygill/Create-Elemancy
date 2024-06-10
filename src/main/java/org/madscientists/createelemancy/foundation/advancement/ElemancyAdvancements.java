package org.madscientists.createelemancy.foundation.advancement;

import com.google.common.collect.Sets;
import com.simibubi.create.AllItems;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.registry.ElemancyFluids;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static org.madscientists.createelemancy.foundation.advancement.Advancement.TaskType.*;


public class ElemancyAdvancements implements DataProvider {

    public static final List<Advancement> ENTRIES = new ArrayList<>();
    public static final Advancement START = null,


    ROOT = create("root", b -> b.icon(ElemancyItems.POWER_SPICE)
            .title("Create Elemancy")
            .description("You play with forces beyond your understanding.")
            .awardedForFree()
            .special(SILENT)),

    GRINDING_WHEEL = create("grinding_wheel", b -> b.icon(ElemancyBlocks.GRINDING_WHEEL)
            .title("A Grinding Duo")
            .description("Craft and place the Grinding Wheels")
            .whenBlockPlaced(ElemancyBlocks.GRINDING_WHEEL.get())
            .after(ROOT)
            .special(NOISY)),

    FERN = create("fern", b -> b.icon(Blocks.FERN)
            .title("As The Fern Demands")
            .description("Grind a Double Fern, Intresting")
            .after(GRINDING_WHEEL)
            .special(SECRET)),

    A_PECULIAR_POWDER = create("peculiar_powder", b -> b.icon(ElemancyItems.MIXED_SPICE)
            .title("A Peculiar Powder")
            .description("Obtain a Mixed Spice from grinding raw materials")
            .whenIconCollected()
            .after(GRINDING_WHEEL)
            .special(NOISY)),

    NEUTRALIZED = create("neutralized", b -> b.icon(Items.MILK_BUCKET)
            .title("Element Neutralized")
            .description("Use Milk from a spout to Neutralize one of each element in mixed spice")
            .after(A_PECULIAR_POWDER)
            .special(NOISY)),

    ENERGY = create("energy", b -> b.icon(ElemancyItems.HEAT_SPICE)
            .title("Energy, in its purest form")
            .description("Obtain a purified spice, of a singular element")
            .after(NEUTRALIZED)
            .special(EXPERT)),


    QUANTUM_CORE = create("quantum_core", b -> b.icon(ElemancyItems.QUANTUM_CORE)
            .title("Super Positioned")
            .description("Assemble a Quantum Core")
            .whenIconCollected()
            .after(ENERGY)
            .special(NOISY)),

    FROSTBURN_CORE = create("frostburn_core", b -> b.icon(ElemancyItems.FROSTBURN_CORE)
            .title("Icy Hot")
            .description("Assemble a FrostBurn Core")
            .whenIconCollected()
            .after(ENERGY)
            .special(NOISY)),

    GRAVITATIONAL_CORE = create("gravitational_core", b -> b.icon(ElemancyItems.GRAVITATIONAL_CORE)
            .title("Orbiting Matter")
            .description("Assemble a Gravitational Core")
            .whenIconCollected()
            .after(ENERGY)
            .special(NOISY)),

    ELEMENTAL_CORE = create("elemental_core", b -> b.icon(ElemancyItems.ELEMENTAL_CORE)
            .title("We're all adrift together.. together")
            .description("The Combination of them all Forge the Elemental Core")
            .whenIconCollected()
            .after(FROSTBURN_CORE)
            .special(NOISY)),

    POWER_CORE = create("power_core", b -> b.icon(ElemancyItems.POWER_CORE)
            .title("Refined Light")
            .description("Forge, The Core of Power")
            .whenIconCollected()
            .after(ELEMENTAL_CORE)
            .special(NOISY)),

    NULL_CORE = create("null_core", b -> b.icon(ElemancyItems.NULL_CORE)
            .title("The Eighth Element")
            .description("You Should Have Known Power Had an Opposite, Forge the Core of Null")
            .whenIconCollected()
            .after(POWER_CORE)
            .special(SECRET)),

    DISTILLER = create("distiller", b -> b.icon(ElemancyBlocks.DISTILLER)
            .title("Lab Equipment")
            .description("Distil a Mixed Spice into its liquid Components")
            .after(NEUTRALIZED)
            .special(NOISY)),

    VORTEX = create("vortex", b -> b.icon(ElemancyBlocks.VCP)
            .title("A Harnessed Storm")
            .description("Successfully run a Vortex Generator for more than 1 in game day")
            .after(ENERGY)
            .special(NOISY)),

    FIVE = create("five", b -> b.icon(ElemancyBlocks.VCP)
            .title("Category Five")
            .description("Successfully run a Vortex Generator for more than 1 in game day")
            .after(VORTEX)
            .special(SECRET)),

    WASTE = create("waste", b -> b.icon(ElemancyItems.GARBLED_SPICE)
            .title("A Volatile Substance")
            .description("Elemental Vortexes collapse into a hazardous waste product, deal with it.")
            .after(VORTEX)
            .special(NOISY)),

    SOLID = create("solid", b -> b.icon(ElemancyItems.DILUTED_GARBLED_SPICE)
            .title("Solidified Volatilitye")
            .description("Attempts to spout this liquid will result in a clump forming, be warned its still highly volatile")
            .after(WASTE)
            .special(NOISY)),


    PROJECTILE = create("projectile", b -> b.icon(AllItems.POTATO_CANNON)
            .title("Fwoomp 2 Explosive Boogaloo")
            .description("Dilute Garbled Spice so you can work with it more easily.")
            .after(SOLID)
            .special(NOISY)),

    LIQUID = create("liquid", b -> b.icon(ElemancyItems.DILUTED_GARBLED_SPICE)
            .title("A Watery Solution")
            .description("Dilute Garbled Spice so you can work with it more easily.")
            .after(WASTE)
            .special(NOISY)),

    DISTILL_WASTE = create("distill_waste", b -> b.icon(ElemancyBlocks.DISTILLER)
            .title("Distilled Danger")
            .description("Distill Garbled Spice, effectively neutralising it.")
            .after(LIQUID)
            .special(NOISY)),

    WAFFLE = create("waffle", b -> b.icon(ElemancyItems.GARBLED_SPICE)
            .title("The Waffle House is closed")
            .description("Garbled Spice build up reached Critical Vortex detonating.")
            .after(WASTE)
            .special(SECRET)),


    CRAYON = create("crayon", b -> b.icon(ElemancyItems.SHORT_HEAT_CHALK)
            .title("Basically a Crayon")
            .description("Create your first Elemental Chalk")
            .after(ENERGY)
            .special(NOISY)),

    RITUAL = create("ritual", b -> b.icon(ElemancyItems.INSIGNIA_GUIDE)
            .title("Its not a Ritual")
            .description("Draw your first Insignia")
            .after(CRAYON)
            .special(NOISY)),

    LONG_CHALK = create("long_chalk", b -> b.icon(ElemancyItems.LONG_HEAT_CHALK)
            .title("Compensating?")
            .description("Create a Long Elemental Chalk")
            .after(CRAYON)
            .special(NOISY)),

    RUNIC = create("runic", b -> b.icon(ElemancyItems.RUNIC_SLATE)
            .title("Runically Enhanced")
            .description("Create your first Runic Insignia")
            .after(RITUAL)
            .special(NOISY)),

    INSIGNIA5 = create("insignia5", b -> b.icon(ElemancyItems.INSIGNIA_GUIDE)
            .title("Large Art Project")
            .description("Draw your first 5x5 Insignia")
            .after(RITUAL)
            .special(NOISY)),


    END = null;
    private final PackOutput output;

    // Datagen

    public ElemancyAdvancements(PackOutput output) {
        this.output = output;
    }

    private static Advancement create(String id, UnaryOperator<Advancement.Builder> b) {
        return new Advancement(id, b);
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (Advancement advancement : ENTRIES)
            advancement.provideLang(consumer);
    }

    public static void register() {
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        List<CompletableFuture<?>> futures = new ArrayList<>();

        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<net.minecraft.advancements.Advancement> consumer = (advancement) -> {
            ResourceLocation id = advancement.getId();
            if (!set.add(id))
                throw new IllegalStateException("Duplicate advancement " + id);
            Path path = pathProvider.json(id);
            futures.add(DataProvider.saveStable(cache, advancement.deconstruct()
                    .serializeToJson(), path));
        };

        for (Advancement advancement : ENTRIES)
            advancement.save(consumer);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Elemancy's Advancements";
    }

}
