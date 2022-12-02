package gay.aurum.retroactiveSynthesis;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RetroactiveSynthesis implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Retroactive Synthesis");
	public static String MODID = "retroactive-synthesis";
	public static RecipeType<RitualRecipe> RitualType = registerType("ritual");
	public static RitualRecipe.RitualSerializer Ritualserializer = Registry.register(Registry.RECIPE_SERIALIZER,Prefix("ritual"), new RitualRecipe.RitualSerializer());

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("{}: Loaded, have a good day", mod.metadata().name());

	}
	private static <T extends Recipe<?>> RecipeType<T> registerType(String id) {
		return Registry.register(Registry.RECIPE_TYPE, Prefix(id), new RecipeType<T>() {
			public String toString() {
				return Prefix(id).toString();
			}
		});
	}
	public static Identifier Prefix(String name){
		return new Identifier(MODID, name);
	}
}
