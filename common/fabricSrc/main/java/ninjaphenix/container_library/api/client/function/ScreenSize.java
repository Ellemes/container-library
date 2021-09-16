package ninjaphenix.container_library.api.client.function;

import net.minecraft.client.MinecraftClient;

@SuppressWarnings("ClassCanBeRecord")
public final class ScreenSize {
    private final int width, height;

    private ScreenSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static ScreenSize of(int width, int height) {
        return new ScreenSize(width, height);
    }

    public static ScreenSize current() {
        return new ScreenSize(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
