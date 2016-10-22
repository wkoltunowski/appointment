package com.falco.testsupport;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class RangeMatchers {
    public static <T> Matcher<? super List<T>> startsWith(T element, T... elements) {
        List<Object> elementsList = new ArrayList<>();
        elementsList.add(element);
        elementsList.addAll(asList(elements));
        return new BaseMatcher<List<T>>() {
            @Override
            public boolean matches(Object item) {
                return ((List<T>) item).subList(0, elements.length + 1).equals(elementsList);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(elementsList);
            }
        };
    }
}
