package com.final_project.crowd_counting.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.databinding.FragmentCameraDetailBinding
import com.final_project.crowd_counting.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

const val ARG_RTSP_ADDRESS = "rtspAddress"

@AndroidEntryPoint
class CameraDetailFragment : BaseFragment<FragmentCameraDetailBinding, HomeViewModel>() {
  private var param1: String? = null
  private val viewModel: HomeViewModel by viewModels()
  private lateinit var mediaPlayer: MediaPlayer
  private lateinit var libVLC: LibVLC
  private lateinit var rtspAddress: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      rtspAddress = it.getString(ARG_RTSP_ADDRESS, "http://210.148.114.53/-wvhttp-01-/GetOneShot?image_size=640x480&frame_count=1000000000")
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initVideoView()
  }

  override fun getVM(): HomeViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = false

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentCameraDetailBinding {
    return FragmentCameraDetailBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = true

  private fun initVideoView(){
    viewBinding.tvTitle.text = rtspAddress
    Toast.makeText(requireContext(), rtspAddress, Toast.LENGTH_LONG).show()
    libVLC = LibVLC(requireContext(), arrayListOf("--rtsp-tcp", "--vout=android-display", "-vvv", "--no-audio"))
//    libVLC = LibVLC(requireContext())
    mediaPlayer = MediaPlayer(libVLC)
//    mediaPlayer.vlcVout.setWindowSize(300, 300)
    mediaPlayer.attachViews(viewBinding.vvCamera, null, false, false)
    val media = Media(libVLC, Uri.parse(rtspAddress))
    media.setHWDecoderEnabled(true, false)
//    media.addOption(":network-caching=600")
    mediaPlayer.media = media
    media.release()
    mediaPlayer.play()
  }

  private fun stopMediaPlayer(){
    mediaPlayer.stop()
    mediaPlayer.detachViews()
  }

  private fun destroyMediaPlayer(){
    mediaPlayer.release()
    libVLC.release()
  }

  override fun onStop() {
    super.onStop()
    stopMediaPlayer()
  }

  override fun onDestroy() {
    super.onDestroy()
    destroyMediaPlayer()
  }

  companion object {
    @JvmStatic fun newInstance(param1: String) = CameraDetailFragment().apply {
      arguments = Bundle().apply {
        putString(ARG_RTSP_ADDRESS, param1)
      }
    }
  }
}