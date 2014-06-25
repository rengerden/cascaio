package com.cascaio.api.v1.validation;

import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
public class CurrencyValidator implements ConstraintValidator<Currency, String> {
    Logger logger = LoggerFactory.getLogger(CurrencyValidator.class);

    @Override
    public void initialize(Currency constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            logger.trace("Got an empty currency. Validation passed.");
            return true;
        }

        for (CurrencyUnit currency : CurrencyUnit.registeredCurrencies()) {
            if (currency.getCurrencyCode().equals(value)) {
                logger.trace("Found {} in the currency stack", value);
                return true;
            }

        }

        logger.trace("Couldn't find {} in the currency stack", value);
        return false;
    }
}