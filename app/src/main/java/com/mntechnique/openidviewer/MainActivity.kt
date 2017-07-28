package com.mntechnique.openidviewer

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.oauth2authenticator.AddAccountSnackbar
import com.mntechnique.oauth2authenticator.auth.AccountGeneral
import com.mntechnique.oauth2authenticator.auth.AuthReqCallback
import com.mntechnique.oauth2authenticator.auth.AuthRequest
import com.mntechnique.oauth2authenticator.auth.RetrieveAuthTokenTask

import org.json.JSONException
import org.json.JSONObject

class MainActivity : AddAccountSnackbar() {
    internal lateinit var mAccountManager: AccountManager
    internal lateinit var mAccount: Account
    internal lateinit var accounts: Array <Account>
    internal lateinit var accessTokenCallback: AuthReqCallback
    internal lateinit var authRequest: AuthRequest
    internal lateinit var bearerToken: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAccountManager = AccountManager.get(this)

        val oauth2Scope = resources.getString(com.mntechnique.openidviewer.R.string.oauth2Scope)
        val clientId = resources.getString(com.mntechnique.openidviewer.R.string.clientId)
        val clientSecret = resources.getString(com.mntechnique.openidviewer.R.string.clientSecret)
        val serverURL = resources.getString(com.mntechnique.openidviewer.R.string.serverURL)
        val redirectURI = resources.getString(com.mntechnique.openidviewer.R.string.redirectURI)
        val authEndpoint = resources.getString(com.mntechnique.openidviewer.R.string.authEndpoint)
        val tokenEndpoint = resources.getString(com.mntechnique.openidviewer.R.string.tokenEndpoint)
        val openIDEndpoint = resources.getString(com.mntechnique.openidviewer.R.string.openIDEndpoint)

        authRequest = AuthRequest(
                applicationContext,
                oauth2Scope, clientId, clientSecret, serverURL,
                redirectURI, authEndpoint, tokenEndpoint)

        accounts = mAccountManager.getAccountsByType(BuildConfig.APPLICATION_ID)

        val request = OAuthRequest(Verb.GET, serverURL + openIDEndpoint)

        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                val openID = JSONObject(s)
                val progressBar = findViewById(R.id.progressBar) as ProgressBar
                val tvSub = findViewById(R.id.tvSub) as TextView
                val tvName = findViewById(R.id.tvName) as TextView
                val tvGivenName = findViewById(R.id.tvGivenName) as TextView
                val tvFamName = findViewById(R.id.tvFamName) as TextView
                val tvEmail = findViewById(R.id.tvEmail) as TextView
                val llOpenID = findViewById(R.id.llOpenID) as LinearLayout

                tvSub.text = openID.getString("sub")
                tvName.text = openID.getString("name")
                tvGivenName.text = openID.getString("given_name")
                tvFamName.text = openID.getString("family_name")
                tvEmail.text = openID.getString("email")

                progressBar.visibility = View.GONE
                llOpenID.visibility = View.VISIBLE
            }

            override fun onErrorResponse(s: String) {
                Toast.makeText(applicationContext,"Error parsing response", Toast.LENGTH_LONG).show()
            }
        }

        accessTokenCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                Log.d("CallbackSuccess", s)
                var bearerToken = JSONObject(s)
                if (bearerToken.length() > 0) {
                    authRequest.makeRequest(bearerToken.getString("access_token"), request, responseCallback)
                }
            }
            override fun onErrorResponse(s: String) {
                Log.d("CallbackError", s)
            }
        }
        fireUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (data != null){
            if (requestCode == 1 && resultCode == RESULT_OK){
                if (!data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME).isNullOrEmpty()){
                    for(a in accounts){
                        if(a.name.equals(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))){
                            val ratt = RetrieveAuthTokenTask(applicationContext, accessTokenCallback)
                            ratt.execute()
                            //getAuthToken(a, accessTokenCallback)
                        }
                    }
                } else {
                    finish()
                }
            }
        } else if (data === null) {
            Toast.makeText(applicationContext,"Account Error", Toast.LENGTH_LONG).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun fireUp(){
        if (accounts.size == 1) {
            mAccount = accounts[0]
            //getAuthToken(mAccount, accessTokenCallback)
            val ratt = RetrieveAuthTokenTask(applicationContext, accessTokenCallback)
            ratt.execute()
        }
        /*
        else if (Build.VERSION.SDK_INT >= 23) {
            val intent = AccountManager.newChooseAccountIntent(null, null, arrayOf(BuildConfig.APPLICATION_ID), null, null, null, null)
            startActivityForResult(intent, 1)
        }
        */
    }

    override fun onRestart() {
        super.onRestart()
        fireUp()
    }

    internal fun getAuthToken(account: Account, callback: AuthReqCallback) {
        mAccountManager.getAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,
                true, { future ->
            val bundle = future.result
            var authToken: String = ""
            if (!bundle.getString(AccountManager.KEY_AUTHTOKEN).isNullOrBlank()){
                authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN)
            }
            try {
                bearerToken = JSONObject(authToken)
            }catch (e:JSONException){
                callback.onErrorResponse(authToken)
            }
            callback.onSuccessResponse(authToken)
            print(authToken)
            mAccountManager.invalidateAuthToken(account.type, authToken)
        }, null)
    }
}
