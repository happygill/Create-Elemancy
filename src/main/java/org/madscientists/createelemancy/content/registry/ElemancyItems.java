package org.madscientists.createelemancy.content.registry;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.madscientists.createelemancy.content.ability.AbilityTotemItem;
import org.madscientists.createelemancy.content.item.*;

import static org.madscientists.createelemancy.Elemancy.LOGGER;
import static org.madscientists.createelemancy.Elemancy.registrate;
import static org.madscientists.createelemancy.content.registry.ElemancyElement.*;

@SuppressWarnings("unused")
public class ElemancyItems {
	public static void init() {
		LOGGER.info("Registering items!");
	}

	// spices
	public static final ItemEntry<MixedSpiceItem> MIXED_SPICE = register("mixed_spice", MixedSpiceItem::new);
	public static final ItemEntry<Item>

			HEAT_SPICE = simple("heat_spice"),
			FRIGID_SPICE = simple("frigid_spice"),
			STABILITY_SPICE = simple("stability_spice"),
			REACTIVITY_SPICE = simple("reactivity_spice"),
            DENSITY_SPICE = simple("density_spice"),
            FEATHERWEIGHT_SPICE = simple("featherweight_spice"),
            POWER_SPICE = simple("power_spice"),
            NULL_SPICE = simple("null_spice"),

    CREATIVE_SPICE = registrate()
            .item("creative_spice", Item::new)
            .properties(properties -> properties.rarity(Rarity.EPIC))
            .register();




	public static final ItemEntry<KineticSpiceItem>
			KINETIC_SPICE = register("kinetic_spice", KineticSpiceItem::new);

	public static final ItemEntry<GarbledSpiceItem>
			GARBLED_SPICE = register("garbled_spice", GarbledSpiceItem::new),
			DILUTED_GARBLED_SPICE = register("diluted_garbled_spice", GarbledSpiceItem::new);


	public static final ItemEntry<InsigniaGuideItem>
			INSIGNIA_GUIDE = register("insignia_guide", InsigniaGuideItem::new);

	public static final ItemEntry<AbilityTotemItem>
			ABILITY_TOTEM = register("ability_totem", AbilityTotemItem::new);

	public static final ItemEntry<Item>

			RUNIC_SLATE = simple("runic_slate");

	public static final ItemEntry<RunicInsigniaItem>
			RUNIC_INSIGNIA = register("runic_insignia", RunicInsigniaItem::new);
	// cores
	public static final ItemEntry<Item>

            FROSTBURN_CORE = simple("frostburn_core"),
            QUANTUM_CORE = simple("quantum_core"),
            GRAVITATIONAL_CORE = simple("gravitational_core"),
            ELEMENTAL_CORE = simple("elemental_core"),
            POWER_CORE = simple("power_core"),
            NULL_CORE = simple("null_core");


	// chalks
	public static final ItemEntry<ChalkItem>

			SHORT_HEAT_CHALK = chalk(HEAT, false),
			LONG_HEAT_CHALK = chalk(HEAT, true),

			SHORT_FRIGID_CHALK = chalk(FRIGID, false),
			LONG_FRIGID_CHALK = chalk(FRIGID, true),

			SHORT_REACTIVITY_CHALK = chalk(REACTIVITY, false),
			LONG_REACTIVITY_CHALK = chalk(REACTIVITY, true),

			SHORT_STABILITY_CHALK = chalk(STABILITY, false),
			LONG_STABILITY_CHALK = chalk(STABILITY, true),

			SHORT_DENSITY_CHALK = chalk(DENSITY, false),
			LONG_DENSITY_CHALK = chalk(DENSITY, true),

			SHORT_FEATHERWEIGHT_CHALK = chalk(FEATHERWEIGHT, false),
			LONG_FEATHERWEIGHT_CHALK = chalk(FEATHERWEIGHT, true),
			SHORT_POWER_CHALK = chalk(POWER, false),
			LONG_POWER_CHALK = chalk(POWER, true),
			SHORT_NULL_CHALK = chalk(NULL, false),
			LONG_NULL_CHALK = chalk(NULL, true);

	public static final ItemEntry<SequencedAssemblyItem>

			INCOMPLETE_SHORT_HEAT_CHALK = sequencedIngredient("incomplete_short_heat_chalk"),
			INCOMPLETE_SHORT_FRIGID_CHALK = sequencedIngredient("incomplete_short_frigid_chalk"),
			INCOMPLETE_SHORT_REACTIVITY_CHALK = sequencedIngredient("incomplete_short_reactivity_chalk"),
			INCOMPLETE_SHORT_STABILITY_CHALK = sequencedIngredient("incomplete_short_stability_chalk"),
			INCOMPLETE_SHORT_DENSITY_CHALK = sequencedIngredient("incomplete_short_density_chalk"),
			INCOMPLETE_SHORT_FEATHERWEIGHT_CHALK = sequencedIngredient("incomplete_short_featherweight_chalk");

	/* * * * * * * * * * */
	/* F A C T O R I E S */
	/* * * * * * * * * * */

	private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
		return register(name, SequencedAssemblyItem::new);
	}

	private static <T extends Item> ItemEntry<T> register(String name, NonNullFunction<Item.Properties, T> factory) {
		return registrate().item(name, factory).register();
	}

	private static ItemEntry<ChalkItem> chalk(ElemancyElement elemancyElement, boolean isLong) {
		String name = "%s_%s_chalk".formatted(isLong ? "long" : "short", elemancyElement.name().toLowerCase());
		return register(name, properties -> new ChalkItem(properties.durability(isLong ? 200 : 20), elemancyElement));
	}

	private static ItemEntry<Item> simple(String name) {
		return register(name, Item::new);
	}
}
