package net.gas.contactbook.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonAdditionalFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import net.gas.contactbook.business.database.entities.Persons
import net.gas.contactbook.business.database.entities.Photos
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.utils.GlideApp

class PersonAdditionalFragment : Fragment() {

    private lateinit var binding: PersonAdditionalFragmentBinding
    private lateinit var viewModel: BranchListViewModel


    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.person_additional_fragment,
            container, false
        )
        viewModel = ViewModelProvider(requireActivity())
            .get(BranchListViewModel::class.java)

        viewModel.personEntity.observe(viewLifecycleOwner, Observer
        { personEntity ->
            binding.name = personEntity.lastName + " " + personEntity.firstName + " " + personEntity.patronymic
            binding.birthday = personEntity.birthday
            binding.person = personEntity
            binding.devPhoneNumber = personEntity.mobilePhone
            viewModel.setupPostEntity(personEntity.postID!!.toInt())
            if (personEntity.photoID != null) viewModel.setupPhotoEntity(personEntity.photoID)

            binding.mobileNumber =
                when {
                    personEntity.mobilePhone.isNullOrBlank() -> "Не указан"
                    personEntity.mobilePhone.contains(",") -> {
                        formatNumber(personEntity.mobilePhone
                            .substring(0, personEntity.mobilePhone.indexOf(",")))
                            .plus("\n")
                            .plus(formatNumber(personEntity.mobilePhone
                                        .substring(
                                            personEntity.mobilePhone.indexOf(",") + 1,
                                            personEntity.mobilePhone.length
                                        )))
                    }
                    else -> formatNumber(personEntity.mobilePhone)
                }
            binding.workNumber =
                if (personEntity.workPhone.isNullOrBlank()) "Не указан" else "8-0212-" + personEntity.workPhone
            binding.homeNumber =
                if (personEntity.homePhone.isNullOrBlank()) "Не указан" else "8-0212-" + personEntity.homePhone
            binding.email =
                if (personEntity.email.isNullOrBlank() || !personEntity.email.contains(".")) "Не указан" else personEntity.email

            startObserveEntities(personEntity)

            binding.addContactButton.setOnClickListener {
                when {
                    personEntity.mobilePhone.isNullOrBlank()
                        -> Snackbar.make(binding.root,
                        "Невозможно определить номер!", Snackbar.LENGTH_LONG).show()
                    personEntity.mobilePhone.contains(",") -> {
                        val firstNumber = personEntity.mobilePhone
                            .substring(0, personEntity.mobilePhone.indexOf(","))
                        val secondNumber = personEntity.mobilePhone
                            .substring(
                                personEntity.mobilePhone.indexOf(",") + 1,
                                personEntity.mobilePhone.length
                            )
                        val optionArray = arrayOf(firstNumber, secondNumber)

                        val dialogBuilder = AlertDialog.Builder(context)
                        dialogBuilder.setTitle("Выберите номер")
                        dialogBuilder.setSingleChoiceItems(optionArray, -1) { dialog, which ->
                            dialog.dismiss()
                            openContactDialog(personEntity, optionArray[which], binding.unit!!)
                        }
                        dialogBuilder.create().show()
                    }
                    else -> openContactDialog(personEntity, personEntity.mobilePhone, binding.unit!!                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        )
                }
            }
        })

        viewModel.floatingButtonState.value = false
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    private fun openContactDialog(person: Persons, number: String, unit: String) {
        viewModel.spinnerState.value = true
        GlobalScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) { createDialog(person, number, unit) }
            viewModel.spinnerState.postValue(false)
        }
    }

    private fun formatNumber(phoneNumber: String): String {
        return if (phoneNumber.contains("+")) {
            val code = phoneNumber.substring(0, 4)
            val prefix = phoneNumber.substring(4, 6)
            val number = phoneNumber.substring(6, phoneNumber.length)
            val firstNumber = number.substring(0, 3)
            val secondNumber = number.substring(3, 5)
            val thirdNumber = number.substring(5, number.length)
            "$code ($prefix) $firstNumber-$secondNumber-$thirdNumber"
        } else {
            phoneNumber
        }
    }

    @ExperimentalStdlibApi
    private fun startObserveEntities(personEntity: Persons) {
        viewModel.postEntity
            .observe(viewLifecycleOwner, Observer {
                binding.post = it.name
            })
        viewModel.getUnitEntity(personEntity.unitID!!.toInt())
            .observe(viewLifecycleOwner, Observer {
                binding.unit = it.name
            })
        viewModel.getDepartmentEntity(personEntity.departmentID!!.toInt())
            .observe(viewLifecycleOwner, Observer {
                binding.department = it.name
            })

        startObservePhoto(viewModel.photoEntity, personEntity.photoID)
    }

    @ExperimentalStdlibApi
    private fun startObservePhoto(photoEntity: LiveData<Photos>, photoID: Int?) {
        photoEntity.observe(viewLifecycleOwner, Observer {
            if (photoID != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    val decodedString = withContext(Dispatchers.Default) { it.photo!!.decodeToString() }
                    val byteArray = withContext(Dispatchers.Default) { Base64.decode(decodedString, Base64.DEFAULT) }
                    GlideApp.with(context!!)
                        .asBitmap()
                        .placeholder(R.drawable.ic_user_30)
                        .load(byteArray)
                        .apply(RequestOptions().transform(RoundedCorners(30)))
                        .into(binding.image)
                }
            } else {
                GlideApp.with(context!!)
                    .asDrawable()
                    .load(context!!.resources.getDrawable(R.drawable.ic_user_30))
                    .into(binding.image)
            }
        })
    }


    private fun createDialog(person: Persons, currentNumber: String, unit: String) {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.add_contact_dialog, null)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        val background = ColorDrawable(Color.TRANSPARENT)
        val margins = InsetDrawable(background, 50)
        dialog.window?.setBackgroundDrawable(margins)

        view.findViewById<EditText>(R.id.text_phonenumber).setText(currentNumber)
        val lastname = view.findViewById<EditText>(R.id.text_lastname).apply { setText(person.lastName) }
        val name = view.findViewById<EditText>(R.id.text_name).apply { setText(person.firstName) }
        val middlename = view.findViewById<EditText>(R.id.text_middlename).apply { setText(person.patronymic) }

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
                }
                viewModel.addNewContact(intent)
            }
        }
        dialog.show()
    }


}