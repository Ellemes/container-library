package ninjaphenix.container_library.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import ninjaphenix.container_library.api.v2.OpenableBlockEntityProviderV2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerController.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private ClientPlayNetHandler connection;

    @Inject(
            method = "useItemOn(Lnet/minecraft/client/entity/player/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void ncl_noPacketOnFail(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockRayTraceResult hit,
                                    CallbackInfoReturnable<ActionResultType> cir,
                                    BlockPos pos, ItemStack __itemStack, PlayerInteractEvent.RightClickBlock __event,
                                    ItemUseContext __useOnContext, boolean __flag, boolean __flag1) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof OpenableBlockEntityProviderV2) {
            ActionResultType result = state.use(world, player, hand, hit);
            if (result == ActionResultType.FAIL) {
                player.swing(hand);
            } else {
                this.connection.send(new CPlayerTryUseItemOnBlockPacket(hand, hit));
            }
            cir.setReturnValue(result);
        }
    }
}
