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

    interface Component{
    }

    class ComponentWithDefaultConstructor implements Component{
        public ComponentWithDefaultConstructor() {
        }
    }


    interface Dependency{ }

    class ComponentWithInjectConstructor implements Component{
        private Dependency dependency;

        @Inject
        public ComponentWithInjectConstructor(Dependency dependency) {
            this.dependency = dependency;
        }

        public Dependency getDependency() {
            return dependency;
        }
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


            //todo a -> b -> c
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

