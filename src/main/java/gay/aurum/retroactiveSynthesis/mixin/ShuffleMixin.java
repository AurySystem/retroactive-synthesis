package gay.aurum.retroactiveSynthesis.mixin;

import gay.aurum.retroactiveSynthesis.DuckShuffle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(SimpleRegistry.class)
public class ShuffleMixin<T> implements DuckShuffle {

	private boolean veryshuffled = false;
	@Final
	@Shadow
	private
	Map<Identifier, Holder.Reference<T>> byId;

	@Shadow
	@Final
	private Map<RegistryKey<T>, Holder.Reference<T>> byKey;

//	@Final
//	@Shadow
//	private ObjectList<Holder.Reference<T>> rawIdToEntry;
//
//	@Final
//	@Shadow
//	private Object2IntMap<T> entryToRawId;

	@Override
	public boolean hasRun() {
		return veryshuffled;
	}

	@Override
	public void shuffleAll() {
		List<Identifier> ids = Set.copyOf(this.byId.keySet()).stream().toList();
		List<Holder.Reference<T>> byids =  this.byId.values().stream().toList();
		List<RegistryKey<T>> keys = Set.copyOf(this.byKey.keySet()).stream().toList();
		List<Holder.Reference<T>> bykeys = this.byKey.values().stream().toList();

//		int[] rawIds = this.entryToRawId.values().intStream().toArray();
//		List<T> byEntry = this.entryToRawId.keySet().stream().toList();
//		List<Holder.Reference<T>> rawIdEntries = this.rawIdToEntry.stream().toList();

		this.byId.clear();
		this.byKey.clear();

//		this.rawIdToEntry.clear();
//		this.entryToRawId.clear();

		int len = ids.size();
		for (int i = 0; i < len; i++) {
			this.byId.put(ids.get(i), byids.get((i+2) % len));
			this.byKey.put(keys.get(i), bykeys.get((i+2) % len));
//			this.rawIdToEntry.add(rawIdEntries.get((i+2) % len));
//			this.entryToRawId.put(byEntry.get(i), (rawIds[i]+2) % len);
		}



		this.veryshuffled = true;
	}
}
