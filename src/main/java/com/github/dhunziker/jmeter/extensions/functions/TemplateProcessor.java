package com.github.dhunziker.jmeter.extensions.functions;

import com.github.dhunziker.jmeter.extensions.util.JMeterUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Created by Dennis Hunziker on 14/10/16.
 */
public class TemplateProcessor extends AbstractFunction {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final String REFERENCE_KEY = "__ProcessTemplate";

    private static final int MAX_PARAM_COUNT = 2;

    private static final int MIN_PARAM_COUNT = 2;

    private List<String> parameters;

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) throws InvalidVariableException {
        Path src = Paths.get(parameters.get(0)).toAbsolutePath();
        String fileName = src.getFileName().toString().replace(".ftl", ".html");
        Path dst = Paths.get(parameters.get(1), fileName).toAbsolutePath();
        processTemplate(src.toString(), dst.toString(), JMeterUtils.getVariables());
        return dst.toString();
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, MIN_PARAM_COUNT, MAX_PARAM_COUNT);
        this.parameters = Stream.of(parameters.toArray()).map(p -> ((CompoundVariable) p).execute().trim()).collect(toList());
    }

    @Override
    public String getReferenceKey() {
        return REFERENCE_KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        List<String> desc = new LinkedList<>();
        desc.add("Absolute path to the template to process.");
        desc.add("Folder to write the output to.");
        return desc;
    }

    void processTemplate(String src, String dst, Map<String, Object> vars) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

        try (Writer file = new FileWriter(dst)) {
            Path srcPath = Paths.get(src);

            FileTemplateLoader ftl = new FileTemplateLoader(srcPath.getParent().toFile());
            ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "/templates");
            MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{ftl, ctl});
            cfg.setTemplateLoader(mtl);

            Template template = cfg.getTemplate(srcPath.getFileName().toString());
            template.process(vars, file);
        } catch (IOException | TemplateException e) {
            log.error("Failed to process template", e);
            throw new RuntimeException(e);
        }
    }
}
