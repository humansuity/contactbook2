package net.gas.gascontact.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_first_login.*
import net.gas.gascontact.R
import net.gas.gascontact.business.adapters.SpinnerRealmAdapter
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.databinding.FragmentFirstLoginBinding
import net.gas.gascontact.utils.ORGANIZATIONUNITLIST
import net.gas.gascontact.utils.Var

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentFirstLoginBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var downloadType: String
    private var selectedRealm = ""
    private var isLoginButtonActive = false


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

        viewModel.onUnitFragmentBackPressed = {
            if (viewModel.parentId == 0) {
                viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
            }
        }

        arguments?.takeIf { it.containsKey("TYPE") }?.apply {
            downloadType = getString("TYPE")!!
            Log.e("KEY", downloadType)
        }

        val realmArray = arrayListOf<String>()
        val icons = arrayListOf<Int>()
        ORGANIZATIONUNITLIST.forEach { icons.add(it.image) }
        ORGANIZATIONUNITLIST.forEach { realmArray.add(it.name) }
        val adapter = SpinnerRealmAdapter(requireContext(), R.layout.spinner_realm_row, realmArray, icons)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        if (downloadType == "UPDATE") {
            viewModel.appToolbarStateCallback?.invoke("Авторизация", true)
            val preferences =
                context?.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            preferences?.let {
                if (preferences.contains("REALM")) {
                    spinner.setSelection(realmArray.indexOf(ORGANIZATIONUNITLIST.find {
                        it.code == preferences.getString(
                            "REALM",
                            "topgas"
                        )
                    }?.name))
                    spinner.isEnabled = false
                    spinner.isActivated = false
                }
            }
        }


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedRealm = realmArray[p2]
                if (downloadType != "UPDATE") {
                    viewModel.putRealmToConfig(ORGANIZATIONUNITLIST.find { it.name == selectedRealm }?.code!!)
                }
            }
        }

        loginButton.setOnClickListener {
            isLoginButtonActive = true
            viewModel.tryToLogin(
                ORGANIZATIONUNITLIST.find { it.name == selectedRealm }?.code,
                loginUsernameText.text.toString(),
                loginPasswordText.text.toString()
            )
        }

        viewModel.userLoginState.observe(viewLifecycleOwner, Observer {
            if (isLoginButtonActive) {
                if (!it.isNullOrBlank()) {
                    findNavController().navigate(R.id.AlertFragment)
                    viewModel.makeRequestToDatabase(it, selectedRealm, downloadType)
                }
            }
        })
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
                }

            })
        }

        return animation
    }


}
