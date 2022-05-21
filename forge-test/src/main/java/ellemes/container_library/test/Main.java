package ellemes.container_library.test;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod("ellemes_container_lib_test")
public final class Main {
    private static BlockEntityType<InventoryTestBlockEntity> blockEntityType;

    public Main() {
        CreativeModeTab group = new CreativeModeTab("ellemes_container_lib_test.tab") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.SOUL_CAMPFIRE);
            }
        };

        List<Supplier<InventoryTestBlock>> blocks = new ArrayList<>();
        List<Supplier<BlockItem>> items = new ArrayList<>();
        IntArrayList list = new IntArrayList(100);
        for (int j = 1; j < 20; j++) {
            list.add(j * 27 - 3);
            list.add(j * 27);
            list.add(j * 27 + 3);
        }

        for (int j : list) {
            ResourceLocation id = new ResourceLocation("ellemes_container_lib_test", "inventory_" + j);
            Supplier<InventoryTestBlock> block = () -> {
                var b = new InventoryTestBlock(BlockBehaviour.Properties.of(Material.BAMBOO), j);
                b.setRegistryName(id);
                return b;
            };
            Supplier<BlockItem> item = () -> {
                var i = new BlockItem(block.get(), new Item.Properties().tab(group));
                i.setRegistryName(id);
                return i;
            };
            blocks.add(block);
            items.add(item);
        }

        Supplier<BlockEntityType<InventoryTestBlockEntity>> bets = () -> {
            var be = new BlockEntityType<>(((pos, state) -> new InventoryTestBlockEntity(Main.getBlockEntityType(), pos, state)), blocks.stream().map(Supplier::get).collect(Collectors.toSet()), null);
            be.setRegistryName(new ResourceLocation("ellemes_container_lib_test", "block_entity_type"));
            return be;
        };
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addGenericListener(Block.class, (RegistryEvent.Register<Block> event) -> {
            IForgeRegistry<Block> registry = event.getRegistry();
            blocks.forEach(b -> registry.register(b.get()));
        });

        bus.addGenericListener(Item.class, (RegistryEvent.Register<Item> event) -> {
            IForgeRegistry<Item> registry = event.getRegistry();
            items.forEach(i -> registry.register(i.get()));
        });

        bus.addGenericListener(BlockEntityType.class, (RegistryEvent.Register<BlockEntityType<?>> event) -> {
            IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
            registry.register(blockEntityType = bets.get());

        });
    }

    public static BlockEntityType<InventoryTestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }
}
