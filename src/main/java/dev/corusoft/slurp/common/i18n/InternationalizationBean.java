package dev.corusoft.slurp.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class InternationalizationBean {
    public static final String[] supportedLanguages = {"en", "es"};
    public static final String[] supportedElements = {"exceptions", "validations", "fields"};
    private final String CLASSPATH = "classpath:i18n";

    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
        bean.setDefaultEncoding(StandardCharsets.UTF_8.name());

        for (String basename: createBasenames()) {
            bean.addBasenames(basename);
        }

        return bean;
    }

    private List<String> createBasenames() {
        List<String> baseNamePaths = new ArrayList<>();

        for (String lang : supportedLanguages) {
            for (String item : supportedElements) {
                baseNamePaths.add(CLASSPATH + File.separator + lang + File.separator + item);
            }
        }

        return baseNamePaths;
    }
}
