package com.example.weather.util;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

/**
 * Renders short UI strings such as {@code "Meteo per ${city} il ${date}"}.
 *
 * <p>Uses Apache Commons Text {@link StringSubstitutor}, whose default interpolators are the
 * code path affected by CVE-2022-42889 ("Text4Shell"). Intentional for the test fixture.
 */
public final class TemplateRenderer {

    private TemplateRenderer() {
    }

    public static String render(String template, Map<String, String> values) {
        // Default constructor enables the ${script:}, ${dns:}, ${url:} lookups (Text4Shell).
        StringSubstitutor substitutor = new StringSubstitutor(values);
        return substitutor.replace(template);
    }
}
