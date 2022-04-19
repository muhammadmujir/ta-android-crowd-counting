package com.final_project.crowd_counting.base.communicator

enum class LoadingMode{
  ONLY_PROGRESS_BAR,
  PROGRESS_BAR_WITH_BACKGROUND
}

interface ILoadingCommunicator {
  fun isLoading(loading: Boolean)
  fun onError(message: String, callbacks: Collection<() -> Unit>?, startLoading: ((Int) -> Unit)?)
}