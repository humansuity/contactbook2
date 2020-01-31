package net.gas.contactbook.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonAdditionalFragmentBinding
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.utils.GlideApp
import kotlin.math.log

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

        viewModel.personEntity.observe(viewLifecycleOwner, Observer {
            binding.name = it.lastName + it.firstName + it.patronymic
            binding.birthday = it.birthday
            viewModel.setupPhotoEntity(it.photoID)
            viewModel.photoEntity.observe(viewLifecycleOwner, Observer {
                val decodedString = it.photo!!.decodeToString()
                val byteArray = Base64.decode(decodedString, Base64.DEFAULT)
                GlideApp.with(context!!)
                    .asBitmap()
                    .placeholder(R.drawable.ic_user_30)
                    .load(byteArray)
                    .into(binding.image)
            })
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }
}