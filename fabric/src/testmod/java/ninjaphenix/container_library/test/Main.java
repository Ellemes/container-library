package ninjaphenix.container_library.test;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninjaphenix.container_library.api.client.gui.AbstractScreen;
import ninjaphenix.container_library.wrappers.PlatformUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;

public class Main implements ModInitializer {
    private static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("test:test");
    private static BlockEntityType<InventoryTestBlockEntity> blockEntityType;
    private static ItemGroup group;

    public static BlockEntityType<InventoryTestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    // todo: print out warning if dependency version in fabric.mod.json is outdated.
    @SuppressWarnings("unused")
    @Override
    public void onInitialize() {
        group = FabricItemGroupBuilder.create(new Identifier("test", "test")).build();
        JLang lang = JLang.lang().itemGroup(new Identifier("test", "test"), "Test Inventory Blocks");
        InventoryTestBlock[] blocks = new ListBuilder<>(i -> Main.register(i, lang)).range(27, 540, 27)
                                                                         .range(24, 540, 27)
                                                                         .range(30, 540, 27)
                                                                         .build().toArray(new InventoryTestBlock[0]);
        RESOURCE_PACK.addLang(new Identifier("test", "en_us"), lang);
        blockEntityType = BlockEntityType.Builder.create(() -> new InventoryTestBlockEntity(Main.getBlockEntityType(), 0), blocks).build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("test", "block_entity_type"), blockEntityType);

        RRPCallback.AFTER_VANILLA.register(resources -> resources.add(RESOURCE_PACK));

        if (PlatformUtils.isClient()) {
            Main.setDebugRenderEnabled();
        }
    }

    private static InventoryTestBlock register(int inventorySize, JLang lang) {
        Identifier id = new Identifier("test", "block" + inventorySize);
        InventoryTestBlock block = new InventoryTestBlock(AbstractBlock.Settings.of(Material.BAMBOO), inventorySize);
        Registry.register(Registry.BLOCK, id, block);
        lang.blockRespect(block, "Inventory " + inventorySize);

        RESOURCE_PACK.addBlockState(JState.state(JState.variant(JState.model(new Identifier(id.getNamespace(), "block/" + id.getPath())))), id);
        RESOURCE_PACK.addModel(JModel.model("minecraft:block/orientable")
                                     .textures(JModel.textures()
                                                     .var("top", "test:block/blockn")
                                                     .var("front", "test:block/block" + inventorySize)
                                                     .var("side", "test:block/block" + inventorySize)),
                new Identifier(id.getNamespace(), "block/" + id.getPath()));

        RESOURCE_PACK.addModel(JModel.model(id.getNamespace() + ":block/" + id.getPath()), new Identifier(id.getNamespace(), "item/" + id.getPath()));
        RESOURCE_PACK.addTexture(new Identifier("test", "block/block" + inventorySize), Main.generateTexture(inventorySize));

        BlockItem item = new BlockItem(block, new Item.Settings().group(group));
        Registry.register(Registry.ITEM, id, item);
        return block;
    }

    private static BufferedImage generateTexture(int inventorySize) {
        try {
            //noinspection OptionalGetWithoutIsPresent
            BufferedImage numbers = ImageIO.read(Files.newInputStream(FabricLoader.getInstance().getModContainer("ninjaphenix_container_lib").get().getPath("assets/test/textures/gen/numbers.png")));
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY);
            Graphics graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 16, 16);
            String characters = Integer.toString(inventorySize);
            for (int i = 0; i < characters.length(); i++) {
                String letter = characters.substring(i, i + 1);
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

    @Environment(EnvType.CLIENT)
    private static void setDebugRenderEnabled() {
        AbstractScreen.DEBUG_RENDER = true;
    }
}
