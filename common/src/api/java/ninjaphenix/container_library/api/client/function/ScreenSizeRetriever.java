package ninjaphenix.container_library.api.client.function;

public interface ScreenSizeRetriever {
    ScreenSize get(int slots, int scaledWidth, int scaledHeight);
}
