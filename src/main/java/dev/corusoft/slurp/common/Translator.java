package dev.corusoft.slurp.common;

import dev.corusoft.slurp.common.config.InternationalizationBean;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@NoArgsConstructor
public class Translator {
    private static final MessageSource messageSource = InternationalizationBean.messageSource();


    public static String generateMessage(String exceptionKey, Locale locale) {
        return generateMessage(exceptionKey, null, locale);
    }

    public static String generateMessage(String exceptionKey, Object[] args, Locale locale) {
        return messageSource.getMessage(
                exceptionKey,
                args,
                exceptionKey,
                locale
        );
    }
}
