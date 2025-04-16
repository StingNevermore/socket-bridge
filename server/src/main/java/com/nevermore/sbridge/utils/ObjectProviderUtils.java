package com.nevermore.sbridge.utils;

import static java.util.stream.Collectors.toSet;

import org.springframework.beans.factory.ObjectProvider;

/**
 * @author Snake
 */
public class ObjectProviderUtils {

    public static <T> T getByClass(ObjectProvider<T> provider, Class<? extends T> clazz) {
        if (provider == null) {
            return null;
        }

        var candidates = provider.stream()
                .filter(t -> clazz.isAssignableFrom(t.getClass()))
                .collect(toSet());
        if (candidates.size() > 1) {
            throw new IllegalStateException("More than one bean of type " + clazz.getName() + " found: " +
                    candidates.stream().map(Object::getClass).map(Class::getName).collect(toSet()));
        } else if (candidates.isEmpty()) {
            return null;
        } else {
            return candidates.iterator().next();
        }
    }
}
