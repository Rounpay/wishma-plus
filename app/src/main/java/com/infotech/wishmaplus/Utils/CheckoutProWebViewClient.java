package com.infotech.wishmaplus.Utils;

import com.payu.custombrowser.Bank;
import com.payu.custombrowser.PayUWebViewClient;

public class CheckoutProWebViewClient extends PayUWebViewClient{

    public CheckoutProWebViewClient(Bank bank, String merchantKey){
        super(bank, merchantKey);
    }
}