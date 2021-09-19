package ninjaphenix.container_library.wrappers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.OpenableBlockEntity;
import ninjaphenix.container_library.api.OpenableBlockEntityProvider;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;

public abstract class NetworkWrapper {
    private static NetworkWrapper INSTANCE;
    private static Client CLIENT_INSTANCE;

    public abstract void initialise();

    protected abstract void openScreenHandler(ServerPlayer player, BlockPos pos, Container inventory, ServerScreenHandlerFactory factory, Component title);

    public static NetworkWrapper getInstance() {
        if (NetworkWrapper.INSTANCE == null) {
            NetworkWrapper.INSTANCE = new NetworkWrapperImpl();
        }
        return NetworkWrapper.INSTANCE;
    }

    private static Client getClientClassInternal() {
        if (NetworkWrapper.CLIENT_INSTANCE == null) {
            NetworkWrapper.CLIENT_INSTANCE = new NetworkWrapperImpl.Client();
        }
        return NetworkWrapper.CLIENT_INSTANCE;
    }

    public final void c_openInventoryAt(BlockPos pos) {
        NetworkWrapper.getClientClassInternal().openInventoryAt(pos);
    }

    protected final void openScreenHandlerIfAllowed(BlockPos pos, ServerPlayer player) {
        ServerLevel world = player.getLevel();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof OpenableBlockEntityProvider block) {
            OpenableBlockEntity inventory = block.getOpenableBlockEntity(world, state, pos);
            if (inventory != null) {
                Component title = inventory.getInventoryTitle();
                if (player.containerMenu == null || player.containerMenu == player.inventoryMenu) {
                    if (inventory.canBeUsedBy(player)) {
                        block.onInitialOpen(player);
                    } else {
                        player.displayClientMessage(new TranslatableComponent("container.isLocked", title), true);
                        player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return;
                    }
                }
                if (!inventory.canContinueUse(player)) {
                    return;
                }
                this.openScreenHandler(player, pos, inventory.getInventory(), AbstractHandler::new, title);
            }
        }
    }

    public final NetworkWrapperImpl toInternal() {
        return (NetworkWrapperImpl) this;
    }

    protected static abstract class Client {
        abstract void sendOpenInventoryPacket(BlockPos pos);
        abstract boolean canSendOpenInventoryPacket();

        public final void openInventoryAt(BlockPos pos) {
            ResourceLocation preference = ConfigWrapper.getInstance().getPreferredScreenType();
            if (preference.equals(Utils.UNSET_SCREEN_TYPE) || !AbstractScreen.isScreenTypeDeclared(preference)) {
                Minecraft.getInstance().setScreen(new PickScreen(() -> this.openInventoryAt(pos)));
            } else {
                if (this.canSendOpenInventoryPacket()) {
                    Player player = Minecraft.getInstance().player;
                    Level world = player.getCommandSenderWorld();
                    BlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof OpenableBlockEntityProvider provider) {
                        int invSize = provider.getOpenableBlockEntity(world, state, pos).getInventory().getContainerSize();
                        if (AbstractScreen.getScreenSize(preference, invSize, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight()) != null) {
                            this.sendOpenInventoryPacket(pos);
                        } else {
                            player.displayClientMessage(Utils.translation("generic.ninjaphenix_container_lib.label").withStyle(ChatFormatting.GOLD).append(Utils.translation("chat.ninjaphenix_container_lib.cannot_display_screen", Utils.translation("screen." + preference.getNamespace() + "." + preference.getPath() + "_screen")).withStyle(ChatFormatting.WHITE)), false);
                        }
                    }
                }
            }
        }
    }
}
