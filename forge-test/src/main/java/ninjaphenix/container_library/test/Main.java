package ninjaphenix.container_library.test;

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
import java.util.Set;

@Mod("ninjaphenix_container_lib_test")
public final class Main {
    private static BlockEntityType<InventoryTestBlockEntity> blockEntityType;

    public Main() {
        CreativeModeTab group = new CreativeModeTab("test.test") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(Blocks.SOUL_CAMPFIRE);
            }
        };

        List<Block> blocks = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        IntArrayList list = new IntArrayList(100);
        for (int i = 1; i < 20; i++) {
            list.add(i * 27 - 3);
            list.add(i * 27);
            list.add(i * 27 + 3);
        }

        for (int i : list) {
            ResourceLocation id = new ResourceLocation("test", "inventory_" + i);
            InventoryTestBlock block = new InventoryTestBlock(BlockBehaviour.Properties.of(Material.BAMBOO), i);
            block.setRegistryName(id);
            Item item = new BlockItem(block, new Item.Properties().tab(group));
            item.setRegistryName(id);
            blocks.add(block);
            items.add(item);
        }

        blockEntityType = new BlockEntityType<>(((pos, state) -> new InventoryTestBlockEntity(Main.getBlockEntityType(), pos, state)), Set.copyOf(blocks), null);
        blockEntityType.setRegistryName(new ResourceLocation("test", "block_entity_type"));
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addGenericListener(Block.class, (RegistryEvent.Register<Block> event) -> {
            IForgeRegistry<Block> registry = event.getRegistry();
            blocks.forEach(registry::register);
        });

        bus.addGenericListener(Item.class, (RegistryEvent.Register<Item> event) -> {
            IForgeRegistry<Item> registry = event.getRegistry();
            items.forEach(registry::register);
        });

        bus.addGenericListener(BlockEntityType.class, (RegistryEvent.Register<BlockEntityType<?>> event) -> {
            IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
            registry.register(blockEntityType);
        });
    }

    public static BlockEntityType<?> getBlockEntityType() {
        return blockEntityType;
    }
}
