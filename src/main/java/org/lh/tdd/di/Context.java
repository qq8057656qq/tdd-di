package org.lh.tdd.di;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.Arrays.stream;

public class Context {
    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type> Type get(Class<Type> type) {
        return (Type) providers.get(type).get();
    }

    public <Type, Implementation extends Type> void bind(Class<Type> type, Class<Implementation> implementationClass) {
        Constructor<Implementation> constructor = getInjectConstructor(implementationClass);
        providers.put(type, (Provider<Type>) () -> {
            try {
                Object[] dependencies = stream(constructor.getParameters())
                        .map(p -> get(p.getType()))
                        .toArray(Object[]::new);
                return (Type) constructor.newInstance(dependencies);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <Type> Constructor<Type> getInjectConstructor(Class<Type> implementationClass) {
        List<Constructor<?>> constructors = stream(implementationClass.getConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
        if (constructors.size() > 1){
            throw new IllegalComponentException();
        }
        return (Constructor<Type>) constructors.stream().findFirst().orElseGet(() -> {
            try {
                return implementationClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalComponentException();
            }
        });
    }
}
