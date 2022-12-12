package gay.aurum.retroactiveSynthesis;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldEvents;

import java.util.*;

public class CheckItemTick {

	private static final Identifier blankid = new Identifier("");
	static Map<ServerWorld, Maps> Worlds = new HashMap<>();
	private static class Maps{
		protected Map<area, BlockPattern.Result> results = new HashMap<>();
		protected Map<area, RitualRecipe> rituals = new HashMap<>();
		protected Map<area, Integer> timers = new HashMap<>();
		protected Map<area, ItemEntity> entity = new HashMap<>();
		protected Map<area, List<BlockPos>> parts = new HashMap<>();
		Maps(ServerWorld world){
			Worlds.put(world, this);
		}

	}
	static Maps getOrCreateMaps(ServerWorld world){
		if(Worlds.containsKey(world)){
			return Worlds.get(world);
		}
		Worlds.put(world, new Maps(world));
		return Worlds.get(world);
	}
	public static void serverWorldStartTick(MinecraftServer server){
		for (ServerWorld world : server.getWorlds()){
			Maps maps = getOrCreateMaps(world);
			for (Map.Entry<area, Integer> a : maps.timers.entrySet()) {
				area key = a.getKey();
				int timer = a.getValue();
				if(timer > 0){
					maps.timers.put(key, timer-1);
					ItemEntity itemEntity = maps.entity.get(key);
					if(itemEntity!=null && (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty())){
						Vec3d aaa = itemEntity.getPos();
						world.spawnParticles(ParticleTypes.SOUL,aaa.getX(),aaa.getY(),aaa.getZ(),1,0,0,0,0);
						for (BlockPos pos:maps.parts.get(key)) {
							world.spawnParticles(ParticleTypes.SCULK_SOUL,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,1,0,0,0,0);
						}
					} else {
						maps.entity.remove(key);
						maps.timers.remove(key);
						maps.results.remove(key);
						maps.rituals.remove(key);
						maps.parts.remove(key);
					}
				}else{
					ItemEntity itemEntity = maps.entity.get(key);
					RitualRecipe recipe = maps.rituals.get(key);
					BlockPatternInfo aa = recipe.ritual;
					BlockPattern.Result result = maps.results.get(key);
					if(itemEntity!=null && (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty()) && recipe.catalyst.isItemEqual(itemEntity.getStack())){
						if (aa.testTransform(world,result.getFrontTopLeft(),result.getForwards(),result.getUp())==null)continue;
						for(int x = 0; x < aa.getWidth(); ++x) {
							for(int y = 0; y < aa.getHeight(); ++y) {
								BlockPatternInfo.ExtraBlockInfo info = aa.getInfoAtPos(x, y, 0);
								CachedBlockPosition cachedBlockPosition3 = result.translate(x, y, 0);
								if(info.consume()){
									world.setBlockState(cachedBlockPosition3.getBlockPos(), info.remainder(), Block.NOTIFY_LISTENERS);
									world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
								}
							}
						}
						itemEntity.getStack().decrement(recipe.catalyst.getCount());

						Vec3d off = recipe.getOffset();
						BlockPos corner = key.corner1();
						BlockPos corner2 = key.corner2();
						double centerX = (corner.getX()-corner2.getX())/2d+corner.getX() +off.x +0.5;
						double centerY = (corner.getY()-corner2.getY())/2d+corner.getY() +off.y +0.5;
						double centerZ = (corner.getZ()-corner2.getZ())/2d+corner.getZ() +off.z +0.5;
						if(recipe.item !=null){
							ItemEntity item = new ItemEntity(world, centerX, centerY, centerZ, recipe.getOutput());
							item.setToDefaultPickupDelay();
							world.spawnEntity(item);
						}
						if (!recipe.getEntity().equals(blankid)){
							NbtCompound nbtCompound = new NbtCompound();
							nbtCompound.putString("id", recipe.getEntity().toString());
							Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, entityx -> {
								entityx.refreshPositionAndAngles(centerX, centerY, centerZ, entityx.getYaw(), entityx.getPitch());
								return entityx;
							});
							if(entity2!=null){
								world.spawnEntity(entity2);
							}
						}
						if (!recipe.getFeature().equals(blankid)){

							Objects.requireNonNull(BuiltinRegistries.CONFIGURED_FEATURE.get(recipe.getFeature())).generate(world, world.getChunkManager().getChunkGenerator(), world.random, new BlockPos(centerX,centerY,centerZ));
						}
					}
					maps.entity.remove(key);
					maps.timers.remove(key);
					maps.results.remove(key);
					maps.rituals.remove(key);
					maps.parts.remove(key);
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
							if(maps.rituals.containsKey(areaa))break;
							maps.rituals.put(areaa, recipe);
							maps.results.put(areaa, pass);
							maps.timers.put(areaa, recipe.getSummontime());
							maps.entity.put(areaa, item);
							List<BlockPos> aaa = new ArrayList<>();
							BlockPatternInfo info = recipe.ritual;
							for (int x = 0; x < pass.getWidth(); x++) {
								for (int y = 0; y < pass.getHeight(); y++) {
									if(info.getInfoAtPos(x,y,0).particleSource()){
										aaa.add(pass.translate(x,y,0).getBlockPos());
									}
								}
							}
							maps.parts.put(areaa, aaa);
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
