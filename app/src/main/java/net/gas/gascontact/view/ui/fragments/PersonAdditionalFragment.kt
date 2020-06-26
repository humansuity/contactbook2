package net.gas.gascontact.view.ui.fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import net.gas.gascontact.R
import net.gas.gascontact.database.entities.Persons
import net.gas.gascontact.database.entities.Photos
import net.gas.gascontact.databinding.PersonAdditionalFragmentBinding
import net.gas.gascontact.utils.Constants
import net.gas.gascontact.utils.GlideApp
import net.gas.gascontact.view.viewmodel.BranchListViewModel

class PersonAdditionalFragment : Fragment() {

    private lateinit var binding: PersonAdditionalFragmentBinding
    private val viewModel by activityViewModels<BranchListViewModel>()
    private var photoByteArray: ByteArray? = null
    private val screenOrientation by lazy { activity?.resources?.configuration?.orientation!! }


    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.person_additional_fragment,
            container, false
        )
        viewModel.spinnerState.value = true
        viewModel.appToolbarStateCallback?.invoke("Сотрудники", true)
        viewModel.optionMenuStateCallback?.invoke("INVISIBLE")
        viewModel.isPersonFragmentActive = false
        Constants.hideSpinnerOnOrientationChanged(viewModel, screenOrientation)


        arguments?.let {
            val person = PersonAdditionalFragmentArgs.fromBundle(it).person
            setupData(person)
            setupListeners(person)
            updateUI()
        }

        viewModel.floatingButtonState.value = false
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }


    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation = super.onCreateAnimation(transit, enter, nextAnim)
        if (animation == null && nextAnim != 0)
            animation = AnimationUtils.loadAnimation(requireContext(), nextAnim)

        if (animation != null) {
            view?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    view?.setLayerType(View.LAYER_TYPE_NONE, null)
                    viewModel.spinnerState.value = false
                }

                override fun onAnimationStart(animation: Animation?) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) { delay(200) }
                }

            })
        }

        return animation
    }


    @ExperimentalStdlibApi
    private fun setupData(personEntity: Persons) {
        viewModel.viewModelScope.launch(Dispatchers.Default) {
            delay(200)
            viewModel.setupPostEntity(personEntity.postID!!.toInt())
            viewModel.setupUnitEntity(personEntity.unitID!!.toInt())
            if (personEntity.departmentID != null)
                viewModel.setupDepartmentEntity(personEntity.departmentID)
            else binding.department = "Отдел не определён"
            launch(Dispatchers.Main) { startObserveEntities(personEntity) }
        }
        binding.name =
            personEntity.lastName + " " + personEntity.firstName + " " + personEntity.patronymic
        binding.birthday = personEntity.birthday
        binding.person = personEntity
        binding.devPhoneNumber = personEntity.mobilePhone
        if (personEntity.photoID != null) viewModel.setupPhotoEntity(personEntity.photoID)
        binding.mobileNumber = setupPhoneNumber(personEntity.mobilePhone?.trim(), "MOBILE")
        binding.workNumber = setupPhoneNumber(personEntity.workPhone?.trim(), "WORK")
        binding.homeNumber = setupPhoneNumber(personEntity.homePhone?.trim(), "HOME")
        binding.innerWorkNumber = setupPhoneNumber(personEntity.shortPhone?.trim(), "INNER_WORK")
        binding.email = if (
            personEntity.email.isNullOrBlank()
            || !personEntity.email.contains("@")
        ) {
            "Не указан"
        } else {
            personEntity.email
        }
    }


    private fun updateUI() {
        if (binding.mobileNumber == "Не указан") {
//            binding.callImage1.visibility = View.GONE
//            binding.textMobile.visibility = View.GONE
//            binding.mobileDescription.visibility = View.GONE
            binding.mobileCallImage.visibility = View.GONE
        }
        if (binding.workNumber == "Не указан") {
//            binding.callImage2.visibility = View.GONE
//            binding.textWorkMobile.visibility = View.GONE
//            binding.workDescription.visibility = View.GONE
            binding.workCallImage.visibility = View.GONE
        }
        if (binding.homeNumber == "Не указан") {
//            binding.callImage3.visibility = View.GONE
//            binding.textHomeMobile.visibility = View.GONE
//            binding.homeDescription.visibility = View.GONE
            binding.homeCallImage.visibility = View.GONE
        }
        if (binding.email == "Не указан") {
//            binding.emailImage.visibility = View.GONE
//            binding.textEmail.visibility = View.GONE
//            binding.emailDescription.visibility = View.GONE
            binding.sendEmailImage.visibility = View.GONE
        }

        if (binding.innerWorkNumber == "Не указан") {
//            binding.emailImage.visibility = View.GONE
//            binding.textEmail.visibility = View.GONE
//            binding.emailDescription.visibility = View.GONE
            binding.innerWorkNumberImage.visibility = View.GONE
        }
    }


    private fun setupPhoneNumber(number: String?, flag: String): String {
        return when {
            number.isNullOrBlank() -> "Не указан"
            number.contains(",") -> {
                formatNumber(
                    number.substring(0, number.indexOf(",")),
                    flag
                )
                    .plus("\n")
                    .plus(
                        formatNumber(
                            number.substring(number.indexOf(",") + 1, number.length),
                            flag
                        )
                    )
            }
            number.contains(";") -> {
                formatNumber(
                    number.substring(0, number.indexOf(";")),
                    flag
                )
                    .plus("\n")
                    .plus(
                        formatNumber(
                            number.substring(number.indexOf(";") + 1, number.length),
                            flag
                        )
                    )
            }
            else -> formatNumber(number, flag)
        }
    }

    @ExperimentalStdlibApi
    private fun setupListeners(personEntity: Persons) {
        binding.addContactButton.setOnClickListener {
            when {
                personEntity.mobilePhone.isNullOrBlank() ->
                    Snackbar.make(
                        binding.root, "Невозможно определить номер!",
                        Snackbar.LENGTH_LONG
                    ).show()
                personEntity.mobilePhone.contains(",") -> {
                    val optionArray = makeOptionMobileArray(personEntity.mobilePhone)
                    val dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setTitle("Выберите номер")
                    dialogBuilder.setSingleChoiceItems(optionArray, -1)
                    { dialog, which ->
                        dialog.dismiss()
                        openContactDialog(personEntity, optionArray[which], binding.unit!!)
                    }
                    dialogBuilder.create().show()
                }
                personEntity.mobilePhone.contains(";") -> {
                    val optionArray = makeOptionMobileArray(personEntity.mobilePhone)
                    val dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setTitle("Выберите номер")
                    dialogBuilder.setSingleChoiceItems(optionArray, -1)
                    { dialog, which ->
                        dialog.dismiss()
                        openContactDialog(personEntity, optionArray[which], binding.unit!!)
                    }
                    dialogBuilder.create().show()
                }
                else -> openContactDialog(
                    personEntity,
                    personEntity.mobilePhone,
                    binding.unit!!
                )
            }
        }

        binding.mobileNumberFrame.setOnClickListener {
            when {
                personEntity.mobilePhone.isNullOrBlank() ->
                    Snackbar.make(
                        binding.root, "Невозможно определить номер!",
                        Snackbar.LENGTH_LONG
                    ).show()
                personEntity.mobilePhone.contains(",") -> {
                    openOptionNumberDialog(makeOptionMobileArray(personEntity.mobilePhone))
                }
                personEntity.mobilePhone.contains(";") -> {
                    openOptionNumberDialog(makeOptionMobileArray(personEntity.mobilePhone))
                }
                else -> {
                    val callIntent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:${personEntity.mobilePhone}")
                    )
                    viewModel.makePhoneCall(callIntent)
                }
            }
        }

        binding.workNumberFrame.setOnClickListener {
            when {
                personEntity.workPhone.isNullOrBlank() ->
                    Snackbar.make(
                        binding.root, "Невозможно определить номер!",
                        Snackbar.LENGTH_LONG
                    ).show()
                personEntity.workPhone.contains(",") -> {
                    openOptionNumberDialog(makeOptionMobileArray(personEntity.workPhone))
                }
                personEntity.workPhone.contains(";") -> {
                    openOptionNumberDialog(makeOptionMobileArray(personEntity.workPhone))
                }
                else -> {
                    val callIntent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:${personEntity.workPhone}")
                    )
                    viewModel.makePhoneCall(callIntent)
                }
            }
        }

        binding.homeNumberFrame.setOnClickListener {
            when {
                personEntity.homePhone.isNullOrBlank() ->
                    Snackbar.make(
                        binding.root, "Невозможно определить номер!",
                        Snackbar.LENGTH_LONG
                    ).show()
                personEntity.homePhone.contains(",") -> {
                    openOptionNumberDialog(makeOptionMobileArray(personEntity.homePhone))
                }
                else -> {
                    val callIntent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:${personEntity.homePhone}")
                    )
                    viewModel.makePhoneCall(callIntent)
                }
            }
        }

        binding.innerWorkFrame.setOnClickListener {
            when {
                personEntity.shortPhone.isNullOrBlank() ->
                    Snackbar.make(
                        binding.root, "Невозможно определить номер!",
                        Snackbar.LENGTH_LONG
                    ).show()
                personEntity.shortPhone.contains(",") -> {
                    openOptionNumberDialog(makeOptionMobileArray(personEntity.shortPhone))
                }
                else -> {
                    val callIntent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:${personEntity.shortPhone}")
                    )
                    viewModel.makePhoneCall(callIntent)
                }
            }
        }

        binding.emailFrame.setOnClickListener {
            when {
                personEntity.email.isNullOrBlank() || !personEntity.email.contains("@") ->
                    Snackbar.make(
                        binding.root, "Невозможно определить email!",
                        Snackbar.LENGTH_LONG
                    ).show()
                personEntity.email.contains("@") -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:" + personEntity.email)
                    }
                    viewModel.sendEmail(intent)
                }
                else -> {
                }
            }
        }


        viewModel.onBackButtonPressed = {
            requireActivity().finish()
        }
    }


    private fun makeOptionMobileArray(phoneNumber: String): Array<String> {
        val firstNumber = phoneNumber
            .substring(0, phoneNumber.indexOf(";"))
        val secondNumber = phoneNumber
            .substring(
                phoneNumber.indexOf(";") + 1,
                phoneNumber.length
            )
        return arrayOf(firstNumber, secondNumber)
    }


    private fun openOptionNumberDialog(optionArray: Array<String>) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Выберите номер")
        dialogBuilder.setSingleChoiceItems(optionArray, -1) { dialog, which ->
            dialog.dismiss()
            val callIntent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:${optionArray[which]}")
            )
            viewModel.makePhoneCall(callIntent)
        }
        dialogBuilder.create().show()
    }

    @ExperimentalStdlibApi
    private fun openContactDialog(person: Persons, number: String, unit: String) {
        viewModel.spinnerState.value = true
        GlobalScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) { createDialog(person, number, unit) }
            viewModel.spinnerState.postValue(false)
        }
    }

    private fun formatNumber(phoneNumber: String, flag: String): String {
        return when (flag) {
            "MOBILE" -> {
                if (phoneNumber.contains("+")) {
                    val code = phoneNumber.substring(0, 4)
                    val prefix = phoneNumber.substring(4, 6)
                    val number = phoneNumber.substring(6, phoneNumber.length)
                    val firstNumber = number.substring(0, 3)
                    val secondNumber = number.substring(3, 5)
                    val thirdNumber = number.substring(5, number.length)
                    "$code ($prefix) $firstNumber-$secondNumber-$thirdNumber"
                } else phoneNumber

            }
            "HOME", "WORK" -> {
                when (phoneNumber.length) {
                    7 -> {
                        val firstPart = phoneNumber.substring(0, 2)
                        val secondPart = phoneNumber.substring(2, 5)
                        val thirdPart = phoneNumber.substring(5, 7)
                        "$firstPart$secondPart$thirdPart"
                    }
                    8 -> {
                        val firstPart = phoneNumber.substring(0, 3)
                        val secondPart = phoneNumber.substring(3, 6)
                        val thirdPart = phoneNumber.substring(6, 8)
                        "$firstPart$secondPart$thirdPart"
                    }
                    else -> phoneNumber
                }
            }
            "INNER_WORK" -> {
                if (phoneNumber.length == 5) {
                    val firstPart = phoneNumber[0]
                    val secondPart = phoneNumber.substring(1, 3)
                    val thirdPart = phoneNumber.substring(3, 5)
                    "$firstPart-$secondPart-$thirdPart"
                } else phoneNumber
            }
            else -> "Указан неверно"
        }
    }

    @ExperimentalStdlibApi
    private fun startObserveEntities(personEntity: Persons) {
        viewModel.postEntity
            .observe(viewLifecycleOwner, Observer {
                if (it != null)
                    binding.post = it.name
                else
                    binding.post = "Должность не определена"
            })

        viewModel.unitEntity
            .observe(viewLifecycleOwner, Observer {
                if (it != null)
                    binding.unit = it.name
                else
                    binding.unit = "Филиал не определён"
            })

        viewModel.departmentEntity
            .observe(viewLifecycleOwner, Observer {
                if (it != null)
                    binding.department = it.name
                else
                    binding.department = "Отдел не определён"
            })

        startObservePhoto(viewModel.photoEntity, personEntity.photoID)
    }

    @ExperimentalStdlibApi
    private fun startObservePhoto(photoEntity: LiveData<Photos>, photoID: Int?) {
        photoEntity.observe(viewLifecycleOwner, Observer {
            if (photoID != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    val decodedString =
                        withContext(Dispatchers.Default) { it.photo?.decodeToString() }
                    try {
                        val byteArray = withContext(Dispatchers.Default) {
                            Base64.decode(
                                decodedString,
                                Base64.DEFAULT
                            )
                        }
                        photoByteArray = byteArray
                        GlideApp.with(requireContext())
                            .asBitmap()
                            .placeholder(R.drawable.person_undefined)
                            .load(byteArray)
                            .apply(RequestOptions().transform(RoundedCorners(30)))
                            .into(binding.image)
                    } catch (e: Exception) {
                        launch(Dispatchers.Main) {
                            GlideApp.with(binding.root.context)
                                .asDrawable()
                                .load(binding.root.context.resources.getDrawable(R.drawable.person_undefined))
                                .into(binding.image)
                        }
                    }
                }
            } else {
                GlideApp.with(requireContext())
                    .asDrawable()
                    .load(requireContext().resources.getDrawable(R.drawable.person_undefined))
                    .into(binding.image)
            }
        })
    }


    @ExperimentalStdlibApi
    private fun createDialog(person: Persons, currentNumber: String, unit: String) {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.add_contact_dialog, null)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        val background = ColorDrawable(Color.TRANSPARENT)
        val margins = InsetDrawable(background, 50)
        dialog.window?.setBackgroundDrawable(margins)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        view.findViewById<EditText>(R.id.text_phonenumber).setText(currentNumber)
        val name = view.findViewById<EditText>(R.id.text_name).apply { setText(person.firstName) }
        val lastname =
            view.findViewById<EditText>(R.id.text_lastname).apply { setText(person.lastName) }
        val middlename =
            view.findViewById<EditText>(R.id.text_middlename).apply { setText(person.patronymic) }

        view.findViewById<Button>(R.id.canel_button).setOnClickListener { dialog.dismiss() }
        view.findViewById<Button>(R.id.ok_button).setOnClickListener {
            if (lastname.text.isNullOrBlank()
                || name.text.isNullOrBlank()
                || middlename.text.isNullOrBlank()
            ) {
                Snackbar.make(view, "Все поля должны быть заполнены!", Snackbar.LENGTH_LONG).show()
            } else {
                val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                    putExtra(ContactsContract.Intents.Insert.COMPANY, unit)
                    putExtra(ContactsContract.Intents.Insert.PHONE, currentNumber)
                    putExtra(
                        ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    putExtra(
                        ContactsContract.Intents.Insert.NAME,
                        "${lastname.text} ${name.text} ${middlename.text}"
                    )
                    putExtra(
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
                        photoByteArray
                    )
                    if (!person.email.isNullOrBlank())
                        if (person.email.contains("@")) {
                            putExtra(ContactsContract.Intents.Insert.EMAIL, person.email)
                            putExtra(
                                ContactsContract.Intents.Insert.EMAIL_TYPE,
                                ContactsContract.CommonDataKinds.Email.TYPE_WORK
                            )
                        }
                }

                if (person.photoID != null) {
                    viewModel.setupPhotoEntity(person.photoID)
                    viewModel.photoEntity.observe(viewLifecycleOwner, Observer {
                        try {
                            val decodedString = it.photo!!.decodeToString()
                            val byteArray = Base64.decode(decodedString, Base64.DEFAULT)
                            val row = ContentValues()
                            val list = arrayListOf<ContentValues>()
                            row.put(
                                ContactsContract.Contacts.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                            )
                            row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray)
                            list.add(row)
                            intent.putParcelableArrayListExtra(
                                ContactsContract.Intents.Insert.DATA,
                                list
                            )
                            viewModel.addNewContact(intent)
                        } catch (e: KotlinNullPointerException) {
                            viewModel.addNewContact(intent)
                        }
                    })
                } else {
                    viewModel.addNewContact(intent)
                }
            }
        }
        dialog.show()
    }

}
