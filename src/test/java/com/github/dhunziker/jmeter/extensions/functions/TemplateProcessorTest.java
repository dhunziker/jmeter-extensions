package com.github.dhunziker.jmeter.extensions.functions;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Dennis Hunziker on 14/10/16.
 */
public class TemplateProcessorTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private TemplateProcessor templateProcessor;

    private String tempDirLocation;

    @Before
    public void setUp() throws Exception {
        templateProcessor = new TemplateProcessor();
        tempDirLocation = tempDir.getRoot().getAbsolutePath();
    }

    @Test
    public void should_execute_function_for_simple_template() throws Exception {
        CompoundVariable p1 = mock(CompoundVariable.class);
        when(p1.execute()).thenReturn("src/test/resources/test-heading.ftl");

        CompoundVariable p2 = mock(CompoundVariable.class);
        when(p2.execute()).thenReturn(tempDirLocation);

        templateProcessor.setParameters(Arrays.asList(p1, p2));

        JMeterVariables vars = new JMeterVariables();
        vars.put("testHeading", "Star Wars: The Force Awakens");
        JMeterContextService.getContext().setVariables(vars);

        templateProcessor.execute();

        String result = FileUtils.readFileToString(new File(tempDirLocation + "/test-heading.html"));
        assertEquals("<h1>Star Wars: The Force Awakens</h1>", result);
    }

    @Test(expected = InvalidVariableException.class)
    public void should_fail_with_invalid_parameters() throws Exception {
        templateProcessor.setParameters(new LinkedList<>());
    }

    @Test
    public void should_return_function_name() throws Exception {
        assertEquals("__ProcessTemplate", templateProcessor.getReferenceKey());
    }

    @Test
    public void should_return_parameter_descriptions() throws Exception {
        List<String> desc = templateProcessor.getArgumentDesc();
        assertEquals(2, desc.size());
    }

    @Test
    public void should_process_simple_template() throws Exception {
        String src = "src/test/resources/test-heading.ftl";
        String dst = tempDirLocation + "/test-heading.html";
        Map<String, Object> vars = new HashMap<>();
        vars.put("testHeading", "Star Wars: The Force Awakens");
        templateProcessor.processTemplate(src, dst, vars);

        String result = FileUtils.readFileToString(new File(dst));
        assertEquals("<h1>Star Wars: The Force Awakens</h1>", result);
    }

    @Test
    public void should_process_table_template() throws Exception {
        String src = "src/test/resources/test-table.ftl";
        String dst = tempDirLocation + "/test-table.html";

        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("col1", "Hello");
        row1.put("col2", "World");

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("col1", "Foo");
        row2.put("col2", "Bar");

        List<Map<String, Object>> resultSet = Arrays.asList(row1, row2);

        Map<String, Object> vars = new HashMap<>();
        vars.put("resultSet", resultSet);
        templateProcessor.processTemplate(src, dst, vars);

        String result = FileUtils.readFileToString(new File(dst));
        assertEquals(3, StringUtils.countMatches(result, "<tr>"));
    }

    @Test(expected = RuntimeException.class)
    public void should_fail_with_missing_variables() throws Exception {
        String src = "src/test/resources/test-heading.ftl";
        String dst = tempDirLocation + "/test-heading.html";
        Map<String, Object> vars = new HashMap<>();
        templateProcessor.processTemplate(src, dst, vars);
    }
}