package ninjaphenix.container_library.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.OpenableBlockEntityProvider;
import ninjaphenix.container_library.internal.api.inventory.ServerMenuFactory;
import ninjaphenix.container_library.inventory.PageMenu;
import ninjaphenix.container_library.inventory.ScrollMenu;
import ninjaphenix.container_library.inventory.SingleMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class NetworkWrapper {
    final Map<UUID, ResourceLocation> playerPreferences = new HashMap<>();
    final Map<ResourceLocation, ServerMenuFactory> menuFactories = Map.of(Utils.PAGE_SCREEN_TYPE, PageMenu::new,
            Utils.SCROLL_SCREEN_TYPE, ScrollMenu::new, Utils.SINGLE_SCREEN_TYPE, SingleMenu::new);

    public static NetworkWrapper getInstance() {
        return NetworkWrapperImpl.getInstance();
    }

    public abstract void initialise();

    public abstract void c2s_sendTypePreference(ResourceLocation selection);

    public final void s_setPlayerScreenType(ServerPlayer player, ResourceLocation selection) {
        UUID uuid = player.getUUID();
        if (menuFactories.containsKey(selection)) {
            playerPreferences.put(uuid, selection);
        } else {
            playerPreferences.remove(uuid);
        }
    }

    public abstract Set<ResourceLocation> getScreenOptions();

    public abstract void c_openInventoryAt(BlockPos pos);

    public abstract void c_openInventoryAt(BlockPos pos, ResourceLocation selection);

    protected final void openMenuIfAllowed(BlockPos pos, ServerPlayer player) {
        UUID uuid = player.getUUID();
        ResourceLocation playerPreference;
        if (playerPreferences.containsKey(uuid) && menuFactories.containsKey(playerPreference = playerPreferences.get(uuid))) {
            var level = player.getLevel();
            var state = level.getBlockState(pos);
            if (state.getBlock() instanceof OpenableBlockEntityProvider block) {
                var inventory = block.getOpenableBlockEntity(level, state, pos);
                var displayName = inventory.getInventoryName();
                if (player.containerMenu == null || player.containerMenu == player.inventoryMenu) {
                    if (inventory.canBeUsedBy(player)) {
                        block.onInitialOpen(player);
                    } else {
                        player.displayClientMessage(new TranslatableComponent("container.isLocked", displayName), true);
                        player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return;
                    }
                }
                if (!inventory.canContinueUse(player)) {
                    return;
                }
                this.openMenu(player, pos, inventory.getInventory(), menuFactories.get(playerPreference), displayName);
            }
        }
    }

    protected abstract void openMenu(ServerPlayer player, BlockPos pos, Container container, ServerMenuFactory factory, Component displayName);
}
