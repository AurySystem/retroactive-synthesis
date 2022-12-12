package gay.aurum.retroactiveSynthesis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BlockPatternInfo extends BlockPattern {

	private final ExtraBlockInfo[][][] info;

	public BlockPatternInfo(Predicate<CachedBlockPosition>[][][] pattern, ExtraBlockInfo[][][] info) {
		super(pattern);
		this.info = info;
	}

	public ExtraBlockInfo[][][] getInfo() {
		return this.info;
	}

	public ExtraBlockInfo getInfoAtPos(int x, int y, int z){
		return this.info[z][y][x];
	}

	public ExtraBlockInfo getInfoAtPos(Vec3i pos){
		return this.info[pos.getZ()][pos.getY()][pos.getX()];
	}

	public record ExtraBlockInfo(boolean consume, BlockState remainder, boolean particleSource) {
	}

	public static class builder{

		private final List<String[]> layers = Lists.newArrayList();
		private final Map<Character, ExtraBlockInfo> charMap = Maps.newHashMap();
		private int height = 0;
		private int width = 0;
		builder(){
			this.charMap.put(' ', new ExtraBlockInfo(false, Blocks.AIR.getDefaultState(), false));}

		public builder layer(String... layers){
			if(this.height == 0){
				this.height = layers.length;
				this.width = layers[0].length();
			}
			if(this.height != layers.length || this.width != layers[0].length()){
				return null;
			}
			this.layers.add(layers);
			return this;
		}


		public builder key(Character key, ExtraBlockInfo value){
			this.charMap.put(key, value);
			return this;
		}

		public BlockPatternInfo build(BlockPattern in){
			ExtraBlockInfo[][][] info = new ExtraBlockInfo[this.layers.size()][this.height][this.width];

			for(int z = 0; z < this.layers.size(); ++z) {
				for(int y = 0; y < this.height; ++y) {
					for(int x = 0; x < this.width; ++x) {
						info[z][y][x] = this.charMap.get(((String[])this.layers.get(z))[y].charAt(x));
					}
				}
			}

			return new BlockPatternInfo(in.getPattern(), info);
		}

	}

}
