package ninjaphenix.container_library.test;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod("ninjaphenix_container_lib_test")
public final class Main {
    private static TileEntityType<InventoryTestBlockEntity> blockEntityType;

    public Main() {
        ItemGroup group = new ItemGroup("test.test") {
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
            InventoryTestBlock block = new InventoryTestBlock(AbstractBlock.Properties.of(Material.BAMBOO), i);
            block.setRegistryName(id);
            Item item = new BlockItem(block, new Item.Properties().tab(group));
            item.setRegistryName(id);
            blocks.add(block);
            items.add(item);
        }

        blockEntityType = new TileEntityType<>((() -> new InventoryTestBlockEntity(Main.getBlockEntityType(), 0)), ImmutableSet.copyOf(blocks), null);
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

        bus.addGenericListener(TileEntityType.class, (RegistryEvent.Register<TileEntityType<?>> event) -> {
            IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
            registry.register(blockEntityType);
        });
    }

    public static TileEntityType<?> getBlockEntityType() {
        return blockEntityType;
    }
}
