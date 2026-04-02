package project.core.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * The {@code DataFactoryRegistry} class was originally intended to serve as a centralized
 * factory and registry for all {@link Data} subclasses used throughout the project.
 * Each data type would be registered here, and all instances would be created via
 * factory functions provided by this registry. 
 *
 * This approach was designed to improve consistency and decouple object creation from
 * concrete class references, effectively providing a unified entry point for all data
 * instantiations. However, due to time constraints and the limited practical benefits
 * beyond cleaner architecture, this system was not fully implemented.
 *
 * As of now, the registry is only utilized for testing purposes—specifically in {@link TreeTester}.
 */
public class DataFactoryRegistry {
    private static final Map<Class<?>, IntFunction<? extends Data<?>>> INT_REGISTRY = new HashMap<>();

    static {
        INT_REGISTRY.put(TreeTestingData.class, TreeTestingData::new);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Data<T>> IntFunction<T> getFactory(Class<?> dataType) {
        return (IntFunction<T>) INT_REGISTRY.get(dataType);
    }
}
