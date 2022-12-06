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

	static Map<ServerWorld, Maps> Worlds = new HashMap<>();
	private static class Maps{
		protected Map<area, BlockPattern.Result> results = new HashMap<>();
		protected Map<area, RitualRecipe> rituals = new HashMap<>();
		protected Map<area, Integer> timers = new HashMap<>();
		Maps(ServerWorld world){
			Worlds.put(world, this);
		}

	}
	static Maps getOrCreateMaps(ServerWorld world){
		Worlds.putIfAbsent(world, new Maps(world));
		return Worlds.get(world);
	}
	public static void serverWorldStartTick(MinecraftServer server){
		for (ServerWorld world : server.getWorlds()){
			Maps maps = getOrCreateMaps(world);
			for (Map.Entry<area, Integer> a : maps.timers.entrySet()) {
				area key = a.getKey();
				int timer = a.getValue();
				if(timer > 0){
					maps.timers.put(key,timer-1);
					//world.spawnParticles()
				}else{
					//todo rest of the owl
					maps.timers.remove(key);
					maps.results.remove(key);
					maps.rituals.remove(key);
				}
			}
		}

	}

	public static void serverWorldEndTick(MinecraftServer server) {
		List<RitualRecipe> ritualRecipes = server.getRecipeManager().listAllOfType(RetroactiveSynthesis.RitualType);
		for (ServerWorld world : server.getWorlds()){
			List<? extends ItemEntity> itemEntities = world.getEntitiesByType(EntityType.ITEM, Entity::isOnGround);
			Maps maps = getOrCreateMaps(world);
			for(ItemEntity item : itemEntities){
				BlockPos pos = item.getBlockPos();
				boolean check = false;
				for (area a : maps.timers.keySet()){
					if(a.isInside(pos)) {
						check = true;
						break;
					}
				}
				if(check) continue;
				for(RitualRecipe recipe : ritualRecipes){
					if(recipe.getCatalyst().isItemEqual(item.getStack()) && recipe.catalyst.getCount()<=item.getStack().getCount()){
						BlockPattern.Result pass = recipe.getRitual().searchAround(world, pos);
						if(pass != null){
							area areaa = new area(pass.translate(0,0,0).getBlockPos(),pass.translate(pass.getWidth(), pass.getHeight(), pass.getDepth()).getBlockPos());
							maps.rituals.put(areaa, recipe);
							maps.results.put(areaa, pass);
							maps.timers.put(areaa, recipe.craftime);
							break;
						}
					}
				}
			}
		}

	}

	public record area(BlockPos corner1, BlockPos corner2){

		public boolean isInside(BlockPos testPos){
			if((corner1.getX() >= testPos.getX() && corner2.getX() <= testPos.getX()) || (corner2.getX() >= testPos.getX() && corner1.getX() <= testPos.getX())){
				if((corner1.getY() >= testPos.getY() && corner2.getY() <= testPos.getY()) || (corner2.getY() >= testPos.getY() && corner1.getY() <= testPos.getY())){
					return (corner1.getZ() >= testPos.getZ() && corner2.getZ() <= testPos.getZ()) || (corner2.getZ() >= testPos.getZ() && corner1.getZ() <= testPos.getZ());
				}
			}
			return false;
		}

	}

}
