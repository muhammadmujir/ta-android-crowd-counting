package com.final_project.crowd_counting

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.utils.Util.loadCircularImage
import com.final_project.crowd_counting.base.view.BaseActivity
import com.final_project.crowd_counting.databinding.ActivityMainBinding
import com.final_project.crowd_counting.home.ARG_CAMERA
import com.final_project.crowd_counting.window.ForegroundService
import com.final_project.crowd_counting.window.STOP_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : BaseActivity(), IToolbarCommunicator {

  private lateinit var viewBinding: ActivityMainBinding
  private val navController: NavController by lazy {
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
  }
  private var textWatcher: TextWatcher? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(viewBinding.root)
    initNavigationView()
    stopExistingForegroundService(ForegroundService::class.java)
    toCameraDetailFragment()
  }

  private fun stopExistingForegroundService(serviceClass: Class<*>){
    val manager = (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
    Log.d("totalService", manager.getRunningServices(Int.MAX_VALUE).size.toString())
    for (sv in manager.getRunningServices(Int.MAX_VALUE)){
      if (serviceClass.name.equals(sv.service.className)){
        Log.d("serviceFound", "yes")
        startForegroundService(Intent(this, ForegroundService::class.java).apply {
          putExtra(STOP_SERVICE, true)
        })
        break
      } else {
        Log.d("serviceNotFound", "yes")
        break
      }
    }
  }

  private fun toCameraDetailFragment(){
    intent.getParcelableExtra<Camera>(ARG_CAMERA)?.let {
      navController.navigate(R.id.to_camera_detail, Bundle().apply { putParcelable(ARG_CAMERA, it) })
    }
  }

  private fun initNavigationView(){
    with(viewBinding) {
      bottomNav.setupWithNavController(navController)
      bottomNav.itemIconTintList = null
      //      navController.addOnDestinationChangedListener { _, destination, _ ->
      //        layoutToolbar.etSearch.setOnEditorActionListener(null)
      //        layoutToolbar.toolbar.run {
      //          when(destination.id){
      //            R.id.option_home -> {
      //              layoutToolbar.etSearch.isVisible = true
      //              logo = ContextCompat.getDrawable(root.context,R.drawable.ic_logo_fastip_no_text)
      //              navigationIcon = null
      //              setNavigationOnClickListener(null)
      //              title = ""
      //              isVisible = true
      //            }
      //            R.id.option_order -> {
      //              layoutToolbar.etSearch.isVisible = false
      //              navigationIcon = ContextCompat.getDrawable(root.context,R.drawable.ic_logo_fastip_no_text)
      //              setNavigationOnClickListener(null)
      //              title = getString(R.string.my_order)
      //              isVisible = true
      //            }
      //          }
      //        }
      //      }
    }
  }

  private fun setupOnNavigationClick(){
    with(viewBinding.layoutToolbar.toolbar){
      setNavigationOnClickListener { navController.popBackStack() }
    }
  }

  override fun setSearchView(owner: LifecycleOwner, hint: String?, initText: String?, onKeyboardSearch: ((String) -> Unit)?,
    afterTextChangedActoin: ((String) -> Unit)?, onFocusAction: ((view: View, b: Boolean) -> Unit)?) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    with(viewBinding.layoutToolbar.etSearch){
      isVisible = initText != null || hint != null || onKeyboardSearch != null || afterTextChangedActoin != null || onFocusAction != null
      setHint(hint)
      initText?.let { setText(initText) }
      afterTextChangedActoin?.let { action ->
        textWatcher?.let { removeTextChangedListener(it) }
        textWatcher = addTextChangedListener(afterTextChanged = {
          action(it.toString())
        })
      } ?: run {
        textWatcher?.let { removeTextChangedListener(it) }
      }
      onFocusAction?.let { action ->
        clearFocus()
        setOnFocusChangeListener { view, b -> action(view, b) }
      } ?: run {
        setOnFocusChangeListener({ _,_ -> })
      }
      onKeyboardSearch?.let {
        setOnEditorActionListener { textView, actionId, keyEvent ->
          if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH){
            it(viewBinding.layoutToolbar.etSearch.text.toString())
          }
          false
        }
      } ?: run {
        setOnEditorActionListener {_,_,_ -> false}
      }
    }
  }

  override fun getCurrentSearchText(): String {
    return viewBinding.layoutToolbar.etSearch.text.toString()
  }

  override fun setTitleAndDescription(mTitle: String, mDescription: String?) {
    with(viewBinding.layoutToolbar){
      toolbar.run {
        title = mTitle
        mDescription?.let { subtitle = mDescription }
      }
      etSearch.isVisible = false
    }
  }

  override fun setCustomeTitle(mTitle: String?) {
    viewBinding.layoutToolbar.tvCustomeTitle.isVisible = mTitle != null
    mTitle?.let { title ->
      viewBinding.layoutToolbar.tvCustomeTitle.text = title
    }
  }

  override fun setCustomeImage(url: String?) {
    with(viewBinding.layoutToolbar){
      ivTitleImage.run {
        url?.let {
          isVisible = true
          loadCircularImage(it)
        } ?: run {
          isVisible = false
        }
      }
      etSearch.isVisible = false
    }
  }

  override fun setLogo(drawable: Drawable?) {
    viewBinding.layoutToolbar.toolbar.logo = drawable
  }

  override fun setOnNavigateBack(owner: LifecycleOwner, clickListener: View.OnClickListener?, isVisible: Boolean) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    viewBinding.layoutToolbar.toolbar.run {
      navigationIcon = if(isVisible) ContextCompat.getDrawable(context, R.drawable.ic_back_arrow) else null
      if (clickListener == null){
        setupOnNavigationClick()
      } else {
        setNavigationOnClickListener(clickListener)
      }
    }
  }

  override fun inflateMenu(menuResId: Int?, isAppendMenu: Boolean) {
    viewBinding.layoutToolbar.toolbar.run {
      if (!isAppendMenu)
        menu.clear()
      menuResId?.let { inflateMenu(it) }
    }
  }

  override fun setOnMenuItemClickListener(owner: LifecycleOwner, listener: Toolbar.OnMenuItemClickListener?) {
    if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
    if (listener != null) viewBinding.layoutToolbar.toolbar.setOnMenuItemClickListener(listener)
    else viewBinding.layoutToolbar.toolbar.setOnMenuItemClickListener({false})
  }

  override fun resetToolbarView() {
    with(viewBinding.layoutToolbar){
      toolbar.run {
        logo = null
        title = ""
        subtitle = ""
        menu.clear()
      }
      ivTitleImage.isVisible = false
      etSearch.isVisible = false
    }
    setToolbarVisibility(true)
    setCustomeTitle(null)
    setCustomeImage(null)
    setOnNavigateBack(this, null, false)
    setSearchView(this, null,  null, null, null, null)
    setOnMenuItemClickListener(this, null)
  }

  override fun setToolbarVisibility(isShow: Boolean) {
    viewBinding.layoutToolbar.toolbar.isVisible = isShow
  }

  override fun isLoading(loading: Boolean) {
    toggleErrorMessage(null, false)
    toggleLoadingVisibility(loading)
  }

  override fun onError(
    message: String,
    callbacks: Collection<() -> Unit>?,
    startLoading: ((Int) -> Unit)?
  ) {
    toggleErrorMessage(message, true)
    viewBinding.btnRetry.setOnClickListener {
      viewBinding.groupError.visibility = View.GONE
      callbacks?.let {
        val callbackSize = it.size
        startLoading?.let { start -> start(callbackSize) }
        for (i in 1..callbackSize){
          Log.d("CALL ", "RETRY")
          (callbacks as? Queue)?.poll()?.let { call ->
            call()
          }
        }
      }
    }
  }

  private fun toggleErrorMessage(message: String?, isVisible: Boolean){
    with(viewBinding){
      if (isVisible){
        toggleLoadingVisibility(isVisible = false, hideViewBackground = false)
        viewBackground.background = ContextCompat.getDrawable(root.context, android.R.color.white)
        message?.let { tvErrorMessage.text = it }
        groupError.visibility = View.VISIBLE
      } else {
        groupError.visibility = View.GONE
      }
    }
  }

  private fun toggleLoadingVisibility(isVisible: Boolean, hideViewBackground: Boolean = true){
    with(viewBinding){
      if (isVisible){
        viewBackground.background = ContextCompat.getDrawable(root.context, android.R.color.white)
        viewBackground.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
      } else {
        if (hideViewBackground)
          viewBackground.visibility = View.GONE
        progressBar.visibility = View.GONE
      }
    }
  }
}