package br.com.authteste

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.authteste.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.GsonBuilder

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var functions: FirebaseFunctions
    private val logEntry = "LOGIN"
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLogin.setOnClickListener {
            verifyLogin()
        }

        binding.btnNewAccount.setOnClickListener {
            openCreateUser()
        }
    }

    private fun verifyLogin() {

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            openMainActivity()
        }
    }

    private fun openMainActivity() {
        val intentCreateAccount = Intent(this, MainActivity::class.java)
        startActivity(intentCreateAccount)
    }

    private fun openCreateUser() {
        val intentCreateAccount = Intent(this, CreateAccountActivity::class.java)
        startActivity(intentCreateAccount)
    }
}