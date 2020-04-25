package net.gas.gascontact.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import net.gas.gascontact.R
import net.gas.gascontact.databinding.FragmentFirstLoginBinding
import kotlinx.android.synthetic.main.fragment_first_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gas.gascontact.business.adapters.SpinnerRealmAdapter
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.network.api.MiriadaApiRetrofitFactory
import net.gas.gascontact.utils.ORGANIZATIONUNITLIST
import net.gas.gascontact.utils.Var
import okhttp3.ResponseBody
import retrofit2.HttpException

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentFirstLoginBinding
    private lateinit var viewModel: BranchListViewModel
    private lateinit var downloadType: String
    private var selectedRealm = ""
    private var isLoginButtonActive = false


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
            Log.e("KEY", downloadType)
        }

        val realmArray = arrayListOf<String>()
        val icons = arrayListOf<Int>()
        ORGANIZATIONUNITLIST.forEach { icons.add(it.image) }
        ORGANIZATIONUNITLIST.forEach { realmArray.add(it.name) }
        val adapter = SpinnerRealmAdapter(context!!, R.layout.spinner_realm_row, realmArray, icons)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        if (downloadType == "UPDATE") {
            viewModel.appToolbarStateCallback?.invoke("Авторизация", true)
            val preferences = context?.getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            if (preferences != null) {
                preferences.getString("REALM", "topgas")
                if (preferences.contains("REALM")) {
                    spinner.setSelection(realmArray.indexOf(ORGANIZATIONUNITLIST.find { it.code == preferences.getString("REALM", "topgas") }?.name))
                    spinner.isEnabled = false
                    spinner.isActivated = false
                }
            } else {
                Toast.makeText(context, "Возникла ошибка!", Toast.LENGTH_SHORT).show()
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
            viewModel.tryToLogin(ORGANIZATIONUNITLIST.find { it.name == selectedRealm }?.code, loginUsernameText.text.toString(), loginPasswordText.text.toString())
        }

        viewModel.userLoginState.observe(viewLifecycleOwner, Observer {
            if (isLoginButtonActive) {
                if (!it.isNullOrBlank()) {
                    viewModel.afterSuccessLoginCallback?.invoke()

                    val apiService = MiriadaApiRetrofitFactory.makeRetrofitService()
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        try {
                            viewModel.downloadSpinnerState.postValue(true)
                            val response = apiService.requestGetDownloadDB(
                                ORGANIZATIONUNITLIST.find { it.name == selectedRealm }?.code!!,
                                "refresh_token", "Bearer $it"
                            )

                            try {
                                withContext(Dispatchers.Main) {
                                    if (response.isSuccessful) {
                                        response.body()?.let {
                                            // Toast.makeText(context, "Response code is ${response.code()}", Toast.LENGTH_LONG).show()
                                            if (downloadType == "DOWNLOAD")
                                                viewModel.startDownloadingDbTest(response)
                                            else {
                                                viewModel.startUpdatingDbTest(response)
                                            }
                                        }
                                        viewModel.realmSpinnerPosition =
                                            spinner.selectedItemPosition
                                        Toast.makeText(
                                            context,
                                            "Success! Response code is ${response.code()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Log.e("Controller", "DownloadDatabase Not success")
                                        Toast.makeText(
                                            context,
                                            "Не удаётся скачать базу данных! Ошибка ${response.code()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        viewModel.downloadSpinnerState.value = false
                                        activity?.supportFragmentManager?.popBackStack();
                                    }
                                }
                            } catch (e: HttpException) {
                                Log.e("Controller", "Exception in DownloadDatabase ${e.message}")
                                Toast.makeText(context, "Ошибка авторизации, проверьте введённые данные", Toast.LENGTH_LONG)
                                    .show()
                            } catch (e: Throwable) {
                                Log.e("Controller", "Ooops: Something else went wrong ${e.message}")
                            }
                        } catch (e: KotlinNullPointerException) {

                        }
                    }
                }
            }
        })
    }


}
