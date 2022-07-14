package ellemes.container_library.forge.mixin;

import ellemes.container_library.client.gui.PageScreen;
import ellemes.container_library.client.gui.ScrollScreen;
import ellemes.container_library.client.gui.SingleScreen;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.quark.api.IQuarkButtonAllowed;

@Mixin({PageScreen.class, SingleScreen.class, ScrollScreen.class})
public class QuarkButtonAllowedMixin implements IQuarkButtonAllowed {
}
