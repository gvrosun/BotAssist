package com.gvr.botassist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_list.view.*
import java.util.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.text.isDigitsOnly
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.lang.Exception


class ChatActivity : Activity(), View.OnClickListener,TextToSpeech.OnInitListener {
    override fun onInit(status: Int) {
        if (status != TextToSpeech.ERROR) {
            //t!!.language = Locale.UK
        }
    }

    val messages: ArrayList<String> = ArrayList()
    var t: TextToSpeech? = null
    var str: String = ""
    var gv:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatList.layoutManager = LinearLayoutManager(this)
        val c = findViewById<ImageButton>(R.id.mic)
        c.setOnClickListener(this)
        val s = findViewById<Button>(R.id.sent)
        s.setOnClickListener(this)
        t = TextToSpeech(this, this)
    }

    class MessageAdapter(val items: ArrayList<String>, val context: Context) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.chat_list,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder?.tvAnimalType?.text = items.get(position)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAnimalType = view.chatItems
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mic -> {
                gVoice()
                gv = false
            }
            R.id.sent -> {
                gSent()
                gv = true
            }
        }
    }

    private fun gVoice() {
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

    private fun gSent() {
            var mess = findViewById<EditText>(R.id.voice1).text.toString()
            if (gv) {
                messages.add(mess)
            } else {
                messages.add(str)
            }
            if (mess == "") {
                mess = str
            } else {
                str = mess
            }
            if (mess == "open alarm") {
                str = "Here we go"
                set(true)
                val managerclock = packageManager
                var i = managerclock.getLaunchIntentForPackage("com.gvr.alarm")
                i!!.addCategory(Intent.CATEGORY_LAUNCHER)
                startActivity(i)
            } else if (mess == "logout") {
                signOut()
            } else if (mess == "start calculation") {
                calculation()
            } else if (mess == "hello" || mess == "hi") {
                str = "hello!"
                set(true)
            } else if (mess == "who are you" || mess == "what is your name" || mess == "tell me about yourself") {
                str = "I am Bot Assist"
                set(true)
            } else if (mess == "how are you" || mess == "how do you do") {
                str = "I am good"
                set(true)
            }else if(mess == "clear") {
                messages.clear()
               // str = "cleared"
                set(true)

            } else if (mess == "close") {
                str = "Bye Bye"
                set(false)
                this.finish()
            } else if (mess == "logout and close") {
                str = "Done"
                set(false)
                var gso: GoogleSignInOptions =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                mGoogleSignInClient.signOut()
                this.finish()
            } else {
                try{
                var delimiter = " "
                val parts = str.split(delimiter)
                if (parts[0].isDigitsOnly() && parts[2].isDigitsOnly()) {
                    if (parts[1] == "+") {
                        var ans = parts[0].toInt() + parts[2].toInt()
                        str = "Answer is: " + ans.toString()
                        set(true)
                    }
                    if (parts[1] == "-") {
                        var ans = parts[0].toInt() - parts[2].toInt()
                        str = "Answer is: " + ans.toString()
                        set(true)
                    }
                    if (parts[1] == "x" || parts[1] == "into" || parts[1] == "multiply") {
                        var ans = parts[0].toInt() * parts[2].toInt()
                        str = "Answer is: " + ans.toString()
                        set(true)
                    }
                    if (parts[1] == "/" || parts[1] == "by") {
                        var ans = parts[0].toInt() / parts[2].toInt()
                        str = "Answer is: " + ans.toString()
                        set(true)
                    }
                } else if (parts[4] == "Armstrong") {
                    var dig = parts[1].length
                    var ans = armstrong(parts[1].toInt(), dig)
                    if (ans)
                        str = parts[1] + " is an Armstrong number"
                    else
                        str = parts[1] + " is not an Armstrong number"
                    set(true)
                } else {
                    str = "Sorry I don't know that"
                    set(true)
                }

            }catch (e:Exception){
                    Log.d("answer",e.toString())
                    str = "Sorry say again"
                    set(false)
                }
        }
    }

    fun armstrong(number:Int,n1:Int):Boolean {
        var originalNumber: Int
        var remainder: Int
        var result = 0
        var n = n1

        originalNumber = number

        while (originalNumber != 0) {
            originalNumber /= 10
            ++n
        }

        originalNumber = number

        while (originalNumber != 0) {
            remainder = originalNumber % 10
            result += Math.pow(remainder.toDouble(), n.toDouble()).toInt()
            originalNumber /= 10
        }
        if (result == number)
            return true
        else
            return false
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            10 -> {
                if (resultCode == RESULT_OK && data != null) {
                    var list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    str = list[0]
                    gSent()
                }
            }
        }
    }

    fun calculation() {

    }

    fun set(ver:Boolean)
    {
        if(ver) {
            messages.add(str)
            chatList.adapter = MessageAdapter(messages, this)
        }
        t!!.speak(str,TextToSpeech.QUEUE_FLUSH,null)
        voice1.setText("")
    }
    private fun signOut() {
        var gso:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, OnCompleteListener<Void> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            })
    }
    public override fun onPause() {
        if (t != null) {
            //t!!.stop()
            //t!!.shutdown()
        }
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}