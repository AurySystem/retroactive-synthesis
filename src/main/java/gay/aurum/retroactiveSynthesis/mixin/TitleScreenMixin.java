package gay.aurum.retroactiveSynthesis.mixin;

import gay.aurum.retroactiveSynthesis.DuckShuffle;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registry.class)
public class TitleScreenMixin {


	@Inject(method = "freezeBuiltins()V", at = @At("HEAD"))
	private static void retroactiveSynthesis$flipways(CallbackInfo cir){
		if(!((DuckShuffle)(Registry.ITEM)).hasRun()){
			((DuckShuffle)(Registry.ITEM)).shuffleAll();
		}
	}

}

