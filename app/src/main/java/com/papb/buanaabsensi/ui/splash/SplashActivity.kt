package com.papb.buanaabsensi.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.papb.buanaabsensi.ui.login.LoginActivity
import com.papb.buanaabsensi.ui.MainActivity
import com.papb.buanaabsensi.ui.admin.AdminActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var checkLoginJob: Job? = null
    private var errorHandler: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runnable = Runnable {
            checkLogin()
//            startActivity(Intent(this, AdminActivity::class.java))
//            finish()
        }
        handler = Handler(Looper.getMainLooper())
        handler.post(runnable)

        errorHandler = lifecycleScope.launchWhenCreated {
            viewModel.errorEvent.collect { errorMessage ->
                Toast.makeText(this@SplashActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin() {
        viewModel.checkLogin()
        checkLoginJob = lifecycleScope.launchWhenCreated {
            viewModel.authState.collect { state ->
                Timber.d("update state is $state")
                when (state) {
                    is AuthState.Error -> {
                        Toast.makeText(
                            this@SplashActivity,
                            state.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        delay(5000)
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
                    AuthState.LoggedIn -> {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                    AuthState.Init -> {
                    }
                    AuthState.Loading -> {
                    }
                    AuthState.NotLogged -> {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        handler.removeCallbacks(runnable)
        checkLoginJob?.cancel()
        errorHandler?.cancel()
    }

}