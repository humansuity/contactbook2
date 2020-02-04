package net.gas.contactbook.ui.fragments

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonAdditionalFragmentBinding
import kotlinx.android.synthetic.main.activity_main.*
import net.gas.contactbook.business.database.entities.Persons
import net.gas.contactbook.business.database.entities.Photos
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.utils.GlideApp

class PersonAdditionalFragment : Fragment() {

    private lateinit var binding: PersonAdditionalFragmentBinding
    private lateinit var viewModel: BranchListViewModel
    var personAdditionalFragmentCallback: (() -> Unit)? = null


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
            viewModel.setupPostEntity(personEntity.postID!!.toInt())
            if (personEntity.photoID != null) viewModel.setupPhotoEntity(personEntity.photoID)

            binding.mobileNumber =
                if (personEntity.mobilePhone.isNullOrBlank()) "Не указан" else personEntity.mobilePhone
            binding.workNumber =
                if (personEntity.workPhone.isNullOrBlank()) "Не указан" else "8-0212-" + personEntity.workPhone
            binding.homeNumber =
                if (personEntity.homePhone.isNullOrBlank()) "Не указан" else "8-0212-" + personEntity.homePhone
            binding.email =
                if (personEntity.email.isNullOrBlank()) "Не указан" else personEntity.email

            startObserveEntities(personEntity)
            personAdditionalFragmentCallback?.invoke()
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    @ExperimentalStdlibApi
    fun startObserveEntities(personEntity: Persons) {
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
    fun startObservePhoto(photoEntity: LiveData<Photos>, photoID: Int?) {
        photoEntity.observe(viewLifecycleOwner, Observer {
            if (photoID != null) {
                val decodedString = it.photo!!.decodeToString()
                val byteArray = Base64.decode(decodedString, Base64.DEFAULT)
                GlideApp.with(context!!)
                    .asBitmap()
                    .placeholder(R.drawable.ic_user_30)
                    .load(byteArray)
                    .apply(RequestOptions().transform(RoundedCorners(30)))
                    .into(binding.image)
            } else {
                GlideApp.with(context!!)
                    .asDrawable()
                    .load(context!!.resources.getDrawable(R.drawable.ic_user_30))
                    .into(binding.image)
            }
        })
    }


}