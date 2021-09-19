package ninjaphenix.container_library.wrappers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import ninjaphenix.container_library.Utils;
import ninjaphenix.container_library.api.OpenableBlockEntityProvider;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.client.gui.PickScreen;
import ninjaphenix.container_library.inventory.ServerScreenHandlerFactory;
import ninjaphenix.container_library.network.OpenInventoryMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class NetworkWrapperImpl extends NetworkWrapper {
    private SimpleChannel channel;

    public void initialise() {
        String channelVersion = "1";
        channel = NetworkRegistry.newSimpleChannel(Utils.id("channel"), () -> channelVersion, channelVersion::equals, channelVersion::equals);

        channel.registerMessage(0, OpenInventoryMessage.class, OpenInventoryMessage::encode, OpenInventoryMessage::decode, OpenInventoryMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public void c_openInventoryAt(BlockPos pos) {
        Client.openInventoryAt(pos);
    }

    @Override
    protected void openScreenHandler(ServerPlayer player, BlockPos pos, Container inventory, ServerScreenHandlerFactory factory, Component displayName) {
        NetworkHooks.openGui(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return displayName;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                return factory.create(windowId, inventory, playerInventory);
            }
        }, buffer -> buffer.writeInt(inventory.getContainerSize()));
    }

    public void handleOpenInventory(BlockPos pos, ServerPlayer player) {
        this.openScreenHandlerIfAllowed(pos, player);
    }

    private static class Client {
        private static void openInventoryAt(BlockPos pos) {
            if (ConfigWrapper.getInstance().getPreferredScreenType().equals(Utils.UNSET_SCREEN_TYPE)) {
                Minecraft.getInstance().setScreen(new PickScreen(() -> Client.openInventoryAt(pos)));
            } else {
                ClientPacketListener listener = Minecraft.getInstance().getConnection();
                if (listener != null && NetworkWrapper.getInstance().toInternal().channel.isRemotePresent(listener.getConnection())) {
                    Player player = Minecraft.getInstance().player;
                    Level world = player.getCommandSenderWorld();
                    BlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof OpenableBlockEntityProvider provider) {
                        int invSize = provider.getOpenableBlockEntity(world, state, pos).getInventory().getContainerSize();
                        ResourceLocation preference = ConfigWrapper.getInstance().getPreferredScreenType();
                        if (AbstractScreen.getScreenSize(preference, invSize, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight()) != null) {
                            NetworkWrapper.getInstance().toInternal().channel.sendToServer(new OpenInventoryMessage(pos));
                        } else {
                            player.displayClientMessage(Utils.translation("generic.ninjaphenix_container_lib.label").withStyle(ChatFormatting.GOLD).append(Utils.translation("chat.ninjaphenix_container_lib.cannot_display_screen", Utils.translation("screen." + preference.getNamespace() + "." + preference.getPath() + "_screen")).withStyle(ChatFormatting.WHITE)), false);
                        }
                    }
                }
            }
        }
    }
}
