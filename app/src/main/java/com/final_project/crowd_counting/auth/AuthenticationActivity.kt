package com.final_project.crowd_counting.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.final_project.crowd_counting.base.communicator.IActivityCommunicator
import com.final_project.crowd_counting.databinding.ActivityAuthenticationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity(), IActivityCommunicator {

  private lateinit var viewBinding: ActivityAuthenticationBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
    setContentView(viewBinding.root)
    if (supportFragmentManager.backStackEntryCount == 0)
      replaceFragment(LoginFragment(), TAG_LOGIN_FRAGMENT, null, true)
  }

  override fun replaceFragment(
    fragment: Fragment?, tag: String?, bundle: Bundle?, isAddedToBackStack: Boolean
  ) {
    tag?.let {
      supportFragmentManager.commit {
        supportFragmentManager.findFragmentByTag(it)?.let { frag ->
          replace(viewBinding.flContainer.id, frag, it)
        } ?: run {
          fragment?.let { newFrag ->
            replace(viewBinding.flContainer.id, newFrag, it)
            if (isAddedToBackStack)
              addToBackStack(it)
          }
        }
      }
    } ?: run {
      supportFragmentManager.commit {
        fragment?.let {
          replace(viewBinding.flContainer.id, it)
        }
      }
    }
  }

  override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount == 1)
      finish()
    else
      super.onBackPressed()
  }

  companion object{
    const val TAG_LOGIN_FRAGMENT = "tagLoginFragment"
    const val TAG_REGISTER_FRAGMENT = "tagRegisterFragment"
  }
}