package ellemes.container_library;

import ellemes.container_library.api.inventory.AbstractHandler;
import ellemes.container_library.inventory.ClientScreenHandlerFactory;
import ellemes.container_library.wrappers.NetworkWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import java.util.function.BiFunction;

public final class CommonMain {
    public static final Logger LOGGER = LogManager.getLogger(Utils.MOD_ID);
    private static MenuType<AbstractHandler> screenHandlerType;
    private static NetworkWrapper networkWrapper;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void initialize(BiFunction<ResourceLocation, ClientScreenHandlerFactory, MenuType> handlerTypeFunction,
                                  NetworkWrapper networkWrapper) {
        CommonMain.screenHandlerType = handlerTypeFunction.apply(Utils.HANDLER_TYPE_ID, AbstractHandler::createClientMenu);
        CommonMain.networkWrapper = networkWrapper;
    }

    public static void warnThrowableMessage(String message, Throwable throwable, Object... values) {
        CommonMain.LOGGER.warn(new FormattedMessage(message, values, throwable));
    }

    public static MenuType<AbstractHandler> getScreenHandlerType() {
        return CommonMain.screenHandlerType;
    }

    public static NetworkWrapper getNetworkWrapper() {
        return CommonMain.networkWrapper;
    }
}
