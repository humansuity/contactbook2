package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import com.example.contactbook.databinding.FragmentFirstLoginBinding
import kotlinx.android.synthetic.main.fragment_first_login.*
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.utils.ORGANIZATIONUNITLIST

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentFirstLoginBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var downloadType: String


    companion object {
        fun newInstance(key: String, value: String) : LoginFragment  {
            val fragment = LoginFragment()
            fragment.arguments = Bundle().apply { putString(key,value) }
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_first_login,
            container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(BranchListViewModel::class.java)
        viewModel.optionMenuStateCallback?.invoke("INVISIBLE")
        viewModel.appToolbarStateCallback?.invoke("Авторизация", false)
        viewModel.floatingButtonState.value = false

        arguments?.takeIf { it.containsKey("TYPE") }?.apply {
            downloadType = getString("TYPE")!!
        }

        val realmArray = arrayListOf<String>()
        ORGANIZATIONUNITLIST.forEach { realmArray.add(it.name) }
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, realmArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        loginButton.setOnClickListener {
            viewModel.tryToLogin(ORGANIZATIONUNITLIST.find { it.name == spinner.selectedItem }?.code, loginUsernameText.text.toString(), loginPasswordText.text.toString())
        }

        viewModel.userLoginState.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (downloadType == "DOWNLOAD")
                    viewModel.startDownloadingDB()
                else
                    viewModel.startUpdatingDB()
                viewModel.afterSuccessLoginCallback?.invoke()
            }
        })
    }


}