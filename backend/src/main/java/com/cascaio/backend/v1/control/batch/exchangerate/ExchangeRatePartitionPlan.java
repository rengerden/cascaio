package com.cascaio.backend.v1.control.batch.exchangerate;

import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;

import javax.batch.api.partition.PartitionPlanImpl;
import javax.inject.Inject;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
public class ExchangeRatePartitionPlan extends PartitionPlanImpl {

    private static List<CurrencyUnit> availableCurrencies = CurrencyUnit.registeredCurrencies();

    @Inject
    Logger logger;

    @Override
    public int getPartitions() {
        int partitions = availableCurrencies.size();
        logger.trace("getPartitions: {}", partitions);
        return partitions;
    }

    @Override
    public int getThreads() {
        int threads = availableCurrencies.size() / 2;
        logger.trace("getThreads: {}", threads);
        return threads;
    }

    @Override
    public Properties[] getPartitionProperties() {
        Properties[] properties = getCurrenciesToProcess();
        logger.trace("Final properties size: {}", properties.length);
        return properties;
    }

    private Properties[] getCurrenciesToProcess() {
        Properties[] properties = new Properties[getPartitions()];
        for (int i = 0 ; i < getPartitions() ; i++) {
            CurrencyUnit currency = availableCurrencies.get(i);
            logger.trace("Adding to {} partition plan position {}", currency.getCurrencyCode(), i);
            properties[i] = new Properties();
            properties[i].put("from", currency.getCurrencyCode());
        }
        return properties;
    }
}