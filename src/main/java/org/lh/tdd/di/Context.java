package org.lh.tdd.di;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.util.Arrays.stream;

public class Context {
    private Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, (Provider<Type>) () -> instance);
    }

    public <Type,Implementation extends Type> void bind(Class<Type> type, Class<Implementation> implementationClass) {
        Constructor<Implementation> constructor = getInjectConstructor(implementationClass);
        providers.put(type, new ConstructorInjectionProvider(constructor));
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

    public <Type> Optional<Type> get(Class<Type> type) {
        return (Optional<Type>) Optional.ofNullable(providers.get(type)).map(provider -> ((Type) provider.get()));
    }

    public  class ConstructorInjectionProvider<T> implements Provider<T> {

        private Constructor<T> constructor;

        private boolean constructing = false;
        public ConstructorInjectionProvider(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T get() {
            if (this.constructing){
                throw new CyclicDependenciesFoundException();
            }
            this.constructing = true;
            try {
                Object[] dependencies = stream(constructor.getParameters())
                        .map(p -> Context.this.get(p.getType()).orElseThrow(DependencyNotFoundException::new))
                        .toArray(Object[]::new);
                return constructor.newInstance(dependencies);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                this.constructing = false;
            }
        }
    }
}
