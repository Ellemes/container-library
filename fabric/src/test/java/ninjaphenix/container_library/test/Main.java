package ninjaphenix.container_library.test;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    private static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("test:test");
    private static BlockEntityType<InventoryTestBlockEntity> blockEntityType;
    private static CreativeModeTab creativeTab;

    public static BlockEntityType<InventoryTestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    // Called by fabric loader.
    @SuppressWarnings("unused")
    public static void initialize() {
        creativeTab = FabricItemGroupBuilder.create(new ResourceLocation("test", "test")).build();
        var lang = JLang.lang().itemGroup(new ResourceLocation("test", "test"), "Test Inventory Blocks");
        InventoryTestBlock[] blocks = new ListBuilder<>(i -> Main.register(i, lang)).range(3, 270, 3)
                                                                       .build()
                                                                       .toArray(InventoryTestBlock[]::new);
        RESOURCE_PACK.addLang(new ResourceLocation("test", "en_us"), lang);
        blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> new InventoryTestBlockEntity(Main.getBlockEntityType(), pos, state, 0), blocks).build();
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("test", "block_entity_type"), blockEntityType);

        RRPCallback.AFTER_VANILLA.register(resources -> resources.add(RESOURCE_PACK));
    }

    private static InventoryTestBlock register(Integer integer, JLang lang) {
        ResourceLocation id = new ResourceLocation("test", "block" + integer);
        InventoryTestBlock block = new InventoryTestBlock(BlockBehaviour.Properties.of(Material.BAMBOO), integer);
        Registry.register(Registry.BLOCK, id, block);
        lang.blockRespect(block, "Inventory " + integer);

        RESOURCE_PACK.addBlockState(JState.state(JState.variant(JState.model(new ResourceLocation(id.getNamespace(), "block/" + id.getPath())))), id);
        RESOURCE_PACK.addModel(JModel.model("minecraft:block/orientable")
                                     .textures(JModel.textures()
                                                     .var("top", "test:block/blockn")
                                                     .var("front", "test:block/block" + integer)
                                                     .var("side", "test:block/block" + integer)),
                new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));

        RESOURCE_PACK.addModel(JModel.model(id.getNamespace() + ":block/" + id.getPath()), new ResourceLocation(id.getNamespace(), "item/" + id.getPath()));
        RESOURCE_PACK.addTexture(new ResourceLocation("test", "block/block" + integer), Main.generateTexture(integer));

        BlockItem item = new BlockItem(block, new Item.Properties().tab(creativeTab));
        Registry.register(Registry.ITEM, id, item);
        return block;
    }

    private static BufferedImage generateTexture(Integer integer) {
        try {
            BufferedImage numbers = ImageIO.read(Files.newInputStream(FabricLoader.getInstance().getModContainer("ninjaphenix_container_lib_test").get().getPath("assets/test/textures/gen/numbers.png")));
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY);
            Graphics graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 16, 16);
            String characters = integer.toString();
            for (int i = 0; i < characters.length(); i++) {
                String letter = characters.substring(i, i+1);
                BufferedImage number = numbers.getSubimage(1, Main.getNumberOffset(letter.charAt(0)), 3, 5);
                graphics.drawImage(number, 1 + 4 * i, 1, 3, 5, null);
            }
            return image;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find numbers.png");
        }
    }

    private static int getNumberOffset(char character) {
        if (character == '1') {
            return 1;
        } else if (character == '2') {
            return 7;
        } else if (character == '3') {
            return 13;
        } else if (character == '4') {
            return 19;
        } else if (character == '5') {
            return 25;
        } else if (character == '6') {
            return 31;
        } else if (character == '7') {
            return 37;
        } else if (character == '8') {
            return 43;
        } else if (character == '9') {
            return 49;
        } else if (character == '0') {
            return 55;
        }
        throw new IllegalArgumentException("character must be a single number");
    }
}
