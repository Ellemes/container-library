package ninjaphenix.container_library.test;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class Main {
    private static BlockEntityType<InventoryTestBlockEntity> blockEntityType;
    private static CreativeModeTab creativeTab;

    public static BlockEntityType<InventoryTestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    public static void initialize() {
        creativeTab = FabricItemGroupBuilder.create(new ResourceLocation("test",  "test")).build();

        InventoryTestBlock[] blocks = new ListBuilder<>(Main::register).range(27, 270, 27).single(18).build().toArray(InventoryTestBlock[]::new);
        blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> new InventoryTestBlockEntity(Main.getBlockEntityType(), pos, state, 0), blocks).build();
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("test", "block_entity_type"), blockEntityType);
    }

    private static InventoryTestBlock register(Integer integer) {
        InventoryTestBlock block = new InventoryTestBlock(BlockBehaviour.Properties.of(Material.BAMBOO), integer);
        Registry.register(Registry.BLOCK, new ResourceLocation("test", "block" + integer), block);
        BlockItem item = new BlockItem(block, new Item.Properties().tab(creativeTab));
        Registry.register(Registry.ITEM, new ResourceLocation("test", "block" + integer), item);
        return block;
    }
}
