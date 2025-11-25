package com.infotech.wishmaplus.Utils;

import android.webkit.JsResult;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.payu.custombrowser.Bank;
import com.payu.custombrowser.PayUWebChromeClient;

public class CheckoutProWebChromeClient extends PayUWebChromeClient {


        public CheckoutProWebChromeClient(@NonNull Bank bank) {
        super(bank);
        }


        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
}


