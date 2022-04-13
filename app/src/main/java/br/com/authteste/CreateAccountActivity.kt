package br.com.authteste

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isEmpty
import br.com.authteste.databinding.ActivityCreateAccountBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding

    private lateinit var functions: FirebaseFunctions
    private val logEntry = "LOGIN"
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
        functions = Firebase.functions("southamerica-east1")
        //functions.useEmulator("localhost", 5001)

        //Tirar isso depois
        auth.signOut()

        binding.btnCreate.setOnClickListener {
            createUser()
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        updateUI(user)
    }

    private fun storeUser(u: User): Task<String> {
        val data = hashMapOf(
            "email" to u.email,
            "name" to u.name
        )
        return functions
            .getHttpsCallable("addUser")
            .call(data)
            .continueWith { task ->
                // se faz necessario transformar a string de retorno como uma string Json valida.
                val res = gson.toJson(task.result?.data)
                res
            }
    }

    private fun createUser() {
        val email = binding.tfEmail.editText?.text.toString()
        val name = binding.tfName.editText?.text.toString()
        val password = binding.tfPassword.editText?.text.toString()

        if(email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->
                    if(task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        //val user = auth.currentUser
                        //updateUI(user)
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_LONG).show()
                        //updateUI(null)
                    }
                }
            val u = User(email, name)
            storeUser(u)
                .addOnCompleteListener(OnCompleteListener {task ->
                    if(!task.isSuccessful) {
                        val e = task.exception
                        if(e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                        }
                    } else {
                        val genericResp = gson.fromJson(task.result, FunctionsGenericResponse::class.java)
                        // abra a aba Logcat e selecione "INFO" e filtre por
                        Log.i(logEntry, genericResp.status.toString())
                        Log.i(logEntry, genericResp.message.toString())

                        // claro, o payload deve ser convertido para outra coisa depois.
                        Log.i(logEntry, genericResp.payload.toString())
                        val user = auth.currentUser
                        updateUI(user)
                    }
                })
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            openMainActivity()
        }
    }

    private fun openMainActivity() {
        val intentMain = Intent(this, MainActivity::class.java)
        startActivity(intentMain)
    }
}