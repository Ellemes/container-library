package ellemes.container_library.forge.mixin;

import ellemes.container_library.api.client.gui.AbstractScreen;
import ellemes.container_library.client.gui.PageScreen;
import ellemes.container_library.client.gui.ScrollScreen;
import ellemes.container_library.client.gui.SingleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.client.handler.InventoryButtonHandler;

import java.util.function.Predicate;

@Mixin(InventoryButtonHandler.class)
public class QuarkButtonRepositioningMixin {
    @Inject(
            method = "Lvazkii/quark/base/client/handler/InventoryButtonHandler;applyProviders(Lnet/minecraftforge/client/event/ScreenEvent$Init$Post;Lvazkii/quark/base/client/handler/InventoryButtonHandler$ButtonTargetType;Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;Ljava/util/function/Predicate;)V",
            at = @At("HEAD"),
            remap = false,
            require = 0
    )
    private void ecl_modifyEndSlot(ScreenEvent.Init.Post event, InventoryButtonHandler.ButtonTargetType type, AbstractContainerScreen<?> screen, Predicate<Slot> slotPredicate, CallbackInfo ci) {
        if (screen instanceof SingleScreen || screen instanceof ScrollScreen || screen instanceof PageScreen) {
            if (type == InventoryButtonHandler.ButtonTargetType.CONTAINER_INVENTORY) {
                slotPredicate = s -> s.container != Minecraft.getInstance().player.getInventory() && s.getSlotIndex() == ((AbstractScreen) screen).getInventoryWidth() - 1;
            }
        }
    }
}
