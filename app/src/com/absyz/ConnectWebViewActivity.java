package com.absyz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

public class ConnectWebViewActivity extends SalesforceActivity {
    private WebView ConnectWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_web_view);
        ConnectWebView=findViewById(R.id.ConnectWebView);


    }

    @Override
    public void onResume(RestClient client) {
        Intent url = getIntent();
        String AccessURL=url.getStringExtra("URL");
        ConnectWebView.setWebViewClient(new WebViewClient());
        ConnectWebView.getSettings().setJavaScriptEnabled(true);
        ConnectWebView.loadUrl(AccessURL);

    }
}
