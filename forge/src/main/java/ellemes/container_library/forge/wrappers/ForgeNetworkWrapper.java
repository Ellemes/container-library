package ellemes.container_library.forge.wrappers;

import ellemes.container_library.CommonMain;
import ellemes.container_library.Utils;
import ellemes.container_library.forge.OpenInventoryMessage;
import ellemes.container_library.inventory.ServerScreenHandlerFactory;
import ellemes.container_library.wrappers.NetworkWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public final class ForgeNetworkWrapper extends NetworkWrapper {
    private final SimpleChannel channel;

    public ForgeNetworkWrapper() {
        String channelVersion = "1.0";
        this.channel = NetworkRegistry.ChannelBuilder.named(Utils.id("channel"))
                .networkProtocolVersion(() -> channelVersion)
                .clientAcceptedVersions(channelVersion::equals)
                .serverAcceptedVersions(channelVersion::equals)
                .simpleChannel();
        channel.registerMessage(0, OpenInventoryMessage.class, OpenInventoryMessage::encode, OpenInventoryMessage::decode, this::handleForgeMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handleForgeMessage(OpenInventoryMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer sender = contextSupplier.get().getSender();
        FriendlyByteBuf buffer = message.getData();
        this.s_handleOpenInventory(sender, buffer);
        contextSupplier.get().setPacketHandled(true);
    }

    @Override
    protected void openScreenHandler(ServerPlayer player, Container inventory, ServerScreenHandlerFactory factory, Component title, ResourceLocation forcedScreenType) {
        NetworkHooks.openGui(player, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return title;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return factory.create(syncId, inventory, playerInventory);
            }
        }, buffer -> {
            buffer.writeInt(inventory.getContainerSize());
            if (forcedScreenType != null) {
                buffer.writeResourceLocation(forcedScreenType);
            }
        });
    }

    @Override
    public void c_openBlockInventory(BlockPos pos) {
        channel.sendToServer(new OpenInventoryMessage(buffer -> {
            CommonMain.getNetworkWrapper().writeBlockData(buffer, pos);
        }));
    }

    @Override
    public void c_openEntityInventory(Entity entity) {
        channel.sendToServer(new OpenInventoryMessage(buffer -> {
            CommonMain.getNetworkWrapper().writeEntityData(buffer, entity);
        }));
    }

//    @Override
//    public void c_openItemInventory(int slotId) {
//        channel.sendToServer(new OpenInventoryMessage(buffer -> {
//            CommonMain.getNetworkWrapper().writeItemData(buffer, slotId);
//        }));
//    }

    @Override
    public boolean canOpenInventory(ServerPlayer player, BlockPos pos) {
        return true;
    }
}
