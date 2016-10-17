package com.github.dhunziker.jmeter.extensions.util;

import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dennis Hunziker on 17/10/16.
 */
public class JMeterUtilsTest {

    @Test
    public void should_get_variables_as_map() throws Exception {
        JMeterVariables vars = new JMeterVariables();
        vars.put("Foo", "Bar");
        JMeterContextService.getContext().setVariables(vars);
        assertEquals("Bar", JMeterUtils.getVariables().get("Foo"));
    }
}