package com.nevermore.sbridge.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

/**
 * @author nevermore
 */
class ObjectProviderUtilsTest {

    @Test
    void testGetByClassWithNullProvider() {
        assertNull(ObjectProviderUtils.getByClass(null, Object.class),
                "Should return null when provider is null");
    }

    @Test
    void testGetByClassWithEmptyCandidates() {
        // Setup
        @SuppressWarnings("unchecked")
        ObjectProvider<Object> mockProvider = mock(ObjectProvider.class);
        when(mockProvider.stream()).thenReturn(Stream.empty());

        // Test
        assertNull(ObjectProviderUtils.getByClass(mockProvider, String.class),
                "Should return null when no candidates found");
    }

    @Test
    void testGetByClassWithSingleCandidate() {
        // Setup
        String expected = "test";
        @SuppressWarnings("unchecked")
        ObjectProvider<Object> mockProvider = mock(ObjectProvider.class);
        when(mockProvider.stream()).thenReturn(Stream.of(expected));

        // Test
        Object result = ObjectProviderUtils.getByClass(mockProvider, String.class);
        assertEquals(expected, result, "Should return the matching candidate");
    }

    @Test
    void testGetByClassWithMultipleCandidatesOfDifferentTypes() {
        // Setup
        String expected = "test";
        Integer notExpected = 123;
        @SuppressWarnings("unchecked")
        ObjectProvider<Object> mockProvider = mock(ObjectProvider.class);
        when(mockProvider.stream()).thenReturn(Stream.of(expected, notExpected));

        // Test
        Object result = ObjectProviderUtils.getByClass(mockProvider, String.class);
        assertEquals(expected, result, "Should return the matching candidate of requested type");
    }

    @Test
    void testGetByClassWithMultipleCandidatesOfSameType() {
        // Setup
        @SuppressWarnings("unchecked")
        ObjectProvider<CharSequence> mockProvider = mock(ObjectProvider.class);
        when(mockProvider.stream()).thenReturn(Stream.of("test1", "test2"));

        // Test
        Exception exception = assertThrows(IllegalStateException.class,
                () -> ObjectProviderUtils.getByClass(mockProvider, String.class),
                "Should throw IllegalStateException when multiple candidates of same type found");

        assertTrue(exception.getMessage().contains("More than one bean of type"),
                "Exception message should indicate multiple beans found");
    }

    @Test
    void testGetByClassWithInheritedTypes() {
        // Setup
        class Parent {
        }
        class Child extends Parent {
        }

        Parent parent = new Parent();
        Child child = new Child();

        @SuppressWarnings("unchecked")
        ObjectProvider<Parent> mockProvider = mock(ObjectProvider.class);
        when(mockProvider.stream()).thenReturn(Stream.of(parent, child));

        // Test for Parent class - should throw exception due to multiple matches
        assertThrows(IllegalStateException.class,
                () -> ObjectProviderUtils.getByClass(mockProvider, Parent.class),
                "Should throw exception with multiple candidates for Parent class");

        // Test for Child class - should return only the child instance
        when(mockProvider.stream()).thenReturn(Stream.of(parent, child));
        Object result = ObjectProviderUtils.getByClass(mockProvider, Child.class);
        assertSame(child, result, "Should return only the Child instance");
    }
}
