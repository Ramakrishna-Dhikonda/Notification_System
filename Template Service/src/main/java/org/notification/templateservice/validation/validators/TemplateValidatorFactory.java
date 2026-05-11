package org.notification.templateservice.validation.validators;

import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.validation.TemplateValidator;
import org.notification.templateservice.validation.validators.channel.EmailTemplateValidator;
import org.notification.templateservice.validation.validators.channel.InAppTemplateValidator;
import org.notification.templateservice.validation.validators.channel.PushTemplateValidator;
import org.notification.templateservice.validation.validators.channel.SmsTemplateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Component
public class TemplateValidatorFactory {

    private Map<TemplateChannel, CompositeTemplateValidator> cache;

    @Autowired
    public TemplateValidatorFactory(SmsTemplateValidator sms,
                                    EmailTemplateValidator email,
                                    InAppTemplateValidator inApp,
                                    PushTemplateValidator push,
                                    PlaceholderSyntaxValidator placeHolder) {

        this.cache = Collections.unmodifiableMap(buildCache(sms, email, inApp, push, placeHolder));
    }

    public CompositeTemplateValidator getValidator(TemplateChannel channel) {
        return Optional.of(cache.get(channel))
                .orElseThrow(() ->
                        new IllegalArgumentException("No validator registered for channel: " + channel + ". Register a composite in TemplateValidatorFactory.")
                );
    }

    private Map<TemplateChannel, CompositeTemplateValidator> buildCache(SmsTemplateValidator sms,
                      EmailTemplateValidator email,
                      InAppTemplateValidator inApp,
                      PushTemplateValidator push,
                      PlaceholderSyntaxValidator placeholder) {

        cache = new EnumMap<>(TemplateChannel.class);
        cache.put(TemplateChannel.SMS, CompositeTemplateValidator
                .named("SMSCompositeValidator")
                .add(placeholder)
                .add(sms)
                .build()
        );
        cache.put(TemplateChannel.EMAIL, CompositeTemplateValidator
                .named("EmailCompositeValidator")
                .add(placeholder)
                .add(email)
                .build()
        );
        cache.put(TemplateChannel.IN_APP, CompositeTemplateValidator
                .named("InAppCompositeValidator")
                .add(placeholder)
                .add(inApp)
                .build()
        );
        cache.put(TemplateChannel.PUSH, CompositeTemplateValidator
                .named("PushCompositeValidator")
                .add(placeholder)
                .add(push)
                .build()
        );
        return cache;
    }
}
