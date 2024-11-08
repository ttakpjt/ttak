import android.content.Context
import android.util.Log
import com.ttak.android.network.socket.SocketEvent
import com.ttak.android.network.util.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Interceptor

class WebSocketManager private constructor() {
    private val TAG = "WebSocketManager"
    private var stompClient: StompClient? = null
    private val disposables = CompositeDisposable()

    private val _socketEvents = MutableSharedFlow<SocketEvent>()
    val socketEvents = _socketEvents.asSharedFlow()

    companion object {
        private const val BASE_URL = "https://k11a509.p.ssafy.io/api"
        private const val SOCKET_URL = "$BASE_URL/ws"
        const val FRIEND_STATUS_TOPIC = "/topic/status"
        const val STATUS_FALSE = 0
        const val STATUS_TRUE = 1

        @Volatile
        private var instance: WebSocketManager? = null

        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }

    fun connect(context: Context) {
        val userId = UserPreferences(context).getUserId()

        Log.d(TAG, "Connecting to WebSocket with userId: $userId")

        // 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "WebSocket HTTP: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 사용자 ID를 헤더에 추가하는 인터셉터
        val userInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            if (userId != null) {
                requestBuilder.addHeader("user", userId.toString())
                Log.d(TAG, "Adding user header: $userId")
            }

            val request = requestBuilder.build()
            Log.d(TAG, "Request URL: ${request.url}")
            Log.d(TAG, "Request Headers: ${request.headers}")

            val response = chain.proceed(request)
            Log.d(TAG, "Response Code: ${response.code}")
            Log.d(TAG, "Response Headers: ${response.headers}")

            response
        }

        // OkHttpClient 설정
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(userInterceptor)
            .build()

        // STOMP 클라이언트 생성
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL, null, client).apply {
            withClientHeartbeat(10000)
            withServerHeartbeat(10000)
        }

        // 연결 상태 모니터링
        disposables.add(
            stompClient!!.lifecycle()
                .subscribeOn(Schedulers.io())
                .subscribe { lifecycleEvent ->
                    when (lifecycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> {
                            Log.d(TAG, "STOMP connection opened")
                            CoroutineScope(Dispatchers.IO).launch {
                                _socketEvents.emit(SocketEvent.Connected)
                            }
                            subscribeToFriendStatus()
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
        )

        Log.d(TAG, "Starting STOMP connection to: $SOCKET_URL")
        stompClient?.connect()
    }

    private fun subscribeToFriendStatus() {
        Log.d(TAG, "Subscribing to: $FRIEND_STATUS_TOPIC")
        disposables.add(
            stompClient!!.topic(FRIEND_STATUS_TOPIC)
                .subscribeOn(Schedulers.io())
                .subscribe({ topicMessage ->
                    Log.d(TAG, "Received message: ${topicMessage.payload}")
                    CoroutineScope(Dispatchers.IO).launch {
                        _socketEvents.emit(SocketEvent.MessageReceived(topicMessage.payload))
                    }
                }, { error ->
                    Log.e(TAG, "Error on subscribe: ", error)
                    error.printStackTrace()
                    CoroutineScope(Dispatchers.IO).launch {
                        _socketEvents.emit(SocketEvent.Error(error))
                    }
                })
        )
    }

    fun sendStatusUpdate(state: Int, userId: Long) {
        val message = """
            {
                "state": $state,
                "userId": $userId
            }
        """.trimIndent()

        stompClient?.send("/app/status", message)?.subscribe(
            { Log.d(TAG, "Status update sent successfully") },
            { error -> Log.e(TAG, "Error sending status update", error) }
        )
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting STOMP client")
        disposables.clear()
        stompClient?.disconnect()
        stompClient = null
    }
}