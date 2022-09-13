package org.opennms.devjam2022.apiserver.model.utils;

import java.util.Random;

public class ModelUtil {
    private static final Random ID_GENERATOR = new Random();

    public static String generateId() {
        // NOTE: This is a little bit hacky, but it works for us as unsigned LONG example
        return Long.toUnsignedString(ID_GENERATOR.nextLong());
    }
}
