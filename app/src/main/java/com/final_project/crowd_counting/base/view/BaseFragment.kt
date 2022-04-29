package com.final_project.crowd_counting.base.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.final_project.crowd_counting.base.communicator.ILoadingCommunicator
import com.final_project.crowd_counting.base.constant.Constant
import com.final_project.crowd_counting.base.constant.Constant.IMAGE_PATH
import com.final_project.crowd_counting.base.utils.FileUtil
import com.final_project.crowd_counting.base.utils.Util.checkPermission
import com.final_project.crowd_counting.base.view.ProgressDialogFragment.Companion.PROGRESS_LOADING
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

abstract class BaseFragment<VB:ViewBinding, VM: BaseViewModel>: BaseFragmentOnlyUI<VB>(), ILoadingCommunicator {

  protected var imagePath: String? = null
  private var fileManagerCallback: (() -> Unit)? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    savedInstanceState?.let {
      imagePath = it.getString(IMAGE_PATH)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d("baseFragObserve", "yes")
    getVM().loading.observe(viewLifecycleOwner, {
      Log.d("baseFragNotify", "yes")
      if (shouldHandleLoading()){
        if (it.isLoading){
          isLoading(true)
        } else if (!it.retryCallback.isNullOrEmpty()){
          onError(it.message, it.retryCallback, it.startLoading)
        } else {
          isLoading(false)
        }
      } else{
        (activity as ILoadingCommunicator).apply {
          if (it.isLoading){
            isLoading(true)
          } else if (!it.retryCallback.isNullOrEmpty()){
            onError(it.message, it.retryCallback, it.startLoading)
          } else {
            isLoading(false)
          }
        }
      }
    })
  }

  abstract fun getVM(): VM

  abstract fun shouldHandleLoading(): Boolean

  protected fun dialogConfirmation(message: String, callback: () -> Unit){
    val dialogFragment = ConfirmationDialogFragment.newInstance(message).apply {
      setResponseCommunicator(object : IResponseCommunicator {
        override fun onResponse(
          dialogResponse: DialogResponse,
          data: Bundle?
        ) {
          if (dialogResponse == DialogResponse.YES){
            callback()
          }
          dismiss()
        }
      })
    }
    childFragmentManager.beginTransaction().add(dialogFragment, null).commit()
  }

  protected fun registerFileManagerCallback(callback: (() -> Unit)? = null){
    fileManagerCallback = callback
  }

  protected fun createFileBody(): MultipartBody.Part? {
    return imagePath?.let {
      val file = File(it)
      val requestFile = file.asRequestBody(("image/jpeg").toMediaTypeOrNull())
      return MultipartBody.Part.createFormData(
        "file",
        file.name,
        requestFile
      )
    }
  }

  protected fun openFileManager(){
    if (checkPermission()){
      val intent = Intent(Intent.ACTION_GET_CONTENT)
      intent.type = "image/*"
      startActivityForResult(intent, Constant.PICK_PHOTO)
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when(requestCode){
      Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
          openFileManager()
        } else {
          Toast.makeText(requireContext(), "CANNOT OPEN FILE MANAGER", Toast.LENGTH_LONG).show()
        }
      }
      else -> {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == Constant.PICK_PHOTO && resultCode == Activity.RESULT_OK){
      if (data != null){
        data.data?.let {
          FileUtil.getPath(it, requireContext())?.let { path ->
            imagePath = path
            fileManagerCallback?.let { it() }
          }
        }
      }
    }
  }

  override fun isLoading(loading: Boolean) {
    if (loading){
      Log.d("LOADING FRAG", "YES")
      (childFragmentManager.findFragmentByTag(PROGRESS_LOADING) as? ProgressDialogFragment)?.dismiss()
      ProgressDialogFragment().show(childFragmentManager, PROGRESS_LOADING)
    } else {
      Log.d("NOT LOADING FRAG", "YES")
      dismissLoadingDialog()
    }
  }

  override fun onError(message: String, callbacks: Collection<() -> Unit>?, startLoading: ((Int) -> Unit)?) {
    Log.d("ERROR FRAG", "YES")
    (childFragmentManager.findFragmentByTag(PROGRESS_LOADING) as? ILoadingCommunicator)?.onError(message, callbacks, startLoading)
  }

  private fun dismissLoadingDialog(){
    childFragmentManager.fragments.filterIsInstance<ProgressDialogFragment>().forEach {
      it.dismiss()
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(IMAGE_PATH, imagePath)
  }

}