package ru.otus.appcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings({"squid:S1068", "java:S112", "java:S3011"})
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... configClasses) {
        processConfig(Arrays.stream(configClasses));
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);

        if (configClasses.isEmpty()) {
            throw new RuntimeException(String.format("No config classes found in package: %s", packageName));
        }

        processConfig(configClasses.stream());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> candidates = appComponents.stream()
                .filter(component -> componentClass.isAssignableFrom(component.getClass()))
                .toList();

        if (candidates.isEmpty()) {
            throw new RuntimeException(String.format("Component not found for class: %s", componentClass.getName()));
        }

        if (candidates.size() > 1) {
            throw new RuntimeException(String.format(
                    "Multiple components found for class:  %s. Use getAppComponent(String name) instead.",
                    componentClass.getName()));
        }

        return (C) candidates.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        C component = (C) appComponentsByName.get(componentName);

        if (component == null) {
            throw new RuntimeException(String.format("Component not found: %s", componentName));
        }
        return component;
    }

    private void processConfig(Stream<Class<?>> configClasses) {
        configClasses
                .sorted(Comparator.comparingInt(cls ->
                        cls.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        List<Method> methods = Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(
                        method -> method.getAnnotation(AppComponent.class).order()))
                .toList();

        try {
            Object configInstance = createConfigInstance(configClass);

            for (Method method : methods) {
                AppComponent annotation = method.getAnnotation(AppComponent.class);
                String name = annotation.name();

                if (appComponentsByName.containsKey(name)) {
                    throw new RuntimeException(String.format("Component with name %s already exists", name));
                }

                Parameter[] parameters = method.getParameters();
                Object[] args = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Class<?> parameterType = parameters[i].getType();
                    Object component = getAppComponent(parameterType);
                    args[i] = component;
                }

                Object component = method.invoke(configInstance, args);

                appComponents.add(component);
                appComponentsByName.put(name, component);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to process config class: %s", configClass.getName()), e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private Object createConfigInstance(Class<?> configClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<?> constructor = configClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
