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
import com.ttak.android.MainActivity
import com.ttak.android.R
import com.ttak.android.features.auth.ui.screens.LoginScreen
import com.ttak.android.common.ui.theme.TtakTheme
import com.ttak.android.domain.model.MemberRequest
import com.ttak.android.features.auth.viewmodel.MemberViewModel
import com.ttak.android.features.auth.viewmodel.MemberViewModelFactory
import com.ttak.android.features.mypage.ProfileSetupActivity
import com.ttak.android.network.util.UserPreferences


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
                                // userModel 생성, FCM 토큰 포함
                                val userModel = MemberRequest(
                                    id = user.uid,
                                    email = user.email ?: "",
                                    profileImage = user.photoUrl.toString(),
                                    fcmToken = fcmToken // FCM 토큰 추가
                                )
                                // 서버로 전송 후 성공 시에만 화면 전환
                                // 내 계정이 이미 존재한다면 닉네임이 있다면 로그인 성공 시 프로필을 설정하러 이동
                                memberViewModel.signIn(userModel) { isSignInSuccessful ->
                                    if (isSignInSuccessful) {
                                        memberViewModel.existNickname { nickname ->
                                            UserPreferences(applicationContext).saveNickname(
                                                nickname
                                            )
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                            finish() // 현재 Activity 종료
                                        }
                                    } else {
                                        Log.e("귯", "프로필 설정 필요")
                                        startActivity(Intent(this, ProfileSetupActivity::class.java))
                                    }
                                }
                            } else {
                                Log.e("귯", "FCM 토큰 가져오기 실패", tokenTask.exception)
                            }
                        }
                    }
                } else {
                    Log.e("귯", "로그인이 실패했습니다.$task")
                    // 로그인 실패 시 Google 로그인 세션 초기화
                    googleSignInClient.signOut()
                }
            }
    }

    // 상수
    companion object {
        private const val RC_SIGN_IN = 9001
    }
}