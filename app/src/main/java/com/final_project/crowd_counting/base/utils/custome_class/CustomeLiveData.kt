package com.final_project.crowd_counting.base.utils.custome_class

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class CustomeLiveData<T> : MutableLiveData<T>() {

  private val observers: MutableList<Pair<LifecycleOwner, Observer<in T?>>> = ArrayList()

  @MainThread
  override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
    observers.add(Pair(owner, observer))
    super.observe(owner, observer)
  }

  @MainThread
  private fun resetAllObservers() {
    for ((first, second) in observers) {
      super.observe(first, second)
    }
  }

  @MainThread
  private fun removeAllObservers() {
    for ((first) in observers) {
      removeObservers(first)
    }
  }

  override fun removeObserver(observer: Observer<in T?>) {
    for (observerItem in observers) {
      if (observerItem.second == observer && observerItem.first.lifecycle.currentState === Lifecycle.State.DESTROYED) {
        observers.remove(observerItem)
      }
    }
    super.removeObserver(observer)
  }

  @MainThread
  fun setValueWithoutNotify(value: T) {
    removeAllObservers()
    super.setValue(value)
  }

  override fun setValue(value: T) {
    super.setValue(value)
    if (!hasObservers()) {
      resetAllObservers()
    }
  }
}