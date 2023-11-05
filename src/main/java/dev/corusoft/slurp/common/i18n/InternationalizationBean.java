package dev.corusoft.slurp.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class InternationalizationBean {
    private static final String[] supportedLanguages = {"en", "es"};
    private static final String[] supportedElements = {"exceptions", "fields", "validations"};
    private final String CLASSPATH = "classpath:i18n";

    @Bean
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
