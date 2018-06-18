/*
 * Copyright (c) 2012-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.absyz;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.auth.HttpAccess;
import com.salesforce.androidsdk.auth.OAuth2;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static com.salesforce.androidsdk.auth.OAuth2.*;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity {

    private RestClient clientRest;
	private TextView errorTextView;
	private String AskToLogout="Try To Login Out And Login Again";
	//change this URL if we are moving to production
	private String URL="https://test.salesforce.com/services/oauth2/token";
	private String Client_ID ="3MVG99S6MzYiT5k9Zi_aoc.dquXJmkC3UIOksfnVzO5qn9KqIJ3KfkOk0WMZr9uqDjeCEEx4bd43jhObsZPkn";
	private String Client_Secret ="8240518122990865023";
	private String TAG = "CLIMA";
	private WebView ConnectWebView;
	private String authToken;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);

	}
	
	@Override 
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);
		super.onResume();
	}		
	
	@Override
	public void onResume(RestClient client) {
        this.clientRest = client;

        new RefreshToken().execute();

	}
	public void refresh(){

	    System.out.print("old token "+clientRest.getAuthToken());
        System.out.print("old token auth token "+authToken);

        ClientManager.AccMgrAuthTokenProvider acc = new ClientManager.AccMgrAuthTokenProvider(SalesforceSDKManager.getInstance().getClientManager(),clientRest.getClientInfo().communityUrl,clientRest.getAuthToken(),clientRest.getRefreshToken());
        String newAuth = acc.getNewAuthToken();
        authToken=newAuth;
        System.out.print("\nnew token "+authToken);
    }

    private void getValidAccessToken() {

        final Context context = SalesforceSDKManager.getInstance().getAppContext();
        RestResponse restResponse = null;
        Exception exception ;

        try {
            restResponse = clientRest.sendSync(RestRequest.getRequestForResources(
                    ApiVersionStrings.getVersionNumber(context)));
            Log.d(TAG, "getValidAccessToken: Rest Response"+restResponse);
        } catch (IOException e) {
            exception = e;
            Log.d(TAG, "getValidAccessToken: Exception"+exception);
        } finally {
            if (restResponse == null || !restResponse.isSuccess()) {
                Log.d(TAG, "getValidAccessToken: Invalid REST client");
            } else {
                Log.d(TAG, "getValidAccessToken: Got a success auth token");
            }
        }

    }

    /**
	 * Called when "Logout" button is clicked. 
	 * 
	 * @param v
	 */
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}
	public void Logout()
    {
        SalesforceSDKManager.getInstance().logout(this);
    }

    class RefreshToken extends AsyncTask<String, Void,Void> {


        @Override
        protected Void doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: /n Getting valid access token check");
            refresh();
            
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: TOken valid now");
            authToken = clientRest.getAuthToken();
            Log.d(TAG, "onPostExecute: OPen web view with"+authToken);
            OpenWebView(authToken);

        }


    }
public void OpenWebView(String authToken)  {

    Toast.makeText(getApplicationContext(),"Logging in",Toast.LENGTH_SHORT).show();
    String communityUrl= String.valueOf(clientRest.getClientInfo().communityUrl);
    System.out.println("community URL"+clientRest.getClientInfo().communityUrl);
    String url = ""+ communityUrl +"/one/one.app?sid="+ authToken+"";
    System.out.println("URL****"+url);
    findViewById(R.id.root).setVisibility(View.VISIBLE);
    ConnectWebView=findViewById(R.id.ConnectWebViews);
    ConnectWebView.setWebViewClient(new WebViewClient(){


        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebView", "your current url when webpage loading.. finish" + url);
            if(url.contains("login"))
            {
                Toast.makeText(getApplicationContext(),"You Logged Out the Session Manually",Toast.LENGTH_LONG).show();
                Logout();
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onLoadResource(view, url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("when you click on any interlink on webview that time you got url :-" + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

    });
    ConnectWebView.getSettings().setJavaScriptEnabled(true);
//
    Log.d(TAG, "onResume: first time loading");
    ConnectWebView.loadUrl(url);
    findViewById(R.id.ConnectWebViews).setVisibility(View.VISIBLE);
    System.out.println("URL:"+ConnectWebView.getUrl());
}
	
public void checkConn()
{
    Log.d(TAG, "\n CheckForConnection: 1 Access token"+clientRest.getAuthToken());
    Log.d(TAG, "checkConn: authTOken:"+authToken+"\n");
    AsyncHttpClient Aclient = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    params.add("grant_type","refresh_token");
    params.add("refresh_token",clientRest.getRefreshToken());
    params.add("client_id",Client_ID);
    params.add("client_secret",Client_Secret);

    Aclient.post(URL,params,new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            Log.d("HTTP", "OnSuccess: "+statusCode+"Header"+headers+"Response"+response.toString());

        }



        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            Log.d("HTTP", "OnFailure: "+statusCode+"Header"+headers+"Response"+errorResponse.toString()+"Throwbable"+throwable.getMessage());
            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
           // LoginError();
            Logout();

        }

    });

}

	public void CheckForConnection(View view) {
        Log.d(TAG, "\n CheckForConnection: Old Access token"+clientRest.getAuthToken());
		AsyncHttpClient Aclient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("grant_type","refresh_token");
		params.add("refresh_token",clientRest.getRefreshToken());
		params.add("client_id",Client_ID);
		params.add("client_secret",Client_Secret);

		Aclient.post(URL,params,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

				Log.d("HTTP", "OnSuccess: "+statusCode+"Header"+headers+"Response"+response.toString());
                try {
                    Toast.makeText(getApplicationContext(),"Logging in",Toast.LENGTH_SHORT).show();
                    String communityUrl= String.valueOf(clientRest.getClientInfo().communityUrl);
                    System.out.println("community URL"+clientRest.getClientInfo().communityUrl);
                    Log.d(TAG, "\nonSuccess: New Access Token"+response.get("access_token"));
                    String url = ""+ communityUrl +"/one/one.app?sid="+ response.get("access_token")+"";
//                    OpenWebView(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

				Log.d("HTTP", "OnFailure: "+statusCode+"Header"+headers+"Response"+errorResponse.toString()+"Throwbable"+throwable.getMessage());
			    Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                //LoginError();
			}

		});

	}

//    private void LoginError() {
//        errorTextView=findViewById(R.id.Error);
//        errorTextView.setText(AskToLogout);
//    }

//    public void OpenWebView(String url) {
//        Intent OpenWebView = new Intent(this,ConnectWebViewActivity.class);
//        OpenWebView.putExtra("URL",url);
//        Toast.makeText(getApplicationContext(),"Logging in",Toast.LENGTH_SHORT).show();
//        startActivity(OpenWebView);
//
//	}

}
