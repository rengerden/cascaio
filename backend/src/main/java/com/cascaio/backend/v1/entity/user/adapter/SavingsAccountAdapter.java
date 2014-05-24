package com.cascaio.backend.v1.entity.user.adapter;

import com.cascaio.api.v1.user.SavingsAccountCreateRequest;
import com.cascaio.api.v1.user.SavingsAccountResponse;
import com.cascaio.api.v1.user.SavingsAccountUpdateRequest;
import com.cascaio.backend.v1.boundary.reference.FinancialInstitutionService;
import com.cascaio.backend.v1.entity.reference.FinancialInstitution;
import com.cascaio.backend.v1.entity.user.SavingsAccount;
import org.joda.money.CurrencyUnit;

import javax.inject.Inject;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
public class SavingsAccountAdapter extends UserDataAdapter<SavingsAccountCreateRequest, SavingsAccountUpdateRequest, SavingsAccountResponse, SavingsAccount> {

    @Inject
    FinancialInstitutionService financialInstitutionService;

    @Override
    public SavingsAccountResponse adaptPersistent(SavingsAccount savingsAccount) {
        SavingsAccountResponse response = new SavingsAccountResponse();
        response.setCurrencyUnit(savingsAccount.getCurrency().getCurrencyCode());
        response.setFinancialInstitutionId(savingsAccount.getFinancialInstitution().getId());
        response.setName(savingsAccount.getName());
        response.setId(savingsAccount.getId());
        response.setTotal(savingsAccount.getTotal());
        return response;
    }

    @Override
    public SavingsAccount adaptUpdate(SavingsAccountUpdateRequest request, SavingsAccount account) {
        account.setName(currentOrUpdated(request.getName(), account.getName()));

        String financialInstitutionId = request.getFinancialInstitutionId();
        if (isSet(financialInstitutionId)) {
            account.setFinancialInstitution(financialInstitutionService.readAsEntity(financialInstitutionId));
        }

        String currencyUnit = request.getCurrencyUnit();
        if (isSet(currencyUnit)) {
            account.setCurrency(CurrencyUnit.of(currencyUnit));
        }

        return account;
    }

    @Override
    public SavingsAccount adaptCreate(SavingsAccountCreateRequest request) {
        FinancialInstitution fi = financialInstitutionService.readAsEntity(request.getFinancialInstitutionId());
        CurrencyUnit currency = CurrencyUnit.of(request.getCurrencyUnit());
        return new SavingsAccount(getCascaioUser(), request.getName(), currency, fi);
    }
}