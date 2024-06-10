package org.madscientists.createelemancy.foundation.advancement;

import com.google.common.collect.Maps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.madscientists.createelemancy.Elemancy;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CriterionTriggerBase<T extends CriterionTriggerBase.Instance> implements CriterionTrigger<T> {

	public CriterionTriggerBase(String id) {
		this.id = Elemancy.asResource(id);
	}

	private final ResourceLocation id;
	protected final Map<PlayerAdvancements, Set<Listener<T>>> listeners = Maps.newHashMap();

	@Override
	public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
		Set<Listener<T>> playerListeners = this.listeners.computeIfAbsent(playerAdvancementsIn, k -> new HashSet<>());

		playerListeners.add(listener);
	}

	@Override
	public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
		Set<Listener<T>> playerListeners = this.listeners.get(playerAdvancementsIn);
		if (playerListeners != null) {
			playerListeners.remove(listener);
			if (playerListeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	protected void trigger(ServerPlayer player, @Nullable List<Supplier<Object>> suppliers) {
		PlayerAdvancements playerAdvancements = player.getAdvancements();
		Set<Listener<T>> playerListeners = this.listeners.get(playerAdvancements);
		if (playerListeners != null) {
			List<Listener<T>> list = new LinkedList<>();

			for (Listener<T> listener : playerListeners) {
				if (listener.getTriggerInstance()
					.test(suppliers)) {
					list.add(listener);
				}
			}

			list.forEach(listener -> listener.run(playerAdvancements));

		}
	}

	public abstract static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation idIn, ContextAwarePredicate predicate) {
			super(idIn, predicate);
		}

		protected abstract boolean test(@Nullable List<Supplier<Object>> suppliers);
	}

}
