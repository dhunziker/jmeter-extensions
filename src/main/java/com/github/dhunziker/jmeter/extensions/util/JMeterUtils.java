package com.github.dhunziker.jmeter.extensions.util;

import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

/**
 * Created by Dennis Hunziker on 17/10/16.
 */
public final class JMeterUtils {

    private JMeterUtils() {
    }

    public static Map<String, Object> getVariables() {
        JMeterVariables vars = JMeterContextService.getContext().getVariables();
        return vars.entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
