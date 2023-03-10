package com.akabc.ngxmobileclient.ui.login

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.databinding.FragmentLoginBinding
import com.akabc.ngxmobileclient.net.FailMsg
import com.akabc.ngxmobileclient.data.Captcha
import com.akabc.ngxmobileclient.data.User


class LoginFragment : DialogFragment() {

    private val name = this.tag
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private var isFirstShow = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding: FragmentLoginBinding get() = _binding!!

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

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading
        val captchaImage = binding.ImagesCaptcha
        val captcha = binding.captcha
        val ipEditText = binding.ipadrr
        val portEditText = binding.port
        val view1 = binding.viewTop
        val view2 = binding.viewBottom

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /** ??????????????????????????????  **/
        mainViewModel.loginResult.value?.success?.let {
            ipEditText.setText(it.ip)
            portEditText.setText(it.port)
            usernameEditText.setText(it.Name)
            // passwordEditText.setText(it.pwd)
        }

        mainViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.ipError?.let {
                    ipEditText.error = getString(it)
                }
                loginFormState.portError?.let {
                    portEditText.error = getString(it)
                }
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        mainViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginError(it)
                }
                loginResult.success?.let {
                    if (!isFirstShow){
                        updateUiWithUser(it)
                        this.dismiss()
                    }
                    isFirstShow = false
                }
                loginResult.fail?.let {
                    showLoginFailed(it)
                }
            })

        mainViewModel.captcha.observe(viewLifecycleOwner) {
            it?.bitmap?.let { bitmap ->
                captchaImage.setImageBitmap(bitmap)
                Log.d(name, it.toString())
            }
        }

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                mainViewModel.loginDataChanged(
                    ipEditText.text.toString(),
                    portEditText.text.toString(),
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
                mainViewModel.repository.user.ip = ipEditText.text.toString()
                mainViewModel.repository.user.port = portEditText.text.toString()
            }
        }
        ipEditText.addTextChangedListener(afterTextChangedListener)
        portEditText.addTextChangedListener(afterTextChangedListener)
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
//        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                captchaId?.let {
//                    mainViewModel.login(
//                        usernameEditText.text.toString(),
//                        passwordEditText.text.toString(),
//                        it,
//                        captcha.text.toString(),
//                        ipEditText.text.toString(),
//                        portEditText.text.toString(),
//                        requireActivity()
//                    )
//                }
//            }
//            false
//        }

        view1.setOnClickListener {
            this.dismiss()
        }
        view2.setOnClickListener {
            this.dismiss()
        }

        loginButton.setOnClickListener {
            mainViewModel.captcha.value?.ctId.let { it2 ->
                loadingProgressBar.visibility = View.VISIBLE
                val loginUser = User(
                    Name = usernameEditText.text.toString(),
                    pwd = passwordEditText.text.toString(),
                    captcha = Captcha(it2, captcha.text.toString()),
                    ip = ipEditText.text.toString(),
                    port = portEditText.text.toString()
                )
                mainViewModel.login(
                    loginUser,
                    getString(R.string.login_url)
                )
            }
        }
        captchaImage.setOnClickListener {
            mainViewModel.repository.getCaptcha(getString(R.string.captcha_id_url),
                getString(R.string.captcha_url),
                mainViewModel)
        }
    }

    private fun updateUiWithUser(model: User) {
        val welcome = getString(R.string.welcome) + model.Name
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginError(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(msg: FailMsg) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, "${msg.code} : ${msg.msg}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}