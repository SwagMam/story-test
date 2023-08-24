package com.example.submission1aplikasistory.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submission1aplikasistory.data.Resource
import com.example.submission1aplikasistory.databinding.ActivityLoginBinding
import com.example.submission1aplikasistory.helper.UserPreferences
import com.example.submission1aplikasistory.helper.ViewModelFactory
import com.example.submission1aplikasistory.ui.main.MainActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private var mShouldFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupView()
        playAnimation()
    }

    override fun onStop() {
        super.onStop()
        if (mShouldFinish)
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnSignUp -> startActivity(Intent(this, RegisterActivity::class.java))
            binding.btnLogin -> {
                if (canLogin()) {
                    val email = binding.inputEmail.text.toString()
                    val password = binding.inputPassword.text.toString()

                    closeKeyboard(this)
                    authViewModel.login(email, password)
                } else {
                    Toast.makeText(this, "Check Your Input", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val subTitle = ObjectAnimator.ofFloat(binding.tvSubTitle, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, subTitle)
            start()
        }
    }

    private fun closeKeyboard(activity: AppCompatActivity) {
        val view: View? = activity.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.authInfo.observe(this) {
            when (it) {
                is Resource.Success -> {
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    mShouldFinish = true
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
        }
    }

    private fun setupView() {
        with(binding) {
            btnSignUp.setOnClickListener(this@LoginActivity)
            btnLogin.setOnClickListener(this@LoginActivity)
        }
    }

    private fun canLogin() =
        binding.inputEmail.error == null && binding.inputPassword.error == null &&
                !binding.inputEmail.text.isNullOrEmpty() && !binding.inputPassword.text.isNullOrEmpty()

    private fun showLoading(isLoading: Boolean) {
        binding.isLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }
}