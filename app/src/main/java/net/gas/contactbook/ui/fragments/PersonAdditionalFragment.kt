package net.gas.contactbook.ui.fragments

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonAdditionalFragmentBinding
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

        viewModel.personEntity.observe(viewLifecycleOwner, Observer { personEntity ->
            binding.name = personEntity.lastName + " " + personEntity.firstName + " " + personEntity.patronymic
            binding.birthday = personEntity.birthday
            binding.person = personEntity
            binding.devPhoneNumber = personEntity.mobilePhone
            viewModel.setupPostEntity(personEntity.postID!!.toInt())
            if (personEntity.photoID != null) viewModel.setupPhotoEntity(personEntity.photoID)

            binding.mobileNumber =
                if (personEntity.mobilePhone.isNullOrBlank()) "Не указан" else formatNumber(personEntity.mobilePhone)
            binding.workNumber =
                if (personEntity.workPhone.isNullOrBlank()) "Не указан" else "8-0212-" + personEntity.workPhone
            binding.homeNumber =
                if (personEntity.homePhone.isNullOrBlank()) "Не указан" else "8-0212-" + personEntity.homePhone
            binding.email =
                if (personEntity.email.isNullOrBlank() || !personEntity.email.contains(".")) "Не указан" else personEntity.email

            startObserveEntities(personEntity)
        })

        viewModel.floatingButtonState.value = false
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
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


}