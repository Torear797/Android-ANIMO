package com.animo.ru.ui.share

import android.app.Dialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.App
import com.animo.ru.App.Companion.accessSpeciality
import com.animo.ru.R
import com.animo.ru.models.Messenger
import com.animo.ru.models.answers.BaseAnswer
import com.animo.ru.models.answers.GetDoctorsFromSpecAndReg
import com.animo.ru.models.answers.ShareDoctor
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.isAppAvailable
import com.animo.ru.utilities.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShareBottomSheetDialog : BottomSheetDialogFragment(),
    ShareMessengerAdapter.OnMessengerClickListener {

    private var shareMessengersList: MutableMap<Int, Messenger>? = mutableMapOf()
    private var titleText: TextView? = null
    private var doctorsList: Spinner? = null
    private var idObject = 0
    private var typeObject = ""
    private var currentTags = mutableListOf<String>()
    private var recyclerView: RecyclerView? = null
    private var currentDoctors: MutableMap<Int, ShareDoctor>? = null
    private var sendTitle = ""
    private var sendText = ""

    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var chipGroup: ChipGroup? = null

    companion object {
        fun newInstance(id: Int, title: String, text: String, typeObj: String): ShareBottomSheetDialog {
            val fragment = ShareBottomSheetDialog()
            val args = Bundle()
            args.putSerializable("title", title)
            args.putSerializable("text", text)
            args.putSerializable("idObject", id)
            args.putSerializable("typeObject", typeObj)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_share, container, false)
        doctorsList = view.findViewById(R.id.userSelect)

        chipGroup = view.findViewById(R.id.mainTagChipGroup)
        autoCompleteTextView = view.findViewById(R.id.mainTagAutoCompleteTextView)

        accessSpeciality?.values?.toList()?.let {
            loadTagsUi(
                autoCompleteTextView!!, chipGroup!!,
                it
            )
        }

        recyclerView = view.findViewById(R.id.share_messengers)
        addUserMessengers()
        initRecyclerView(recyclerView!!)

        titleText = view.findViewById(R.id.share_title)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        idObject = arguments?.getSerializable("idObject") as Int
        sendTitle = arguments?.getString("title") as String
        sendText = arguments?.getString("text") as String
        typeObject = arguments?.getString("typeObject") as String

        titleText!!.text = sendTitle
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    val newMaterialShapeDrawable: MaterialShapeDrawable =
                        createMaterialShapeDrawable(bottomSheet)
                    ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        return dialog
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable {
        val shapeAppearanceModel =
            //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
            ShapeAppearanceModel.builder(context, 0, R.style.CustomShapeAppearanceBottomSheetDialog)
                .build()

        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottoSheet)
        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        //Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }

    private fun loadTagsUi(
        autoCompleteTextView: AutoCompleteTextView,
        chipGroup: ChipGroup,
        allTags: List<String>
    ) {

        val adapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_dropdown_item_1line,
                allTags
            )
        }
        autoCompleteTextView.setAdapter(adapter)

        fun addTag(name: String) {
            if (name.isNotEmpty() && !currentTags.contains(name) && allTags.contains(name)) {
                currentTags.add(name)
                addChipToGroup(name, chipGroup)
            }
        }

        // select from auto complete
        autoCompleteTextView.setOnItemClickListener { adapterView, _, position, _ ->
            autoCompleteTextView.text = null
            val name = adapterView.getItemAtPosition(position) as String
            addTag(name)
        }

        // done keyboard button is pressed
        autoCompleteTextView.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val name = textView.text.toString()
                textView.text = null
                addTag(name)
                return@setOnEditorActionListener true
            }
            false
        }

        // space or comma is detected
        autoCompleteTextView.addTextChangedListener {
            if (it != null && it.isEmpty()) {
                return@addTextChangedListener
            }

            if (it?.last() == ',' || it?.last() == ' ') {
                val name = it.substring(0, it.length - 1)
                addTag(name)

                autoCompleteTextView.text = null
            }
        }

        // initialize
        for (tag in currentTags) {
            addChipToGroup(tag, chipGroup)
        }
    }

    private fun addChipToGroup(name: String, chipGroup: ChipGroup) {
        val chip = Chip(context)
        chip.text = name

        chip.isClickable = true
        chip.isCheckable = false
        chip.isCloseIconVisible = true

        chipGroup.addView(chip)

        getDoctors()

        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
            currentTags.remove(name)

            if (currentTags.isNotEmpty()) {
                getDoctors()
            } else
                deActiveDoctorsList()
        }
    }

    private fun addUserMessengers() {
        var index = 0
        shareMessengersList?.put(
            index, Messenger(
                index++,
                "Копировать",
                R.drawable.ic_baseline_content_copy_35,
                R.color.copyBackground
            )
        )
        shareMessengersList?.put(
            index, Messenger(
                index++,
                "Email",
                R.drawable.ic_baseline_alternate_email_35,
                R.color.EmailBackground
            )
        )

/*        val intentViber: Intent? =
            requireContext().packageManager.getLaunchIntentForPackage("com.viber.voip")
        val intentWhatsApp: Intent? =
            requireContext().packageManager.getLaunchIntentForPackage("com.whatsapp")
        val intentTelegram: Intent? =
            requireContext().packageManager.getLaunchIntentForPackage("org.telegram.messenger")*/

        val isViberInstalled = isAppAvailable(requireContext(), "com.viber.voip")
        val isWhatsAppInstalled = isAppAvailable(requireContext(), "com.whatsapp")
        val isTelegramInstalled = isAppAvailable(requireContext(), "org.telegram.messenger")

        if (isViberInstalled) {
            shareMessengersList?.put(
                index, Messenger(
                    index++,
                    "Viber",
                    R.drawable.viber_icon_35,
                    R.color.viberBackground
                )
            )
        }

        if (isWhatsAppInstalled) {
            shareMessengersList?.put(
                index, Messenger(
                    index++,
                    "WhatsApp",
                    R.drawable.ic_whatsapp_35,
                    R.color.WhatsAppBackground
                )
            )
        }

        if (isTelegramInstalled) {
            shareMessengersList?.put(
                index, Messenger(
                    index,
                    "Telegram",
                    R.drawable.ic_telegram_35,
                    R.color.TelegramBackground
                )
            )
        }

    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(SpacesItemDecoration(15, 15))
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(
            recyclerView.context,
            4,
            GridLayoutManager.VERTICAL,
            false
        )
        recyclerView.adapter = shareMessengersList?.let { ShareMessengerAdapter(it, this) }
    }

    override fun onItemClick(messenger: Messenger) {
        var typeBtn = "error"
        when (messenger.appName) {
            "Копировать" -> {
                typeBtn = "Скопировать текст в буфер обмена"
            }
            "Email" -> {
                typeBtn = "Email"
            }
            "Viber" -> {
                typeBtn = "Viber"
            }
            "WhatsApp" -> {
                typeBtn = "WhatsApp"
            }
            "Telegram" -> {
                typeBtn = "Telegram"
            }

        }

        sendShareTrackingInfo(typeBtn)
    }

    private fun reInitSpinner(mList: MutableMap<Int, ShareDoctor>) {
        if (currentTags.isNotEmpty() && mList.isNotEmpty()) {
            doctorsList!!.isEnabled = true

            val list = arrayListOf<ShareDoctor>()

            mList.forEach { (_, value) -> list.add(value) }

            doctorsList!!.adapter = context?.let {
                SpinnerWithDoctorsAdapter(
                    it,
                    list
                )
            }

            recyclerView!!.visibility = VISIBLE
        } else {
            deActiveDoctorsList()
        }
    }

    private fun getDoctors() {
        App.mService.getDoctorsFromRegionAndSpec(
            App.user.token!!,
            idObject,
            App.user.id!!,
            getSpecialityIdForName()
        )
            .enqueue(
                object : Callback<GetDoctorsFromSpecAndReg> {
                    override fun onFailure(call: Call<GetDoctorsFromSpecAndReg>, t: Throwable) {}

                    override fun onResponse(
                        call: Call<GetDoctorsFromSpecAndReg>,
                        response: Response<GetDoctorsFromSpecAndReg>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                response.body()!!.data?.let {
                                    reInitSpinner(it)
                                    currentDoctors = it
                                }

                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
    }

    private fun getSpecialityIdForName(): ArrayList<Int> {
        val ids = arrayListOf<Int>()

        accessSpeciality?.forEach { (key, value) ->
            if (currentTags.contains(value)) {
                ids.add(key)
            }
        }

        return ids
    }

    private fun deActiveDoctorsList() {
        doctorsList!!.isEnabled = false
        doctorsList!!.adapter = null
        recyclerView!!.visibility = INVISIBLE
    }

    private fun sendShareTrackingInfo(typeBtn: String) {
        val selectedDoctor: ShareDoctor = doctorsList?.selectedItem as ShareDoctor
        val idDoc = getDoctorIdForValue(selectedDoctor.fio!!)

        App.mService.sendTrackingInfo(
            App.user.token!!,
            typeBtn,
            "mobile",
            idObject,
            typeObject,
            idDoc,
            App.deviceInfo.getResolution(),
            "Android " + android.os.Build.VERSION.RELEASE
        )
            .enqueue(
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

                                sendText = sendText.replace(
                                    "[ИО\\И врача]",
                                    selectedDoctor.io!!,
                                    true
                                )

                                when (typeBtn) {
                                    "Скопировать текст в буфер обмена" -> {
                                        val clipboard: ClipboardManager =
                                            context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(
                                            ClipData.newPlainText(
                                                "share_text",
                                                sendText
                                            )
                                        )
                                        showToast("Текст скопирован")
                                    }
                                    "Email" -> {
                                        val mIntent = Intent(Intent.ACTION_SEND)
                                        mIntent.data = Uri.parse("mailto:")
                                        mIntent.type = "text/plain"
                                        mIntent.putExtra(
                                            Intent.EXTRA_EMAIL,
                                            arrayOf(selectedDoctor.email)
                                        )
                                        mIntent.putExtra(Intent.EXTRA_SUBJECT, sendTitle)
                                        mIntent.putExtra(Intent.EXTRA_TEXT, sendText)
                                        startActivity(
                                            Intent.createChooser(
                                                mIntent,
                                                "Выбирите почтовый клиент"
                                            )
                                        )
                                    }
                                    "Viber" -> {
                                        val uri = Uri.parse("smsto:${selectedDoctor.phone}")
                                        val viberIntent = Intent(Intent.ACTION_SENDTO, uri)
                                        viberIntent.putExtra(Intent.EXTRA_TEXT, sendText)
                                        viberIntent.setPackage("com.viber.voip")
                                        startActivity(
                                            Intent.createChooser(
                                                viberIntent,
                                                "Поделиться"
                                            )
                                        )
                                    }
                                    "WhatsApp" -> {
                                        val sendIntent = Intent("android.intent.action.MAIN")
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, sendText)
                                        sendIntent.component = ComponentName(
                                            "com.whatsapp",
                                            "com.whatsapp.Conversation"
                                        )
                                        sendIntent.putExtra(
                                            "jid",
                                            PhoneNumberUtils.stripSeparators(selectedDoctor.phone) + "@s.whatsapp.net"
                                        )
                                        startActivity(sendIntent)
                                    }
                                    "Telegram" -> {
                                        val myIntent = Intent(Intent.ACTION_SEND)
                                        myIntent.type = "text/plain"
                                        myIntent.setPackage("org.telegram.messenger")
                                        myIntent.putExtra(Intent.EXTRA_TEXT, sendText)
                                        startActivity(myIntent)
                                    }
                                }
                                clearFields()
                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
    }

    private fun getDoctorIdForValue(fio: String): Int {
        currentDoctors?.forEach { (key, value) ->
            if (value.fio.equals(fio)) {
                return key
            }
        }

        return 0
    }

    private fun clearFields() {
        currentTags = mutableListOf()
        chipGroup?.removeAllViews()
        getDoctors()

        accessSpeciality?.values?.toList()?.let {
            loadTagsUi(
                autoCompleteTextView!!, chipGroup!!,
                it
            )
        }
    }
}