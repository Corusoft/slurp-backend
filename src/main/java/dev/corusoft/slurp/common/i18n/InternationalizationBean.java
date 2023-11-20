package dev.corusoft.slurp.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
public class InternationalizationBean {
    private static final String[] supportedLanguages = {"en", "es"};
    private static final String[] supportedElements = {"exceptions", "fields", "validations"};
    private static final String CLASSPATH = "classpath:i18n";

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        for (String basename: createBasenames()) {
            messageSource.addBasenames(basename);
        }

        return messageSource;
    }

    // INFO: https://lokalise.com/blog/spring-boot-internationalization
    @Bean
    public LocaleResolver localeResolver() {
        List<Locale> supportedLocales = getSupportedLocales();

        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setSupportedLocales(supportedLocales);
        localeResolver.setDefaultLocale(new Locale("es"));

        return localeResolver;
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

    private List<Locale> getSupportedLocales() {
        return Arrays.stream(supportedLanguages)
                .map(Locale::new)
                .toList();
    }
}
