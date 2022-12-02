package gay.aurum.retroactiveSynthesis;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.function.Predicate;

public class BlockPatternInfo extends BlockPattern {

	private final ExtraBlockInfo[][][] info;

	public BlockPatternInfo(Predicate<CachedBlockPosition>[][][] pattern, ExtraBlockInfo[][][] info) {
		super(pattern);
		this.info = info;
	}

	@VisibleForTesting
	public ExtraBlockInfo[][][] getInfo() {
		return this.info;
	}

	public ExtraBlockInfo getInfoAtPos(int x, int y, int z){
		return this.info[x][y][z];
	}

	public ExtraBlockInfo getInfoAtPos(Vec3i pos){
		return this.info[pos.getX()][pos.getY()][pos.getZ()];
	}

	public ExtraBlockInfo getInfoAtPos(BlockPos pos){
		return this.info[pos.getX()][pos.getY()][pos.getZ()];
	}
	public record ExtraBlockInfo(boolean preserve, boolean particleSource) {
	}

}
