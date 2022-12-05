package gay.aurum.retroactiveSynthesis;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckItemTick {

	static Map<BlockPos, BlockPattern.Result> Results = new HashMap<>();
	static Map<BlockPos, RitualRecipe> Rituals = new HashMap<>();
	static Map<BlockPos, Integer> Timers = new HashMap<>();

	public static void serverWorldEndTick(MinecraftServer server) {
		List<RitualRecipe> ritualRecipes = server.getRecipeManager().listAllOfType(RetroactiveSynthesis.RitualType);
		for (ServerWorld world : server.getWorlds()){
			List<? extends ItemEntity> itemEntities = world.getEntitiesByType(EntityType.ITEM, Entity::isOnGround);
			for(ItemEntity item : itemEntities){
				BlockPos pos = item.getBlockPos();
				if(Results.containsKey(pos)) continue;
				for(RitualRecipe recipe : ritualRecipes){
					if(recipe.getCatalyst().isItemEqual(item.getStack()) && recipe.catalyst.getCount()<=item.getStack().getCount()){
						BlockPattern.Result pass = recipe.getRitual().searchAround(world, pos);
						if(pass != null){
							Rituals.put(pos, recipe);
							Results.put(pos, pass);
							break;
						}
					}
				}
			}
			//world.spawnParticles()
		}

	}

}
