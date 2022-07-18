package org.lh.tdd.di;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {

    private Context context;

    @BeforeEach
    public void setup(){
        context = new Context();
    }
    @Nested
    public class ComponentConstruction{
        // todo instance


        @Test
        void should_bind_type_to_a_specific_instance() {
            context = new Context();
            Component instance = new Component() {
            };
            context.bind(Component.class,instance);
            assertSame(instance,context.get(Component.class));
        }

        // todo abstract class
        // interface
        @Nested
        public class ConstructorInjection{
            @Test
            public void should_bind_type_to_a_class_with_default_constructor(){
                Context context = new Context();
                context.bind(Component.class,ComponentWithDefaultConstructor.class);
                Component instance = context.get(Component.class);
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }
            //todo with dependencies

            @Test
            void should_bind_type_to_a_class_with_inject_constructor() {
                Dependency dependency = new Dependency() {
                };
                context.bind(Component.class,ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);
                Component instance = context.get(Component.class);
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).getDependency());
            }

            @Test
            void should_bind_type_to_a_class_with_transitive_dependencies() {
                context.bind(Component.class,ComponentWithInjectConstructor.class);
                context.bind(Dependency.class,DependencyWithInjectConstructor.class);
                context.bind(String.class,"indirect dependency");
                Component instance = context.get(Component.class);
                assertNotNull(instance);
                Dependency dependency = ((ComponentWithInjectConstructor) instance).getDependency();
                assertNotNull(dependency);
                assertEquals("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());

            }
        }

        @Nested
        public class FieldInjection{

        }

        @Nested
        public class MethodInjection{

        }
    }

    @Nested
    public class DependenciesSelection{

    }

    @Nested
    public class LifecycleManagement{

    }
}


interface Component{
}

class ComponentWithDefaultConstructor implements Component{
    public ComponentWithDefaultConstructor() {
    }
}

interface Dependency{ }

class ComponentWithInjectConstructor implements Component{
    private  Dependency dependency;

    @Inject
    public ComponentWithInjectConstructor(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }
}
class DependencyWithInjectConstructor implements Dependency{
    private String dependency;

    @Inject
    public DependencyWithInjectConstructor(String dependency) {
        this.dependency = dependency;
    }

    public String getDependency() {
        return dependency;
    }
}

