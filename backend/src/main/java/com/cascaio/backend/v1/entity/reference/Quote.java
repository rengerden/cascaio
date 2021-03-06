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
package com.cascaio.backend.v1.entity.reference;

import com.cascaio.backend.v1.entity.CascaioEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
@MappedSuperclass
public class Quote extends CascaioEntity {

    @NotNull
    private LocalDate date;

    @NotNull
    @Column(precision = 19, scale = 6)
    private BigDecimal price;

    // JPA happy
    protected Quote() {
    }

    public Quote(LocalDate date, BigDecimal price) {
        this(UUID.randomUUID().toString(), date, price);
    }

    public Quote(String id, LocalDate date, BigDecimal price) {
        super(id);
        if (null == date) {
            throw new IllegalArgumentException("A quote should always be relative to a date.");
        }

        if (null == price) {
            throw new IllegalArgumentException("A quote should always have a price.");
        }

        this.date = date;
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quote)) return false;

        Quote quote = (Quote) o;

        if (!date.isEqual(quote.date)) return false;
        if (price.compareTo(quote.price) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "date=" + date +
                ", price=" + price +
                ", entity=" + super.toString() +
                '}';
    }
}
