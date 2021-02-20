package com.animo.ru.ui.change_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.answers.BaseAnswer
import com.animo.ru.utilities.showToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_change_password, container, false)

        navController = findNavController()

        val oldPasswordEditText: TextInputEditText = view.findViewById(R.id.old_password)
        val newPasswordEditText: TextInputEditText = view.findViewById(R.id.new_password)
        val repeatPasswordEditText: TextInputEditText = view.findViewById(R.id.repeat_password)
        val changePasswordBtn: MaterialButton = view.findViewById(R.id.Change)

        changePasswordBtn.setOnClickListener {
            App.mService.changePassword(
                App.user.token!!,
                oldPasswordEditText.text.toString(),
                newPasswordEditText.text.toString(),
                repeatPasswordEditText.text.toString()
            ).enqueue(
                object : Callback<BaseAnswer> {
                    override fun onFailure(call: Call<BaseAnswer>, t: Throwable) {
                        showToast(getString(R.string.error_server_lost))
                    }

                    override fun onResponse(
                        call: Call<BaseAnswer>,
                        response: Response<BaseAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                response.body()!!.text?.let { it1 -> showToast(it1) }
                                navController?.navigate(R.id.nav_profile)
                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
        }

        return view
    }
}