package ellemes.container_library.thread.mixin;

import ellemes.container_library.CommonClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Minecraft.class)
public class BeforeUseMixin {
    @Inject(
            method = "startUseItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void ecl_beforeUse(CallbackInfo ci, InteractionHand[] hands, int handsLength, int handIndex, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            Minecraft client = (Minecraft) (Object) this;
            if (CommonClient.tryOpenSpectatorInventory(client.level, client.player, client.hitResult, hand)) {
                ci.cancel();
            }
        }
    }
}
