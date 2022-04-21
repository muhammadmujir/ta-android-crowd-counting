package com.final_project.crowd_counting.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.final_project.crowd_counting.MainActivity
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.auth.AuthenticationActivity.Companion.TAG_REGISTER_FRAGMENT
import com.final_project.crowd_counting.auth.model.LoginRequest
import com.final_project.crowd_counting.auth.viewmodel.LoginViewModel
import com.final_project.crowd_counting.base.communicator.IActivityCommunicator
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.source.session.SessionManager
import com.final_project.crowd_counting.base.source.session.SessionManager.Companion.REFRESH_TOKEN
import com.final_project.crowd_counting.base.source.session.SessionManager.Companion.USER_TOKEN
import com.final_project.crowd_counting.base.utils.Util
import com.final_project.crowd_counting.base.utils.Util.validateEmailFormat
import com.final_project.crowd_counting.base.utils.Util.validateEmpty
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.base.view.CrowdCountingApp
import com.final_project.crowd_counting.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

  private val iCommunicator: IActivityCommunicator by lazy { activity as IActivityCommunicator }
  private val loginViewModel: LoginViewModel by viewModels()

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentLoginBinding {
    return FragmentLoginBinding.inflate(inflater, container, false)
  }

  override fun getVM(): LoginViewModel = loginViewModel

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setSpannableString()
    onLoginButtonClicked()
    initObserver()
  }

  private fun initObserver(){
    loginViewModel.loginResponse.observe(viewLifecycleOwner, {
      when(it.status){
        ResponseWrapper.Status.SUCCESS -> {
          Toast.makeText(requireContext(), getString(R.string.message_success, "login"), Toast.LENGTH_SHORT).show()
          CoroutineScope(Dispatchers.Main).launch {
            launch(Dispatchers.IO) {
              SessionManager(requireContext()).saveData(
                Bundle().apply {
                  putString(USER_TOKEN, it.body?.data?.accessToken)
                },null)
              (context?.applicationContext as CrowdCountingApp).apply {
                userToken = it.body?.data?.accessToken
              }
              startActivity(Intent(requireActivity(), MainActivity::class.java))
            }
          }
        }
        ResponseWrapper.Status.ERROR -> {
          Toast.makeText(requireContext(), it.body?.errors?.firstOrNull().toString(), Toast.LENGTH_SHORT).show()
        }
      }
    })
  }

  private fun onLoginButtonClicked() {
    with(viewBinding){
      btnSubmit.setOnClickListener {
        callLoginApi()
      }
    }
  }

  private fun callLoginApi(){
    with(viewBinding){
      val email = etEmail.text.toString().trim()
      val password = etPassword.text.toString()
      val loginRequest = LoginRequest(email = email, password = password)
      if (validateEmpty(listOf(etEmail, etPassword), listOf("Email","Password"))){
        if (validateEmailFormat(etEmail)){
          loginViewModel.callLoginApi(loginRequest)
        }
      }
    }
  }

  private fun setSpannableString() {
    val spannableString = SpannableString(resources.getString(R.string.dont_have_account))
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
      override fun onClick(widget: View) {
        iCommunicator.replaceFragment(RegisterFragment(), TAG_REGISTER_FRAGMENT, null, true)
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = Util.getThemeAttribute(R.attr.themeTextColorPrimary, viewBinding.root.context)
        ds.isUnderlineText = false
        ds.isFakeBoldText = true
      }
    }
    spannableString.setSpan(clickableSpan,
      resources.getInteger(R.integer.register_span_start),
      resources.getInteger(R.integer.register_span_end),
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    viewBinding.tvToRegister.run {
      text = spannableString
      highlightColor = Color.TRANSPARENT
      movementMethod = LinkMovementMethod.getInstance()
    }
  }

  override fun shouldHandleLoading(): Boolean = false

  override fun shouldResetToolbarView(): Boolean = false

}