package com.mntechnique.openidviewer

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.otpmobileauth.auth.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar

import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var accounts: Array<Account>
    lateinit var accessTokenCallback: AuthReqCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serverURL = resources.getString(com.mntechnique.openidviewer.R.string.serverURL)
        val openIDEndpoint = resources.getString(com.mntechnique.openidviewer.R.string.openIDEndpoint)

        var progressBar = find<ProgressBar>(R.id.progressBar)
        var tvSub = find<TextView>(R.id.tvSub)
        var tvName = find<TextView>(R.id.tvName)
        var tvGivenName = find<TextView>(R.id.tvGivenName)
        var tvFamName = find<TextView>(R.id.tvFamName)
        var tvEmail = find<TextView>(R.id.tvEmail)
        var llOpenID = find<LinearLayout>(R.id.llOpenID)

        val authRequest = AuthRequest(
                applicationContext,
                resources.getString(com.mntechnique.openidviewer.R.string.oauth2Scope),
                resources.getString(com.mntechnique.openidviewer.R.string.clientId),
                resources.getString(com.mntechnique.openidviewer.R.string.clientSecret),
                resources.getString(com.mntechnique.openidviewer.R.string.serverURL),
                resources.getString(com.mntechnique.openidviewer.R.string.redirectURI),
                resources.getString(com.mntechnique.openidviewer.R.string.authEndpoint),
                resources.getString(com.mntechnique.openidviewer.R.string.tokenEndpoint)
        )

        val request = OAuthRequest(Verb.GET, serverURL + openIDEndpoint)

        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                val openID = JSONObject(s)
                tvSub.text = openID.getString("sub")
                tvName.text = openID.getString("name")
                tvGivenName.text = openID.getString("given_name")
                tvFamName.text = openID.getString("family_name")
                tvEmail.text = openID.getString("email")
                progressBar.visibility = View.GONE
                llOpenID.visibility = View.VISIBLE
            }

            override fun onErrorResponse(s: String) {
                toast("Error parsing response")
            }
        }

        accessTokenCallback = object : AuthReqCallback {
            override fun onSuccessResponse(s: String) {
                val bearerToken = JSONObject(s)
                if (bearerToken.length() > 0) {
                    authRequest.makeRequest(bearerToken.getString("access_token"), request, responseCallback)
                }
            }
            override fun onErrorResponse(s: String) {
                toast("Something went wrong")
            }
        }

        fireUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && !data.extras.isEmpty) {
            if (requestCode == 1 && resultCode == RESULT_OK){
                if (!data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME).isNullOrEmpty()){
                    for(a in accounts){
                        if(a.name.equals(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))){
                            val ratt = RetrieveAuthTokenTask(applicationContext, accessTokenCallback)
                            ratt.execute()
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRestart() {
        super.onRestart()
        fireUp()
    }

    override fun onResume() {
        super.onResume()
        fireUp()
    }

    fun fireUp(){
        val mAccountManager = AccountManager.get(this@MainActivity)
        accounts = mAccountManager.getAccountsByType(BuildConfig.APPLICATION_ID)
        var snackBar: Snackbar? = null
        if (accounts.size == 1) {
            val ratt = RetrieveAuthTokenTask(applicationContext, accessTokenCallback)
            ratt.execute()
        } else {
            snackbar(find(android.R.id.content), resources.getString(R.string.please_add_account))
                    .setAction ("Add"){
                        startActivity<AuthenticatorActivity>(AuthenticatorActivity.ARG_ACCOUNT_TYPE to BuildConfig.APPLICATION_ID)
                    }.show()
        }
    }
}
