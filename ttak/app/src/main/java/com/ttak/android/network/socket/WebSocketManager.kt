import android.content.Context
import android.util.Log
import com.ttak.android.network.socket.SocketEvent
import com.ttak.android.utils.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class WebSocketManager private constructor(private val applicationContext: Context) {
    private val TAG = "WebSocketManager"
    private var stompClient: StompClient? = null
    private val disposables = CompositeDisposable()
    private var lifecycleDisposable: Disposable? = null
    private var topicDisposable: Disposable? = null

    private val _socketEvents = MutableSharedFlow<SocketEvent>()
    val socketEvents = _socketEvents.asSharedFlow()

    companion object {
        private const val BASE_URL = "https://k11a509.p.ssafy.io"
        private const val SOCKET_URL = "$BASE_URL/wss/websocket"
        const val FRIEND_STATUS_TOPIC = "/topic/friend-status"
        const val STATUS_FALSE = 0
        const val STATUS_TRUE = 1

        @Volatile
        private var instance: WebSocketManager? = null

        fun getInstance(context: Context): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun connect() {
        try {
            val userId = UserPreferences(applicationContext).getUserId()
            if (userId == null) {
                Log.e(TAG, "Failed to connect: userId is null")
                return
            }

            Log.d(TAG, "Connecting to WebSocket with userId: $userId")

            // 이미 연결되어 있다면 먼저 연결 해제
            disconnect()

            // 로깅 인터셉터 설정
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d(TAG, "WebSocket HTTP: $message")
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // OkHttpClient 설정
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            Log.d(TAG, "Attempting to create STOMP client with URL: $SOCKET_URL")

            // STOMP 클라이언트 생성
            stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                SOCKET_URL,
                mapOf("user" to userId.toString()),
                client
            ).apply {
                withClientHeartbeat(10000)
                withServerHeartbeat(10000)
            }

            // 연결 상태 모니터링
            lifecycleDisposable = stompClient!!.lifecycle()
                .subscribeOn(Schedulers.io())
                .subscribe { lifecycleEvent ->
                    when (lifecycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.d(TAG, "STOMP connection opened")
                            // 약간의 지연 후 구독 시도
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(500) // 500ms 대기
                                _socketEvents.emit(SocketEvent.Connected)
                                Log.d(TAG, "Attempting to subscribe after connection opened")
                                subscribeToFriendStatus()
                            }
                        }
                        LifecycleEvent.Type.CLOSED -> {
                            Log.d(TAG, "STOMP connection closed")
                            CoroutineScope(Dispatchers.IO).launch {
                                _socketEvents.emit(SocketEvent.Disconnected)
                            }
                        }
                        LifecycleEvent.Type.ERROR -> {
                            Log.e(TAG, "STOMP connection error: ${lifecycleEvent.exception}")
                            lifecycleEvent.exception?.printStackTrace()
                            CoroutineScope(Dispatchers.IO).launch {
                                lifecycleEvent.exception?.let {
                                    _socketEvents.emit(SocketEvent.Error(it))
                                }
                            }
                        }
                        else -> Log.d(TAG, "STOMP lifecycle event: ${lifecycleEvent.type}")
                    }
                }
            disposables.add(lifecycleDisposable!!)

            Log.d(TAG, "Starting STOMP connection to: $SOCKET_URL")
            stompClient?.connect()

        } catch (e: Exception) {
            Log.e(TAG, "Error during connect", e)
            CoroutineScope(Dispatchers.IO).launch {
                _socketEvents.emit(SocketEvent.Error(e))
            }
        }
    }

    private fun subscribeToFriendStatus() {
        Log.d(TAG, """
        WebSocket Connection Details:
        Base URL: $BASE_URL
        WebSocket URL: $SOCKET_URL
        Subscribe Topic: $FRIEND_STATUS_TOPIC
        Full STOMP URL: ${SOCKET_URL}
        Protocol: STOMP
    """.trimIndent())

        if (stompClient == null) {
            Log.e(TAG, "Failed to subscribe: stompClient is null")
            return
        }

        if (!stompClient!!.isConnected) {
            Log.e(TAG, "Failed to subscribe: STOMP client is not connected")
            return
        }

        topicDisposable = stompClient!!.topic(FRIEND_STATUS_TOPIC)
            .subscribeOn(Schedulers.io())
            .subscribe({ topicMessage ->
                Log.d(TAG, "Successfully subscribed and received message")
                Log.d(TAG, "Message payload: ${topicMessage.payload}")

                CoroutineScope(Dispatchers.IO).launch {
                    _socketEvents.emit(SocketEvent.MessageReceived(topicMessage.payload))
                }
            }, { error ->
                Log.e(TAG, "Subscription error for topic $FRIEND_STATUS_TOPIC: ${error.message}")
                Log.e(TAG, "Detailed subscription error: ", error)
                error.printStackTrace()

                CoroutineScope(Dispatchers.IO).launch {
                    _socketEvents.emit(SocketEvent.Error(error))
                }
            }, {
                // onComplete
                Log.d(TAG, "Subscription completed for topic: $FRIEND_STATUS_TOPIC")
            })

        disposables.add(topicDisposable!!)
        Log.d(TAG, "Subscription request sent for topic: $FRIEND_STATUS_TOPIC")
    }

    fun sendStatusUpdate(state: Int, userId: Long) {
        if (stompClient == null || !stompClient!!.isConnected) {
            Log.e(TAG, "Cannot send status update: STOMP client is null or not connected")
            return
        }

        val message = """
            {
                "state": $state,
                "userId": $userId
            }
        """.trimIndent()

        Log.d(TAG, "Sending status update: $message")

        stompClient?.send("/app/status", message)?.subscribe(
            {
                Log.d(TAG, "Status update sent successfully")
            },
            { error ->
                Log.e(TAG, "Error sending status update", error)
            }
        )?.let { disposables.add(it) }
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting STOMP client")
        try {
            lifecycleDisposable?.dispose()
            topicDisposable?.dispose()
            disposables.clear()
            stompClient?.disconnect()
            stompClient = null
        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect", e)
        }
    }
}