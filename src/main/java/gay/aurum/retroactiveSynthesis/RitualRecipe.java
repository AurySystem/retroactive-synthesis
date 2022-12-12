package gay.aurum.retroactiveSynthesis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.unascribed.lib39.machination.ingredient.BlockIngredient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.quiltmc.qsl.recipe.api.serializer.QuiltRecipeSerializer;

import java.util.Map;
import java.util.function.Predicate;

public class RitualRecipe implements Recipe<Inventory> {

	protected final Identifier id;
	protected final String group;
	protected final ItemStack catalyst;
	protected final BlockPatternInfo ritual;

	protected final ItemStack item;
	protected final Identifier entity;
	protected final Identifier feature;
	protected final int craftime;
	protected final Vec3d offset;

	public RitualRecipe(Identifier id, String group, ItemStack catalyst, int craftime, BlockPatternInfo ritual, Vec3d offset, ItemStack item, Identifier entity, Identifier feature){
		this.id = id;
		this.group = group;
		this.catalyst = catalyst;
		this.craftime = craftime;
		this.ritual = ritual;
		this.offset = offset;
		this.item = item;
		this.entity = entity;
		this.feature = feature;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return false;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return item.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getOutput() {
		return item.copy();
	}

	@Override
	public String getGroup() {
		return group;
	}
	@Override
	public Identifier getId() {
		return id;
	}

		@Override
	public RecipeSerializer<?> getSerializer() {
		return RetroactiveSynthesis.Ritualserializer;
	}

	@Override
	public RecipeType<?> getType() {
		return RetroactiveSynthesis.RitualType;
	}

	public BlockPattern getRitual(){
		return this.ritual;
	}

	public int getSummontime() {
		return craftime;
	}

	public ItemStack getCatalyst(){
		return this.catalyst.copy();
	}

	public Identifier getEntity() {
		return this.entity;
	}

	public Identifier getFeature() {
		return feature;
	}

	public Vec3d getOffset() {
		return offset;
	}

	public static Predicate<CachedBlockPosition> matchesBlock(Predicate<Block> block) {
		return pos -> pos != null && block.test(pos.getBlockState().getBlock());
	}

	public static class RitualSerializer implements QuiltRecipeSerializer<RitualRecipe> {
		JsonArray defualtPos(){
			JsonArray l = new JsonArray();
			l.add(0);
			l.add(0);
			l.add(0);
			return l;
		}

		@Override
		public RitualRecipe read(Identifier id, JsonObject obj) {
			String group = JsonHelper.getString(obj,"group","");
			ItemStack cat = ShapedRecipe.outputFromJson(obj.getAsJsonObject("catalyst"));
			int time = JsonHelper.getInt(obj, "summon-time",0);

			JsonArray posar = JsonHelper.getArray(obj,"offset", defualtPos());
			Vec3d off = new Vec3d(posar.get(0).getAsInt(),posar.get(1).getAsInt(),posar.get(2).getAsInt());

			ItemStack item = ShapedRecipe.outputFromJson(obj.getAsJsonObject("item"));
			Identifier entity = new Identifier(JsonHelper.getString(obj, "entity",""));
			Identifier feature = new Identifier(JsonHelper.getString(obj, "feature",""));

			BlockPatternBuilder pattern = BlockPatternBuilder.start();
			BlockPatternInfo.builder build = new BlockPatternInfo.builder();
			JsonArray patternRows = JsonHelper.getArray(obj,"pattern");
			String[] rowsAr = new String[patternRows.size()];
			for(int i = 0; i < patternRows.size(); i++){
				rowsAr[i] = patternRows.get(i).getAsString();
			}
			pattern.aisle(rowsAr);
			build.layer(rowsAr);
			JsonObject keys = JsonHelper.getObject(obj,"key");
			for(Map.Entry<String, JsonElement> entry : keys.entrySet()) {
				if (entry.getKey().length() != 1) {
					throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
				}
				if (" ".equals(entry.getKey())) {
					throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
				}
				BlockIngredient a = BlockIngredient.fromJson(entry.getValue());
				if (entry.getValue().isJsonObject()) {
					JsonObject ah = entry.getValue().getAsJsonObject();
					boolean consume = JsonHelper.getBoolean(ah, "consume", false) || JsonHelper.hasString(ah, "remainder");
					BlockState block = JsonHelper.hasString(ah, "remainder")? Registry.BLOCK.get(Identifier.tryParse(ah.get("remainder").getAsString())).getDefaultState():
							Blocks.AIR.getDefaultState();
					build.key(entry.getKey().charAt(0), new BlockPatternInfo.ExtraBlockInfo(consume,block,JsonHelper.getBoolean(ah, "particle", false)));
				}
				pattern.where(entry.getKey().charAt(0), matchesBlock(a));
			}



			return new RitualRecipe(id, group, cat, time, build.build(pattern.build()), off, item, entity, feature);
		}

		@Override
		public RitualRecipe read(Identifier id, PacketByteBuf buf) {
			String group = buf.readString();
			ItemStack cat = buf.readItemStack();
			//todo networks
			return null;
		}

		@Override
		public void write(PacketByteBuf buf, RitualRecipe recipe) {
			//todo networks
		}

		@Override
		public JsonObject toJson(RitualRecipe recipe) {
			return null;
		}
	}
}
