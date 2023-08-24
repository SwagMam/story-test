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
import com.example.submission1aplikasistory.databinding.ActivityRegisterBinding
import com.example.submission1aplikasistory.helper.UserPreferences
import com.example.submission1aplikasistory.helper.ViewModelFactory

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupView()
        playAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViewModel() {
        val pref = UserPreferences.getInstance(dataStore)
        authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.authInfo.observe(this) {
            when (it) {
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
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
            btnRegister.setOnClickListener(this@RegisterActivity)
            imgBackHome.setOnClickListener(this@RegisterActivity)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.isLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.imgBackHome -> finish()
            binding.btnRegister -> {
                val name = binding.inputUsername.text.toString()
                val email = binding.inputEmail.text.toString()
                val password = binding.inputPassword.text.toString()

                if (binding.inputEmail.error == null && binding.inputPassword.error == null) {
                    closeKeyboard(this)
                    authViewModel.register(name, email, password)
                } else {
                    Toast.makeText(this, "Check Your Input", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvTitleReg, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title)
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
}