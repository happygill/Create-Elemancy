package org.madscientists.createelemancy.foundation.data;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancySoundEvents;
import org.madscientists.createelemancy.foundation.advancement.ElemancyAdvancements;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ElemancyDatagen {
    public static void gatherData(GatherDataEvent event) {
        addExtraRegistrateData();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeClient()) {
            generator.addProvider(true, ElemancySoundEvents.provider(generator));
        }

        if (event.includeServer()) {
            GeneratedEntriesProvider generatedEntriesProvider = new GeneratedEntriesProvider(output, lookupProvider);
            lookupProvider = generatedEntriesProvider.getRegistryProvider();
            generator.addProvider(true, generatedEntriesProvider);

            generator.addProvider(true, new DamageTypeTagGen(output, lookupProvider, existingFileHelper));
            generator.addProvider(true, new ElemancyAdvancements(output));
        }
    }

    private static void addExtraRegistrateData() {

        Elemancy.registrate().addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;
            ElemancyAdvancements.provideLang(langConsumer);
            ElemancySoundEvents.provideLang(langConsumer);
        });
    }

}
