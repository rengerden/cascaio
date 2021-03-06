/* 
 * Copyright (C) 2014 Juraci Paixão Kröhling <juraci at kroehling.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cascaio.backend.v1.control.batch.stock;

import com.cascaio.backend.v1.boundary.reference.StockMarketService;
import com.cascaio.backend.v1.boundary.reference.StockService;
import com.cascaio.backend.v1.entity.reference.Stock;
import com.cascaio.backend.v1.entity.reference.StockMarket;
import org.slf4j.Logger;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
@Named
public class NasdaqProcessor implements ItemProcessor {

    @Inject
    StockService stockService;

    @Inject
    StockMarketService stockMarketService;

    @Inject
    Logger logger;

    @Override
    public Object processItem(Object item) throws Exception {
        String line = (String) item;
        if (acceptLine(line)) {
            return parseLine(line);
        } else {
            return null;
        }
    }

    public Stock parseLine(String line) {
        logger.trace("Parsing line:" + line);

        String[] splittedLine = line.split("\\|");
        logger.trace("Number of items on the line: {}", splittedLine.length);

        StockMarket stockMarket = stockMarketService.getBySymbolAsEntity(getMarketSymbol());

        if (null == stockMarket) {
            throw new IllegalStateException("Stock market '" + getMarketSymbol() + "' doesn't exists on the database.");
        }

        Stock stock = new Stock(splittedLine[0], getSanitizedName(splittedLine[1]), stockMarket);
        if (null == stockService.getBySymbolAsEntity(stock.getSymbol(), stock.getMarket())) {
            // we have no found, proceed with persisting it
            return stock;
        }

        // we have found a record already, ignore this item
        return null;
    }

    protected boolean acceptLine(String line) {
        String[] parts = line.split("\\|");

        if (parts.length < 4) {
            return false;
        }

        if ("Y".equals(parts[3])) {
            // Y = Yes, it is a test issue, reject
            logger.trace("Rejecting, as this is a test symbol: {}", line);
            return false;
        }

        // for plain nasdaq file, we accept all lines (except test issues), as they all contain stocks
        return true;
    }

    public String getSanitizedName(String name) {
        return name.split("\\-")[0].trim();
    }

    public String getMarketSymbol() {
        return "NSDQ";
    }

}
