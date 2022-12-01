package gay.aurum.retroactiveSynthesis;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RitualRecipe implements Recipe<Inventory> {

	@Override
	public boolean matches(Inventory inventory, World world) {
		return false;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return null;
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getOutput() {
		return null;
	}

	@Override
	public Identifier getId() {
		return null;
	}

		@Override
	public RecipeSerializer<?> getSerializer() {
		return null;
	}

	@Override
	public RecipeType<?> getType() {
		return null;
	}

	public static class RitualSerializer implements RecipeSerializer<RitualRecipe>{

		@Override
		public RitualRecipe read(Identifier id, JsonObject json) {
			return null;
		}

		@Override
		public RitualRecipe read(Identifier id, PacketByteBuf buf) {
			return null;
		}

		@Override
		public void write(PacketByteBuf buf, RitualRecipe recipe) {

		}
	}
}
