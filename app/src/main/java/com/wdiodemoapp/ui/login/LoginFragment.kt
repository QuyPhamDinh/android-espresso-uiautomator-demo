package com.wdiodemoapp.ui.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wdiodemoapp.R
import com.wdiodemoapp.databinding.FragmentLoginBinding
import com.wdiodemoapp.ui.applyTopThirdOffset

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isSignUpMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyTopThirdOffset(binding.loginScreen, binding.loginContent)

        binding.loginHeader.setOnClickListener { dismissKeyboard() }
        binding.loginContent.setOnClickListener { dismissKeyboard() }

        binding.buttonLoginContainer.setOnClickListener {
            setSignUpMode(false)
        }
        binding.buttonSignUpContainer.setOnClickListener {
            setSignUpMode(true)
        }

        binding.buttonLogin.setOnClickListener {
            dismissKeyboard()
            if (validateInputs(requireRepeatPassword = false)) {
                showAlert(
                    title = getString(R.string.alert_login_title),
                    message = getString(R.string.alert_login_message),
                )
            }
        }

        binding.buttonSignUp.setOnClickListener {
            dismissKeyboard()
            if (validateInputs(requireRepeatPassword = true)) {
                showAlert(
                    title = getString(R.string.alert_signup_title),
                    message = getString(R.string.alert_signup_message),
                )
            }
        }

        binding.buttonBiometric.setOnClickListener {
            dismissKeyboard()
            launchBiometricPrompt()
        }

        setSignUpMode(false)
        updateBiometricVisibility()
    }

    override fun onResume() {
        super.onResume()
        updateBiometricVisibility()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            updateBiometricVisibility()
        }
    }

    private fun setSignUpMode(signUp: Boolean) {
        isSignUpMode = signUp
        clearErrors()

        val orange = ContextCompat.getColor(requireContext(), R.color.wdio_orange)
        val secondary = ContextCompat.getColor(requireContext(), R.color.text_secondary)

        binding.buttonLoginContainer.setTextColor(if (signUp) secondary else orange)
        binding.buttonSignUpContainer.setTextColor(if (signUp) orange else secondary)

        binding.inputRepeatPassword.visibility = if (signUp) View.VISIBLE else View.GONE
        binding.buttonLogin.visibility = if (signUp) View.GONE else View.VISIBLE
        binding.buttonSignUp.visibility = if (signUp) View.VISIBLE else View.GONE

        updateBiometricVisibility()
    }

    private fun updateBiometricVisibility() {
        val biometricManager = BiometricManager.from(requireContext())
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK,
        ) == BiometricManager.BIOMETRIC_SUCCESS

        binding.buttonBiometric.visibility =
            if (!isSignUpMode && canAuthenticate) View.VISIBLE else View.GONE
    }

    private fun launchBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)
                    showAlert(
                        title = getString(R.string.alert_login_title),
                        message = getString(R.string.alert_login_message),
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Keep the system BiometricPrompt open on failed match.
                }
            },
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_prompt_cancel))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK,
            )
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun validateInputs(requireRepeatPassword: Boolean): Boolean {
        clearErrors()

        val email = binding.inputEmail.text?.toString()?.trim().orEmpty()
        val password = binding.inputPassword.text?.toString().orEmpty()
        val repeatPassword = binding.inputRepeatPassword.text?.toString().orEmpty()

        var valid = true
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.errorEmail.visibility = View.VISIBLE
            valid = false
        }
        if (password.length < 8) {
            binding.errorPassword.visibility = View.VISIBLE
            valid = false
        }
        if (requireRepeatPassword && (repeatPassword.isEmpty() || repeatPassword != password)) {
            binding.errorRepeatPassword.visibility = View.VISIBLE
            valid = false
        }
        return valid
    }

    private fun clearErrors() {
        binding.errorEmail.visibility = View.GONE
        binding.errorPassword.visibility = View.GONE
        binding.errorRepeatPassword.visibility = View.GONE
    }

    private fun showAlert(title: String, message: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.alert_ok, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isAllCaps = true
        }
        dialog.show()
    }

    private fun dismissKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val target = requireActivity().currentFocus ?: binding.root
        imm.hideSoftInputFromWindow(target.windowToken, 0)
        target.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
