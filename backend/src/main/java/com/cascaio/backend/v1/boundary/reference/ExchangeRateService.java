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
package com.cascaio.backend.v1.boundary.reference;

import com.cascaio.api.v1.ReadRequestById;
import com.cascaio.api.v1.reference.ExchangeRateCreateRequest;
import com.cascaio.api.v1.reference.ExchangeRateQueryRequest;
import com.cascaio.api.v1.reference.ExchangeRateResponse;
import com.cascaio.api.v1.reference.ExchangeRateUpdateRequest;
import com.cascaio.backend.v1.boundary.BaseService;
import com.cascaio.backend.v1.entity.reference.ExchangeRate;
import com.cascaio.backend.v1.entity.reference.ExchangeRate_;
import com.cascaio.backend.v1.entity.reference.adapter.ZonedDateTimeAdapter;
import com.cascaio.backend.v1.entity.reference.adapter.ExchangeRateAdapter;
import com.cascaio.backend.v1.entity.reference.adapter.LocalDateAdapter;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
@Path("/reference/exchangeRates")
@Stateless
public class ExchangeRateService extends BaseService<
        ExchangeRateCreateRequest,
        ExchangeRateUpdateRequest,
        ExchangeRateQueryRequest,
        ReadRequestById,
        ExchangeRateResponse,
        ExchangeRate,
        ExchangeRateAdapter> {

    @Inject
    private ExchangeRateAdapter adapter;

    @Inject
    private LocalDateAdapter dateTimeAdapter;

    @RolesAllowed({"admin", "user"})
    @Override
    public List<ExchangeRate> listAsEntity(ExchangeRateQueryRequest request) {
        CurrencyUnit from = CurrencyUnit.of(request.getCurrencyFrom());
        CurrencyUnit to = CurrencyUnit.of(request.getCurrencyTo());
        String dateStart = request.getDateStart();
        String dateEnd = request.getDateEnd();

        LocalDate localDateStart = null;
        LocalDate localDateEnd = null;

        if (null != dateStart && !dateStart.isEmpty()) {
            localDateStart = dateTimeAdapter.adapt(dateStart);
        }

        if (null != dateEnd && !dateEnd.isEmpty()) {
            localDateEnd = dateTimeAdapter.adapt(dateEnd);
        }

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ExchangeRate> query = builder.createQuery(ExchangeRate.class);
        Root<ExchangeRate> root = query.from(ExchangeRate.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>(4);
        predicates.add(builder.equal(root.get(ExchangeRate_.currencyFrom), from));
        predicates.add(builder.equal(root.get(ExchangeRate_.currencyTo), to));

        if (null != localDateStart) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(ExchangeRate_.date), localDateStart));
        }

        if (null != localDateEnd) {
            predicates.add(builder.lessThanOrEqualTo(root.get(ExchangeRate_.date), localDateEnd));
        }

        query.where(predicates.toArray(new Predicate[predicates.size()]));
        query.orderBy(builder.desc(root.get(ExchangeRate_.date)));

        return getEntityManager().createQuery(query).getResultList();
    }
}
