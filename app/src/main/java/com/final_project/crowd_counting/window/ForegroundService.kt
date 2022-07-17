package com.final_project.crowd_counting.window

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.view.isVisible
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.base.constant.Constant
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.model.CameraStreamRequest
import com.final_project.crowd_counting.base.model.CameraStreamResponse
import com.final_project.crowd_counting.home.ARG_CAMERA
import com.final_project.crowd_counting.home.EVENT_CROWD_RESPONSE
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
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


class ForegroundService : Service() {

    private lateinit var mSocket: Socket
    private val gson = Gson()
    private var job: Job? = null
    private var camera: Camera? = null
    private lateinit var window: Window

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        // create the custom or default notification
        // based on the android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground() else {
            startForeground(1, Notification())
            Log.d("startForGr", "Yes")
        }

        // create an instance of Window class
        // and display the content on screen
        window = Window(this)
        window.open()
        window.mView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_parent).setOnClickListener {
            window.mView.findViewById<ImageButton>(R.id.window_close).run {
                isVisible = !isVisible
            }
        }
        Log.d("onCreateService", "yes")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("onStartCommandSerive", intent?.getParcelableExtra<Camera>(ARG_CAMERA).toString())
        intent?.getParcelableExtra<Camera>(ARG_CAMERA)?.let { cam ->
            camera = cam
            initialSocket("")
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // for android version >=O we need to create
    // custom notification stating
    // foreground service is running
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!
        manager.createNotificationChannel(chan)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Service running")
            .setContentText("Displaying over other apps") // this is important, otherwise the notification will show the way
            // you want i.e. it will show some default notification
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    fun initialSocket(token: String) {
        try {
            val myHostnameVerifier = HostnameVerifier { hostname, session -> true }
            val mySSLContext: SSLContext = SSLContext.getInstance("TLS")
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object :
                X509TrustManager {

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
            mSocket = IO.socket(Constant.SERVER_URL +"/camera", opts)
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket.on(Manager.EVENT_ERROR, onEventError)
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
        job = CoroutineScope(Dispatchers.Main).launch {
            Log.d("CrowdResponse: ", response.count.toString())
            window.mView.findViewById<TextView>(R.id.titleText).run {
                text = response.count.toString()
            }
//            with(viewBinding){
//                tvTime.text = com.final_project.crowd_counting.base.utils.Util.millisToDate(
//                    response.time * 1000,
//                    "yyyy-MM-dd HH:mm:ss"
//                )
//                ivCrowdImage.setImageBitmap(
//                    com.final_project.crowd_counting.base.utils.Util.base64ToBitmap(
//                        response.image
//                    )
//                )
//                if (!isCrowdIndicatorSet || isOutOfMaxCrowd != response.count > camera?.maxCrowdCount.orDefaultInt(100000)){
//                    isCrowdIndicatorSet = true
//                    isOutOfMaxCrowd = response.count > camera?.maxCrowdCount.orDefaultInt(100000)
//                    if (isOutOfMaxCrowd){
//                        tvCrowdCount.background = androidx.core.content.ContextCompat.getDrawable(root.context, com.final_project.crowd_counting.R.drawable.bg_box_contained_red)
//                        tvCrowdCount.setTextColor(androidx.core.content.ContextCompat.getColor(root.context, android.R.color.white))
//                        ivCrowdIndicator.setImageDrawable(androidx.core.content.ContextCompat.getDrawable(root.context, com.final_project.crowd_counting.R.drawable.ic_cross_octa_red))
//                        tvCrowdStatus.text = getString(com.final_project.crowd_counting.R.string.dangerous_crowd_status)
//                        tvCrowdStatus.setTextColor(androidx.core.content.ContextCompat.getColor(root.context, com.final_project.crowd_counting.R.color.colorRedDark_C42625))
//                    } else {
//                        tvCrowdCount.background = androidx.core.content.ContextCompat.getDrawable(root.context, com.final_project.crowd_counting.R.drawable.bg_box_contained_yellow)
//                        tvCrowdCount.setTextColor(androidx.core.content.ContextCompat.getColor(root.context, com.final_project.crowd_counting.R.color.colorTextPrimary))
//                        ivCrowdIndicator.setImageDrawable(androidx.core.content.ContextCompat.getDrawable(root.context, com.final_project.crowd_counting.R.drawable.ic_check_shield))
//                        tvCrowdStatus.text = getString(com.final_project.crowd_counting.R.string.safe_crowd_status)
//                        tvCrowdStatus.setTextColor(androidx.core.content.ContextCompat.getColor(root.context, com.final_project.crowd_counting.R.color.colorGreenDark_3E8606))
//                    }
//                }
//                tvCrowdCount.text = getString(com.final_project.crowd_counting.R.string.crowd_is, response.count.toString())
//            }
        }
    }

    private fun stopWebSocket(){
        camera?.id?.let { id ->
            if (this::mSocket.isInitialized){
                Log.d("LEAVE", "YES")
                mSocket.emit("leave", gson.toJson(CameraStreamRequest(id, camera?.rtspAddress.orEmpty())))
                mSocket.disconnect()
                mSocket.off(Socket.EVENT_CONNECT)
                mSocket.off(EVENT_CROWD_RESPONSE)
                mSocket.off(Socket.EVENT_CONNECT_ERROR)
                mSocket.off(Manager.EVENT_ERROR)
            }
        }
    }

    override fun onDestroy() {
        Log.d("serviceDestroy", "yes")
        job?.cancel()
        job = null
        stopWebSocket()
        super.onDestroy()
    }
}