package ellemes.container_library.api.v3.context;

import net.minecraft.world.item.ItemStack;

public interface ItemContext extends Context {
    ItemStack getItemStack();
}
