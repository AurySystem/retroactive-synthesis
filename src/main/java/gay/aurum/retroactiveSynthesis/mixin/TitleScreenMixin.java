package gay.aurum.retroactiveSynthesis.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRegion.class)
public class TitleScreenMixin {

	@Final
	@Shadow
	private ServerWorld world;


	private int retroactiveSynthesis$wrap(int val){

		return (-val) - (-world.getTopY() - world.getBottomY());

	}

//	@ModifyVariable(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"),ordinal = 0)
//	private BlockPos retroactiveSynthesis$setblock(BlockPos value) {
//		return new BlockPos(value.getX(),retroactiveSynthesis$wrap(value.getY()),value.getZ());
//	}
//	@ModifyVariable(method = "getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At("HEAD"),ordinal = 0)
//	private BlockPos retroactiveSynthesis$getblock(BlockPos value) {
//		return new BlockPos(value.getX(),retroactiveSynthesis$wrap(value.getY()),value.getZ());
//	}
//	@ModifyVariable(method = "getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;", at = @At("HEAD"),ordinal = 0)
//	private BlockPos retroactiveSynthesis$getfluid(BlockPos value) {
//		return new BlockPos(value.getX(),retroactiveSynthesis$wrap(value.getY()),value.getZ());
//	}

	@Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at= @At("RETURN"))
	private void retroactiveSynthesis$flipways(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create, CallbackInfoReturnable<Chunk> cir){
		Chunk chunk = cir.getReturnValue();
		if (chunk != null){
			if(/*chunk.getStatus().equals(ChunkStatus.FULL)*/true){
				ChunkSection[] sections = chunk.getSectionArray();
				ChunkSection[] copy = new ChunkSection[sections.length];
				for (int i = 0; i < sections.length;i++) {
					copy[i] = sections[sections.length-(i+1)];
					//sections[i].getContainer();
				}
				for (int i = 0; i < sections.length;i++) {
					sections[i] = copy[i];
				}
				chunk.setNeedsSaving(true);
			}
		}
	}

}

