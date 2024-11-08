package com.ttak.android.features.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.ttak.android.R
import com.ttak.android.features.auth.ui.screens.LoginScreen
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.domain.model.UserModel
import com.ttak.android.features.mypage.ProfileSetupActivity
import com.ttak.android.features.auth.viewmodel.MemberViewModel
import com.ttak.android.features.auth.viewmodel.MemberViewModelFactory

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val memberViewModel: MemberViewModel by viewModels { MemberViewModelFactory(application) } // Repository 주입

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FirebaseAuth 및 GoogleSignInClient 초기화
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            TtakTheme {
                LoginScreen(onLoginClick = { signIn() })
            }
        }
    }

    // 로그인
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // ???
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // 로그인 실패 시 처리
                Log.e("login", "오류 코드 ${e}")
            }
        }
    }

    // firebase를 통한 구글 계정 연동 로그인
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 계정 정보를 mysql에 보냄
                    val user = auth.currentUser
                    if (user != null) {
                        // 구글 로그인 정보로 UserModel 생성
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                            Log.d("귯", "FCM 토큰은? : ${tokenTask.result}")
                            if (tokenTask.isSuccessful) {
                                val fcmToken = tokenTask.result
                                // UserModel 생성, FCM 토큰 포함
                                val userModel = UserModel(
                                    id = user.uid,
                                    email = user.email ?: "",
                                    profileImage = user.photoUrl.toString(),
                                    fcmToken = fcmToken // FCM 토큰 추가
                                )
                                Log.d("귯", "UserModel with FCM token: $userModel")
                                // ViewModel을 통해 서버로 전송
                                memberViewModel.signIn(userModel)

                                // 로그인 성공 시 계정이 존재하지 않는다면 프로필을 설정하러 이동
                                startActivity(Intent(this, ProfileSetupActivity::class.java))
                                finish()
                            } else {
                                Log.e("이규석", "FCM 토큰 가져오기 실패", tokenTask.exception)
                            }
                        }
                    }
                } else {
                    // 로그인 실패 시 처리
                    Log.e("이규석", "로그인이 실패했습니다.$task")
                }
            }
    }

    // 상수
    companion object {
        private const val RC_SIGN_IN = 9001
    }
}