package org.souffle.utils;

import org.souffle.standard.QueryException;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 下午4:04
 * @see
 * @since JDK1.7
 */
public final class ClassUtils {

    public static <T> T newInstance(String className, Class<T> clazz, Class<?>[] parameterTypes, Object[] parameters) {
        Objects.requireNonNull(className, "CLass name is null.");
        Objects.requireNonNull(clazz, "CLass type is null.");
        Objects.requireNonNull(parameterTypes, "Parameter types is null.");
        try {
            Class<?> clz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Constructor<?> constructor = clz.getConstructor(parameterTypes);
            Object obj = constructor.newInstance(parameters);
            return clazz.cast(obj);
        } catch (Throwable t) {
            throw new QueryException("Cannot create " + clazz.getSimpleName() + " object with class name " + className, t);
        }
    }
}
