package com.final_project.crowd_counting.base.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.final_project.crowd_counting.base.constant.Constant

abstract class FragmentWithPermissionChecker: Fragment() {
  protected fun checkPermission(): Boolean {
    val currentAPIVersion = Build.VERSION.SDK_INT
    return if (currentAPIVersion >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(
          requireContext(),
          Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        if (shouldShowRequestPermissionRationale(
            Manifest.permission.READ_EXTERNAL_STORAGE
          )
        ) {
          showDialog(
            "External storage", Manifest.permission.READ_EXTERNAL_STORAGE
          )
        } else {
          requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
          )
        }
        false
      } else {
        true
      }
    } else {
      true
    }
  }

  private fun showDialog(message: String, permissionType: String) {
    val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
    alertBuilder.setCancelable(true)
    alertBuilder.setTitle("Permission necessary")
    alertBuilder.setMessage("$message permission is necessary")
    alertBuilder.setPositiveButton(android.R.string.yes
    ) { dialog, _ ->
      requestPermissions(
        arrayOf(permissionType),
        Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
      )
    }
    val alert: AlertDialog = alertBuilder.create()
    alert.show()
  }
}