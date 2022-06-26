package org.lh.tdd.di;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private Map<Class<?>,Object> components = new HashMap<>();
    private Map<Class<?>,Class<?>> componentImplementations = new HashMap<>();

    public <ComponentType> void bind(Class<ComponentType> type, ComponentType instance) {
        components.put(type,instance);
    }

    public <ComponentType> ComponentType get(Class<ComponentType> typeClass) {
        if (components.containsKey(typeClass)){
            return (ComponentType) components.get(typeClass);
        }
        Class<?> implementation = componentImplementations.get(typeClass);
        try {
            return (ComponentType) implementation.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <ComponentType, ComponentImplementation extends ComponentType> void
    bind(Class<ComponentType> typeClass, Class<ComponentImplementation> implementationClass) {
        componentImplementations.put(typeClass,implementationClass);
    }
}
