package ru.otus.appcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();
    private final Map<Class<?>, Object> appComponentsByClass = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
//        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) throws Exception {
//        checkConfigClass(configClass);
//        List<Method> methods = Arrays.stream(configClass.getDeclaredMethods())
//                                     .filter(method -> method.isAnnotationPresent(AppComponent.class))
//                                     .sorted(Comparator.comparingInt(method -> method.getAnnotation(AppComponent.class).order()))
//                                     .toList();
//
//        Object configInstance = createConfigInstance(configClass);
//
//        for (Method method : methods) {
//            AppComponent annotation = method.getAnnotation(AppComponent.class);
//            String name = annotation.name();
//
//
//        }

    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private Object createConfigInstance(Class<?> configClass)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<?> constructor = configClass.getDeclaredConstructor();
        return constructor.newInstance();
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        return null;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return null;
    }

}
