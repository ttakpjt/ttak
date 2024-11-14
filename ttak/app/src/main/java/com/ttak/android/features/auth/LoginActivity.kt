package com.ttak.android.features.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.ttak.android.utils.UserPreferences


class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val memberViewModel: MemberViewModel by viewModels { MemberViewModelFactory(application) } // Repository 주입

    private val signInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.e("login", "firebase 인증 오류 코드 ${e}")
                }
            }
        }

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

    // google firebase를 통해 로그인
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        // startActivityForResult는 최신 안드로이드 버전에서 deprecated 되었기 때문에 변경
        signInResultLauncher.launch(signInIntent)
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
                        // Firebase Messaging 토큰 받기
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
                                memberViewModel.signIn(userModel) { isSignInSuccessful ->
                                    if (isSignInSuccessful) {
                                        // 기존 저장한 닉네임이 있다면 그것을 사용
                                        // 기존 저장한 닉네임이 있다면 그것을 사용
                                        memberViewModel.existNickname { nickname ->
                                            if (nickname != null) {
                                                // 닉네임이 존재하면 MainActivity로 이동
                                                UserPreferences(applicationContext).saveNickname(nickname)
                                                navigateToActivity(MainActivity::class.java)
                                            } else {
                                                // 닉네임이 없으면 ProfileSetupActivity로 이동
                                                navigateToActivity(ProfileSetupActivity::class.java)
                                            }
                                        }
                                    } else {
                                        // 서버 로그인 실패 처리
                                        Toast.makeText(this, "서버 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                        auth.signOut() // Firebase 로그아웃

                                    }
                                }
                            } else {
                                Log.e("귯", "FCM 토큰 가져오기 실패", tokenTask.exception)
                                Toast.makeText(this, "FCM 토큰을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                                auth.signOut() // Firebase 로그아웃
                            }
                        }
                    }
                } else {
                    // Firebase 로그인 실패 처리
                    Log.e("귯", "Firebase 로그인 실패", task.exception)
                    Toast.makeText(this, "Firebase 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    auth.signOut() // Firebase 로그아웃
                }
            }
    }

    // Activity 이동 함수 (매개변수로 이동할 Activity와 추가 Flags를 받음)
    private fun navigateToActivity(targetActivity: Class<out Activity>, clearTask: Boolean = true) {
        val intent = Intent(this, targetActivity)
        if (clearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)   // activity 스택을 새로 시작
        }
        startActivity(intent)
        finish()
    }

    // 상수
    companion object {
        private const val RC_SIGN_IN = 9001
    }
}