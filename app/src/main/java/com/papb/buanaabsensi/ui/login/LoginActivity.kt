package com.papb.buanaabsensi.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.databinding.ActivityLoginBinding
import com.papb.buanaabsensi.ui.MainActivity
import com.papb.buanaabsensi.ui.admin.AdminActivity
import com.papb.buanaabsensi.ui.register.RegisterActivity
import com.papb.buanaabsensi.util.Constants.Companion.RESULT_DAFTAR_SUKSES
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initActionGo()
    }

    private fun login() {
        binding.progressBar.show()
        val email = binding.loginId.text.toString()
        val password = binding.loginPw.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                binding.loginId.error = getString(R.string.kosong)
            } else {
                binding.loginPw.error = getString(R.string.kosong)
            }
            binding.progressBar.hide()
            return
        }
        if (email == "admin" && password == "admin") {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        } else {
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.signOut()
            }
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    exception.message ?: "Unknown Error",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.hide()
            }
        }
    }

    private fun initActionGo() {
        binding.loginPw.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_GO) {
                login()
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(
                    currentFocus?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun initView() {
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_DAFTAR_SUKSES) {
                    // There are no request codes
                    result.data?.let { data ->
                        binding.loginId.setText(data.getStringExtra("email"))
                        binding.loginPw.requestFocus()
                    }
                }
            }
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val ss = SpannableString(getString(R.string.atau_daftar_disini))
        val clickDisini = object : ClickableSpan() {
            override fun onClick(widget: View) {
                resultLauncher.launch(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
        ss.setSpan(clickDisini, 12, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textRegister.text = ss
        binding.textRegister.movementMethod = LinkMovementMethod.getInstance()
        binding.loginButton.setOnClickListener { login() }
    }

}