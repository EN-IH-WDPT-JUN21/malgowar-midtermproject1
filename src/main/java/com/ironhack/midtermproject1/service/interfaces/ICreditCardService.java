package com.ironhack.midtermproject1.service.interfaces;

import com.ironhack.midtermproject1.dao.CreditCard;

public interface ICreditCardService {
    CreditCard updateCreditCardBalanceByAddingInterests(Long id);
    CreditCard createCreditCard(CreditCard creditCard);
}
