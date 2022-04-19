package com.final_project.crowd_counting.base.communicator

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class ActivityObserver(private val callback: () -> Unit): DefaultLifecycleObserver {
  override fun onCreate(owner: LifecycleOwner) {
    super.onCreate(owner)
    owner.lifecycle.removeObserver(this)
    callback()
  }
}