package com.gvr.botassist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import java.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T





class MainActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.login -> check()
            R.id.glogin2 -> glogin1()
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var googleApiClient: GoogleApiClient? = null
    var REQ_CODE:Int = 9001
    var a:Int = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val p = findViewById<Button>(R.id.login)
        p.setOnClickListener(this)
        var p1: SignInButton = findViewById(R.id.glogin2)
        p1!!.setOnClickListener(this)
        var signInOptions:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build()

    }
    private fun glogin1()
    {
        var intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(intent,REQ_CODE)
    }
    private fun check()
    {
        gVoice()
    }
    private fun check1(ch:String)
    {
        if(ch.equals("hello"))
        {
            val intent = Intent(this,ChatActivity::class.java)
            startActivity(intent)
        }
        else
        {
            val txt = findViewById<TextView>(R.id.attemt)
            txt.setText("Invalid data try again")
            val p = findViewById<Button>(R.id.login)
            p.setText("Try again")
            a--
            if(a==0)
            {
                Toast.makeText(this,"PLease try again after sometime",Toast.LENGTH_LONG).show()
                this.finishAffinity()
            }
            val txt1 = findViewById<TextView>(R.id.chsn)
            txt1.setText("Attempt: "+a.toString())
        }
    }
    private fun gVoice()
    {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10)
        } else {
            Toast.makeText(this, "Device not Supported!!!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleResult(result:GoogleSignInResult)
    {
        if(result.isSuccess)
        {
            var account:GoogleSignInAccount = result.signInAccount!!
            var name:String = account.displayName!!
            Toast.makeText(this,"Hi "+name,Toast.LENGTH_SHORT).show()
            val intent = Intent(this,ChatActivity::class.java)
            Log.d("result",result.toString())
            intent.putExtra(name,"gdata")
            startActivity(intent)
            this.finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            10 -> {
                if(resultCode == Activity.RESULT_OK && data !=null)
                {
                    var list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    check1(list[0])
                }
            }
            REQ_CODE -> {
                var result:GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleResult(result)
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        val alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (alreadyloggedAccount != null) {
            val intent = Intent(this,ChatActivity::class.java)
            startActivity(intent)
            this.finish()
        } else {
            Log.d("login", "Not logged in")
        }
    }
}
