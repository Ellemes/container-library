package ninjaphenix.container_library.api.client.function;

public final class ScreenSize {
    private final int width, height;

    private ScreenSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static ScreenSize of(int width, int height) {
        return new ScreenSize(width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
