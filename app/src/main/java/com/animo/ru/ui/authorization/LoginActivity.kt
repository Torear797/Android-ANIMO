package com.animo.ru.ui.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.animo.ru.App
import com.animo.ru.App.Companion.deviceInfo
import com.animo.ru.App.Companion.mService
import com.animo.ru.R
import com.animo.ru.models.DeviceInfo
import com.animo.ru.models.User
import com.animo.ru.models.answers.LoginAnswer
import com.animo.ru.models.answers.UserInfoAnswer
import com.animo.ru.ui.menu.MenuActivity
import com.animo.ru.utilities.showToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.orhanobut.hawk.Hawk
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var pass: TextInputLayout
    private lateinit var textInputLayoutLogin: TextInputLayout
    private lateinit var loginBtn: MaterialButton

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        pass = findViewById(R.id.pass)
        textInputLayoutLogin = findViewById(R.id.textInputLayoutLogin)
        loginBtn = findViewById(R.id.login_btn)

        password.setImeActionLabel(
            resources.getString(R.string.action_sign_in),
            EditorInfo.IME_ACTION_GO
        )

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = baseContext.resources.getString(R.string.login_title)
        }

        /*Заполнение информации об устройтсве пользователя*/
        val size = Point()
        val outMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = this.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }

        size.x = outMetrics.widthPixels
        size.y = outMetrics.heightPixels

        deviceInfo = DeviceInfo(
            size, Settings.Secure.getString(
                this.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        )
    }

    override fun onStart() {
        super.onStart()

        val saveLogin = Hawk.get<String>("login", null)
        if (saveLogin != "" && saveLogin != null && saveLogin.isNotBlank())
            username.text = saveLogin.toEditable()

        password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    login(); true
                }
                else -> false
            }
        }

        loginBtn.setOnClickListener {
            login()
        }

        setupListeners()
    }

    private fun login() {
        sendLoginRequest()
    }

    private fun error() {
        showToast(getString(R.string.error_server_lost))
        loginBtn.isEnabled = true
    }

    private fun sendLoginRequest() {
        Hawk.put("login", username.text.toString())
        loginBtn.isEnabled = false

        mService.login(
            username.text.toString(),
            password.text.toString(),
            deviceInfo.deviceId,
            true,
            Build.MANUFACTURER + " " + Build.MODEL,
            "Android"
        ).enqueue(
            object : Callback<LoginAnswer> {
                override fun onFailure(call: Call<LoginAnswer>, t: Throwable) {
                    error()
                }

                override fun onResponse(
                    call: Call<LoginAnswer>,
                    response: Response<LoginAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            response.body()!!.user_id?.let {
                                response.body()!!.token?.let { it1 ->
                                    response.body()!!.exp?.let { it2 ->
                                        response.body()!!.refreshToken?.let { it3 ->
                                            response.body()!!.refreshExp?.let { it4 ->
                                                sendGetUserInfoRequest(
                                                    it,
                                                    it1, it2, it3, it4
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }

                    loginBtn.isEnabled = true
                }
            })
    }

    private fun sendGetUserInfoRequest(
        UserId: Int,
        Token: String,
        exp: String,
        refreshToken: String,
        refreshExp: String
    ) {
        mService.getUserInfo(Token).enqueue(
            object : Callback<UserInfoAnswer> {
                override fun onFailure(call: Call<UserInfoAnswer>, t: Throwable) {
                    error()
                }

                override fun onResponse(
                    call: Call<UserInfoAnswer>,
                    response: Response<UserInfoAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            App.user = User(
                                UserId,
                                response.body()!!.userInfo?.first_name,
                                response.body()!!.userInfo?.surname,
                                response.body()!!.userInfo?.patronymic,
                                response.body()!!.userInfo?.phone,
                                response.body()!!.userInfo?.email,
                                response.body()!!.userInfo?.direction,
                                response.body()!!.userInfo?.role,
                                response.body()!!.userInfo?.regions,
                                response.body()!!.userInfo?.create_date,
                                Token,
                                exp,
                                refreshToken,
                                refreshExp
                            )

                            Hawk.put("user", App.user)
                            Hawk.put("deviceInfo", deviceInfo)

                            startActivity(Intent(applicationContext, MenuActivity::class.java))
                            finish()
                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }

                    loginBtn.isEnabled = true
                }
            })
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.username -> {
                    validateUserName()
                }
                R.id.password -> {
                    if(username.text.toString().length > 3)
                    validatePassword()
                }
            }

            loginBtn.isEnabled = isValidate()
        }
    }

    private fun validatePassword(): Boolean {
        when {
            password.text.toString().trim().isEmpty() -> {
                pass.error = "Обязательное поле!"
                return false
            }
            password.text.toString().length < 5 -> {
                pass.error = "Длина пароля должна быть минимум 5 символов!"
                return false
            }
            else -> {
                pass.isErrorEnabled = false
            }
        }
        return true
    }

    private fun validateUserName(): Boolean {
        if (username.text.toString().trim().isEmpty()) {
            textInputLayoutLogin.error = "Обязательное поле!"
            username.requestFocus()
            return false
        } else {
            textInputLayoutLogin.isErrorEnabled = false
        }
        return true
    }

    private fun setupListeners() {
        username.addTextChangedListener(TextFieldValidation(username))
        password.addTextChangedListener(TextFieldValidation(password))
    }

    private fun isValidate(): Boolean = validateUserName() && validatePassword()

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == EditorInfo.IME_ACTION_SEND || p1 == R.id.password) {
            login()
            return true
        }

        return false
    }
}