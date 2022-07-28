package com.final_project.crowd_counting.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
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
import com.final_project.crowd_counting.base.utils.Util.base64ToBitmap
import com.final_project.crowd_counting.base.utils.Util.millisToDate
import com.final_project.crowd_counting.base.utils.Util.orDefaultInt
import com.final_project.crowd_counting.base.view.BaseFragment
import com.final_project.crowd_counting.databinding.FragmentCameraDetailBinding
import com.final_project.crowd_counting.home.viewmodel.HomeViewModel
import com.final_project.crowd_counting.window.ForegroundService
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Manager.EVENT_ERROR
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.net.URI
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


const val ARG_CAMERA = "argCamera"
private const val KEY_OUT_OF_MAX = "outOfMax"
const val EVENT_CROWD_RESPONSE = "my_response"
private const val KEY_CROWD_INDICATOR_SET = "crowdIndicatorSet"

@AndroidEntryPoint
class CameraDetailFragment : BaseFragment<FragmentCameraDetailBinding, HomeViewModel>() {
  private val viewModel: HomeViewModel by viewModels()
  private lateinit var mediaPlayer: MediaPlayer
  private lateinit var libVLC: LibVLC
  private lateinit var mSocket: Socket
  private val gson = Gson()
  private val camera: Camera? by lazy { arguments?.getParcelable(ARG_CAMERA) }
  private var isOutOfMaxCrowd = false
  private var isCrowdIndicatorSet = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    savedInstanceState?.let {
      isOutOfMaxCrowd = it.getBoolean(KEY_OUT_OF_MAX)
      isCrowdIndicatorSet = it.getBoolean(KEY_CROWD_INDICATOR_SET)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initCameraData()
    initVideoView()
//    initWebSocket()
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
        inflateMenu(R.menu.toolbar_menu, false)
        setOnMenuItemClickListener(this@CameraDetailFragment
        ) { item ->
          if (item?.itemId == R.id.option_minimize) {
            Log.d("masukItem", "Yes")
            checkOverlayPermission()
            startService()
          }
          false
        }
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
    mediaPlayer.setEventListener { event ->
      when(event.type){
        MediaPlayer.Event.Playing -> {
          viewBinding.progressBar.isVisible = false
          initialSocket("")
        }
      }
    }
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

  fun initialSocket(token: String) {
    try {
      val myHostnameVerifier = HostnameVerifier { hostname, session -> true }
      val mySSLContext: SSLContext = SSLContext.getInstance("TLS")
      val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object : X509TrustManager {

        override fun checkClientTrusted(
          chain: Array<X509Certificate?>?,
          authType: String?
        ) {
        }

        override fun checkServerTrusted(
          chain: Array<X509Certificate?>?,
          authType: String?
        ) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
          return arrayOf()
        }
      })
      mySSLContext.init(null, trustAllCerts, SecureRandom())
      val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
        .writeTimeout(5, TimeUnit.MINUTES) // write timeout
        .readTimeout(5, TimeUnit.MINUTES) // read timeout
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        .hostnameVerifier(myHostnameVerifier)
        .sslSocketFactory(mySSLContext.getSocketFactory(), object : X509TrustManager {
          override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
          override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
          override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
          }
        })
      val okHttpClient: OkHttpClient = clientBuilder.build()
      IO.setDefaultOkHttpCallFactory(okHttpClient)
      IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
      val opts = IO.Options()
      opts.forceNew = true
      opts.callFactory = okHttpClient
      opts.webSocketFactory = okHttpClient
      opts.query = "token=$token"
      mSocket = IO.socket(SERVER_URL+"/camera", opts)
      mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
      mSocket.on(EVENT_ERROR, onEventError)
      mSocket.on(Socket.EVENT_CONNECT, onConnect)
      mSocket.on(EVENT_CROWD_RESPONSE, onJoinRoom)
      mSocket.connect()
      joinRoom()
    } catch (e: URISyntaxException) {
      throw RuntimeException(e)
    } catch (e: NoSuchAlgorithmException) {
      e.printStackTrace()
    } catch (e: KeyManagementException) {
      e.printStackTrace()
    }
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
      Log.d("fail", "Failed to connect to socket server")
    }
    mSocket.on(Socket.EVENT_CONNECT, onConnect)
    mSocket.on(EVENT_CROWD_RESPONSE, onJoinRoom)
    mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
    mSocket.on(EVENT_ERROR, onEventError)
    mSocket.connect()
    joinRoom()
  }

  private fun stopWebSocket(){
    camera?.id?.let { id ->
      if (this::mSocket.isInitialized){
        mSocket.emit("leave", gson.toJson(CameraStreamRequest(id, camera?.rtspAddress.orEmpty())))
        mSocket.disconnect()
        mSocket.off(Socket.EVENT_CONNECT)
        mSocket.off(EVENT_CROWD_RESPONSE)
        mSocket.off(Socket.EVENT_CONNECT_ERROR)
        mSocket.off(EVENT_ERROR)
      }
    }
  }

  private fun joinRoom(){
    camera?.id?.let { id ->
      mSocket.emit("join", gson.toJson(CameraStreamRequest(id, camera?.rtspAddress.orEmpty())))
    }
  }

  private val onConnect = Emitter.Listener {
//    Toast.makeText(requireContext(), "Connected", Toast.LENGTH_SHORT).show()
  }

  private val onConnectError = Emitter.Listener {
    for (err in it){
      Log.d("ConnectError", err.toString())
    }
  }

  private val onEventError = Emitter.Listener {
    for (err in it){
      Log.d("EventError", err.toString())
    }
  }

  private val onJoinRoom = Emitter.Listener {
    val response = gson.fromJson(it[0].toString(), CameraStreamResponse::class.java)
    lifecycleScope.launch(Dispatchers.Main) {
      with(viewBinding){
        tvTime.text = millisToDate(response.time * 1000, "yyyy-MM-dd HH:mm:ss")
//        ivCrowdImage.setImageBitmap(base64ToBitmap(response.image))
        if (!isCrowdIndicatorSet || isOutOfMaxCrowd != response.count > camera?.maxCrowdCount.orDefaultInt(100000)){
          isCrowdIndicatorSet = true
          isOutOfMaxCrowd = response.count > camera?.maxCrowdCount.orDefaultInt(100000)
          if (isOutOfMaxCrowd){
            tvCrowdCount.background = ContextCompat.getDrawable(root.context, R.drawable.bg_box_contained_red)
            tvCrowdCount.setTextColor(ContextCompat.getColor(root.context, android.R.color.white))
            ivCrowdIndicator.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_cross_octa_red))
            tvCrowdStatus.text = getString(R.string.dangerous_crowd_status)
            tvCrowdStatus.setTextColor(ContextCompat.getColor(root.context, R.color.colorRedDark_C42625))
          } else {
            tvCrowdCount.background = ContextCompat.getDrawable(root.context, R.drawable.bg_box_contained_yellow)
            tvCrowdCount.setTextColor(ContextCompat.getColor(root.context, R.color.colorTextPrimary))
            ivCrowdIndicator.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_check_shield))
            tvCrowdStatus.text = getString(R.string.safe_crowd_status)
            tvCrowdStatus.setTextColor(ContextCompat.getColor(root.context, R.color.colorGreenDark_3E8606))
          }
        }
        tvCrowdCount.text = getString(R.string.crowd_is, response.count.toString())
      }
    }
  }

  fun checkOverlayPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(requireContext())) {
        // send user to the device settings
        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivity(myIntent)
      } else {
        Log.d("notNeedPermission", "Yes")
      }
    } else {
      Log.d("aboveM", "Yes")
    }
  }

  fun startService() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // check if the user has already granted
      // the Draw over other apps permission
      if (Settings.canDrawOverlays(requireContext())) {
        // start the service based on the android version
        val intent = Intent(requireContext(), ForegroundService::class.java).apply {
          putExtra(ARG_CAMERA, camera)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          requireContext().startForegroundService(intent)
        } else {
          requireContext().startService(intent)
        }
      }
    } else {
      requireContext().startService(Intent(requireContext(), ForegroundService::class.java))
    }
    requireActivity().finish()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(KEY_OUT_OF_MAX, isOutOfMaxCrowd)
    outState.putBoolean(KEY_CROWD_INDICATOR_SET, isCrowdIndicatorSet)
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