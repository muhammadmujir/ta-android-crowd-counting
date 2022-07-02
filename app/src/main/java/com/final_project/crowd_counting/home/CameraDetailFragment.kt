package com.final_project.crowd_counting.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.communicator.ActivityObserver
import com.final_project.crowd_counting.base.communicator.IToolbarCommunicator
import com.final_project.crowd_counting.base.constant.Constant.SERVER_URL
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.model.CameraStreamRequest
import com.final_project.crowd_counting.base.model.CameraStreamResponse
import com.final_project.crowd_counting.base.utils.Util.orDefaultInt
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.databinding.FragmentCameraDetailBinding
import com.final_project.crowd_counting.home.viewmodel.HomeViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.net.URI

const val ARG_CAMERA = "argCamera"
private const val KEY_OUT_OF_MAX = "outOfMax"

@AndroidEntryPoint
class CameraDetailFragment : BaseFragment<FragmentCameraDetailBinding, HomeViewModel>() {
  private val viewModel: HomeViewModel by viewModels()
  private lateinit var mediaPlayer: MediaPlayer
  private lateinit var libVLC: LibVLC
  private lateinit var mSocket: Socket
  private val gson = Gson()
  private val camera: Camera? by lazy { arguments?.getParcelable(ARG_CAMERA) }
  private var isOutOfMaxCrowd = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    savedInstanceState?.let {
      isOutOfMaxCrowd = it.getBoolean(KEY_OUT_OF_MAX)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initCameraData()
    initVideoView()
    initWebSocket()
  }

  override fun getVM(): HomeViewModel = viewModel

  override fun shouldHandleLoading(): Boolean = false

  override fun getFragmentBinding(
    inflater: LayoutInflater, container: ViewGroup?
  ): FragmentCameraDetailBinding {
    requireActivity().lifecycle.addObserver(ActivityObserver{
      (requireActivity() as IToolbarCommunicator).run {
        setOnNavigateBack(this@CameraDetailFragment, {
          findNavController().popBackStack()
        }, true)
        setTitleAndDescription(getString(R.string.camera_detail), null)
      }
    })
    return FragmentCameraDetailBinding.inflate(inflater, container, false)
  }

  override fun shouldResetToolbarView(): Boolean = true

  private fun initCameraData(){
    with(viewBinding){
      tvLocation.text = getString(R.string.location_is, camera?.location)
      val textArea = getString(R.string.area_width_is, camera?.area)
      tvArea.text = HtmlCompat.fromHtml(textArea, HtmlCompat.FROM_HTML_MODE_LEGACY)
      tvMaxCrowdCount.text = getString(R.string.max_crowd_is, camera?.maxCrowdCount.toString())
      tvDesc.text = getString(R.string.description_is, camera?.description)
      tvCrowdCount.text = getString(R.string.crowd_is, "-")
    }
  }

  private fun initVideoView(){
    libVLC = LibVLC(requireContext(), arrayListOf("--rtsp-tcp", "--vout=android-display", "-vvv", "--no-audio"))
//    libVLC = LibVLC(requireContext())
    mediaPlayer = MediaPlayer(libVLC)
//    mediaPlayer.vlcVout.setWindowSize(300, 300)
    mediaPlayer.attachViews(viewBinding.vvCamera, null, false, false)
    val media = Media(libVLC, Uri.parse(camera?.rtspAddress))
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

  private fun initWebSocket(){
    try {
      //      This address is the way you can connect to localhost with AVD(Android Virtual Device)
      //      mSocket = IO.socket("http://192.168.43.194:5000/")
      //      Log.d("success", mSocket.id().toString())
      val manager = Manager(URI(SERVER_URL))
      mSocket = manager.socket("/camera")
    } catch (e: Exception) {
      e.printStackTrace()
      Log.d("fail", "Failed to connect")
    }

    mSocket.connect()
    mSocket.on(Socket.EVENT_CONNECT, onConnect)
    mSocket.on("my_response", onJoinRoom)// To know if the new user entered the room.
    joinRoom()
  }

  private fun stopWebSocket(){
    camera?.id?.let { id ->
      mSocket.emit("leave", gson.toJson(CameraStreamRequest(id)))
      mSocket.disconnect()
    }
  }

  private fun joinRoom(){
    camera?.id?.let { id ->
      mSocket.emit("join", gson.toJson(CameraStreamRequest(id)))
    }
  }

  var onConnect = Emitter.Listener {
//    Toast.makeText(requireContext(), "Connected", Toast.LENGTH_SHORT).show()
  }

  var onJoinRoom = Emitter.Listener {
    val response = gson.fromJson(it[0].toString(), CameraStreamResponse::class.java)
    lifecycleScope.launch(Dispatchers.Main) {
      with(viewBinding){
        if (isOutOfMaxCrowd != response.count > camera?.maxCrowdCount.orDefaultInt(100000)){
          isOutOfMaxCrowd = response.count > camera?.maxCrowdCount.orDefaultInt(100000)
          Log.d("Coba", "coba: "+isOutOfMaxCrowd)
          if (isOutOfMaxCrowd){
            tvCrowdCount.background = ContextCompat.getDrawable(root.context, R.drawable.bg_box_contained_red)
            tvCrowdCount.setTextColor(ContextCompat.getColor(root.context, android.R.color.white))
            tvCrowdStatus.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(root.context, R.drawable.ic_cross_octa_red), null, null)
            tvCrowdStatus.text = getString(R.string.dangerous_crowd_status)
            tvCrowdStatus.setTextColor(ContextCompat.getColor(root.context, R.color.colorRedDark_C42625))
          } else {
            tvCrowdCount.background = ContextCompat.getDrawable(root.context, R.drawable.bg_box_contained_yellow)
            tvCrowdCount.setTextColor(ContextCompat.getColor(root.context, R.color.colorTextPrimary))
            tvCrowdStatus.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(root.context, R.drawable.ic_check_shield), null, null)
            tvCrowdStatus.text = getString(R.string.safe_crowd_status)
            tvCrowdStatus.setTextColor(ContextCompat.getColor(root.context, R.color.colorGreenDark_3E8606))
          }
        }
        tvCrowdCount.text = getString(R.string.crowd_is, response.count.toString())
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(KEY_OUT_OF_MAX, isOutOfMaxCrowd)
  }

  override fun onStop() {
    super.onStop()
    stopMediaPlayer()
  }

  override fun onDestroy() {
    super.onDestroy()
    destroyMediaPlayer()
    stopWebSocket()
  }

}