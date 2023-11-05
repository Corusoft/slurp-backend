package dev.corusoft.slurp.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class Translator {
    private final MessageSource messageSource;


    public Translator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public String generateMessage(String exceptionKey, Locale locale) {
        return generateMessage(exceptionKey, null, locale);
    }

    public String generateMessage(String exceptionKey, Object[] args, Locale locale) {
        return messageSource.getMessage(
                exceptionKey,
                args,
                exceptionKey,
                locale
        );
    }
}
