package ninjaphenix.container_library.test;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    public static final Logger LOGGER = LogManager.getLogger("Container-Lib-Test");
    private static BlockEntityType<InventoryTestBlockEntity> blockEntityType;

    public static BlockEntityType<InventoryTestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    public static void initialize() {
        LOGGER.debug("Registering test inventory blocks.");
        var blocks = new ListBuilder<>(Main::register).range(27, 135, 27).single(18).build().toArray(InventoryTestBlock[]::new);
        blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> new InventoryTestBlockEntity(Main.getBlockEntityType(), pos, state, 0), blocks).build();
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("test", "block_entity_type"), blockEntityType);
    }

    private static InventoryTestBlock register(Integer integer) {
        InventoryTestBlock block = new InventoryTestBlock(BlockBehaviour.Properties.of(Material.BAMBOO), integer);
        Registry.register(Registry.BLOCK, new ResourceLocation("test", "block" + integer), block);
        BlockItem item = new BlockItem(block, new Item.Properties());
        Registry.register(Registry.ITEM, new ResourceLocation("test", "block" + integer), item);
        return block;
    }
}
