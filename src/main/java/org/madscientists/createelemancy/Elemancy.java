package org.madscientists.createelemancy;


import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.commons.lang3.StringUtils;
import org.madscientists.createelemancy.foundation.advancement.ElemancyTriggers;
import org.madscientists.createelemancy.foundation.advancement.ElemancyAdvancements;
import org.madscientists.createelemancy.content.insignia.InsigniaUtils;
import org.madscientists.createelemancy.content.projectile.ElemancyPotatoProjectileTypes;
import org.madscientists.createelemancy.content.projectile.PotatoGunHelper;
import org.madscientists.createelemancy.content.item.GarbledSpiceItem;
import org.madscientists.createelemancy.content.registry.*;
import org.madscientists.createelemancy.foundation.config.ElemancyConfig;
import org.madscientists.createelemancy.foundation.data.ElemancyDatagen;
import org.madscientists.createelemancy.foundation.events.CommonEvents;
import org.madscientists.createelemancy.foundation.network.ElemancyMessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod(Elemancy.ID)
public class Elemancy {
	public static final String ID = "createelemancy";
	public static final String NAME = "Create: Elemancy";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static final String VERSION = getVersion();
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Elemancy.ID);

	public static CreateRegistrate registrate() {
		Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
		if(callerClass.getName().startsWith(Elemancy.class.getPackageName())) {
			return REGISTRATE;
		}
		LOGGER.error(String.format("Mod %s tried to access Elemancy's registrate! Instead, make your own. Class: %s",
				ModLoadingContext.get().getActiveContainer().getModId(), callerClass.getName()),
				new IllegalCallerException("Access denied to Elemancy Registrate"));
		System.exit(1);
		return null;
	}

	private static boolean lock = false;

	public Elemancy() {
		if(lock)
			throw new IllegalStateException("Somebody tried to initialize Elemancy, but it already was!");
		lock = true;

		LOGGER.info("Create: Elemancy v{} initializing!", VERSION);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModLoadingContext context = ModLoadingContext.get();
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

		REGISTRATE.registerEventListeners(modEventBus);
		ElemancyAttributes.register(modEventBus);
		ElemancyAbilityTypes.init();
		ElemancyNullSpaceTypes.register();
		ElemancyBlocks.init();
		ElemancyConfig.register(context);
		ElemancyFluids.init();
		ElemancyItems.init();
		ElemancyBlockEntities.init();
		ElemancyPartials.init();
		ElemancyEntities.init();
		ElemancySoundEvents.prepare();
		ElemancyDamageTypes.register();
		ElemancyRecipes.register(modEventBus);
		CommonEvents.init();
		InsigniaUtils.init();
		ElemancyCreativeTabs.register(modEventBus);
		modEventBus.addListener(EventPriority.LOWEST, ElemancyDatagen::gatherData);
		modEventBus.addListener(Elemancy::init);
		registerForgeEvents(forgeEventBus);
		modEventBus.addListener(ElemancySoundEvents::register);

	}


	public static void init(final FMLCommonSetupEvent event) {
		ElemancyPotatoProjectileTypes.init();
		event.enqueueWork(() -> {
			ElemancyMessages.register();
			ElemancyAdvancements.register();
			ElemancyTriggers.register();
			ElemancyFluids.registerFluidInteractions();
		});
	}


	private void registerForgeEvents(IEventBus forgeEventBus) {
		forgeEventBus.addListener(PotatoGunHelper::potatoCannonEvent);
		forgeEventBus.addListener(GarbledSpiceItem::garbledSpiceItemExpire);
	}

	/**
	 * copy create's toHumanReadable method to prevent their config classes from being loaded too early
	 */
	public static String toHumanReadable(String key) {
		String s = key.replaceAll("_", " ");
		s = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(s)).map(StringUtils::capitalize).collect(Collectors.joining(" "));
		s = StringUtils.normalizeSpace(s);
		return s;
	}

	public static ResourceLocation asResource(String name) {
		return new ResourceLocation(ID, name);
	}

	private static String getVersion() {
		Optional<? extends ModContainer> container = ModList.get().getModContainerById(ID);
		if(container.isEmpty()) {
			LOGGER.warn("Could not find mod container for modid " + ID);
			return "UNKNOWN";
		}
		return container.get()
				.getModInfo()
				.getVersion()
				.toString();
	}
}
