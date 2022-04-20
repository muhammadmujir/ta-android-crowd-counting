package com.final_project.crowd_counting.auth

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
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.auth.model.RegisterRequest
import com.final_project.crowd_counting.auth.viewmodel.RegisterViewModel
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.utils.Util
import com.final_project.crowd_counting.base.utils.Util.validateEmailFormat
import com.final_project.crowd_counting.base.utils.Util.validateEmpty
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {

  private val viewModel: RegisterViewModel by viewModels()

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentRegisterBinding {
    return FragmentRegisterBinding.inflate(inflater, container, false)
  }

  override fun getVM(): RegisterViewModel = viewModel

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView()
  }

  private fun initView() {
    setSpannableString()
    viewBinding.btnSubmit.setOnClickListener {
      onRegisterButtonClicked()
    }
    initObserver()
  }

  private fun setSpannableString() {
    val spannableString = SpannableString(resources.getString(R.string.already_have_account))
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
      override fun onClick(widget: View) {
        requireActivity().supportFragmentManager.popBackStack()
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = Util.getThemeAttribute(R.attr.themeTextColorPrimary, viewBinding.root.context)
        ds.isUnderlineText = false
        ds.isFakeBoldText = true
      }
    }
    spannableString.setSpan(clickableSpan,
      resources.getInteger(R.integer.login_span_start),
      resources.getInteger(R.integer.login_span_end),
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    viewBinding.tvToRegister.run {
      text = spannableString
      highlightColor = Color.TRANSPARENT
      movementMethod = LinkMovementMethod.getInstance()
    }
  }

  private fun initObserver(){
    viewModel.registerResponse.observe(viewLifecycleOwner, {
      when(it.status){
        ResponseWrapper.Status.SUCCESS -> {
          Toast.makeText(requireContext(), getString(R.string.message_create_success, "user"), Toast.LENGTH_SHORT).show()
          requireActivity().supportFragmentManager.popBackStack()
        }
        else -> {
          Toast.makeText(requireContext(), "ERROR\n"+it.body?.errors.toString(), Toast.LENGTH_SHORT).show()
        }
      }
    })
  }

  private fun onRegisterButtonClicked(){
    with(viewBinding){
      if (validateEmpty(listOf(etName, etEmail, etPassword), listOf("Name", "Email", "Password"))){
        if (validateEmailFormat(etEmail)){
          viewModel.register(
            RegisterRequest(
              etEmail.text.toString(),
              etPassword.text.toString(),
              etName.text.toString()
            )
          )
        }
      }
    }
  }

  override fun shouldHandleLoading(): Boolean = false
  override fun shouldResetToolbarView(): Boolean = false

}