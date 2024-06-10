package org.madscientists.createelemancy.content.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.item.IAdditionalCreativeItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.madscientists.createelemancy.Elemancy.registrate;

public class ElemancyCreativeTabs {
	private static final DeferredRegister<CreativeModeTab> REGISTER =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Elemancy.ID);

	public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB = addTab("base", "Create: Elemancy",
			ElemancyItems.HEAT_SPICE::asStack);

	public static final RegistryObject<CreativeModeTab> DEV_CREATIVE_TAB = addTab("dev", "Create: Elemancy Dev",
			ElemancyItems.CREATIVE_SPICE::asStack);


	public static RegistryObject<CreativeModeTab> addTab(String id, String name, Supplier<ItemStack> icon) {
		String itemGroupId = "itemGroup." + Elemancy.ID + "." + id;
		registrate().addRawLang(itemGroupId, name);
		CreativeModeTab.Builder tabBuilder = CreativeModeTab.builder()
				.icon(icon)
				.displayItems((arg, arg2) -> displayItems(arg, arg2, id.equals("dev")))
				.title(Components.translatable(itemGroupId))
				.withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey());
		return REGISTER.register(id, tabBuilder::build);
	}


	private static void displayItems(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput,boolean pIsDev) {
		for (RegistryEntry<Item> item : registrate().getAll(ForgeRegistries.ITEMS.getRegistryKey())) {
			if (item.get() instanceof SequencedAssemblyItem)
				continue;
			if (item.get() instanceof IAdditionalCreativeItems customCreativeItem) {
				List<ItemStack> items = new ArrayList<>();
				if(pIsDev)
					customCreativeItem.addCreativeDevItems(items);
				else
					customCreativeItem.addCreativeMainItems(items);
				pOutput.acceptAll(items);
			}
			else if(!pIsDev)
				pOutput.accept(item.get());
		}
	}

	public static void register(IEventBus eventBus) {
		REGISTER.register(eventBus);
	}

}