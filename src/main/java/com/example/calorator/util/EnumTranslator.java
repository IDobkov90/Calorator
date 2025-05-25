package com.example.calorator.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EnumTranslator {

    private final MessageSource messageSource;

    public EnumTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String translate(Enum<?> enumValue) {
        if (enumValue == null) {
            return "";
        }

        String key = enumValue.getClass().getSimpleName() + "." + enumValue.name();
        return messageSource.getMessage(key, null, enumValue.name(), LocaleContextHolder.getLocale());
    }
}