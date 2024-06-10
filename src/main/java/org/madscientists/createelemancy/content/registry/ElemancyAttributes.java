package org.madscientists.createelemancy.content.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.item.IAdditionalCreativeItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.madscientists.createelemancy.Elemancy.registrate;
import static org.madscientists.createelemancy.Elemancy.toHumanReadable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Elemancy.ID)
public class ElemancyAttributes {
	private static final DeferredRegister<Attribute> REGISTER =
			DeferredRegister.create(Registries.ATTRIBUTE, Elemancy.ID);

	public static final RegistryObject<Attribute> EXTRA_JUMPS = registerRanged("extra_jumps", 0, 0, 10);


	private static RegistryObject<Attribute> registerRanged(String name,double defaultValue,double minValue,double maxValue) {
		String attributeId = "attribute."+Elemancy.ID+"."+name;
		registrate().addRawLang(attributeId, toHumanReadable(name));
		return REGISTER.register(name, () -> new RangedAttribute(attributeId, defaultValue, minValue, maxValue).setSyncable(true));
	}
	public static void register(IEventBus eventBus) {
		REGISTER.register(eventBus);
	}

	@SubscribeEvent
	public static void addAttributes(EntityAttributeModificationEvent event){
		event.add(EntityType.PLAYER, EXTRA_JUMPS.get());
	}
}