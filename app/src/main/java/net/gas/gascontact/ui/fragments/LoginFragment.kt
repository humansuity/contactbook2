package net.gas.gascontact.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.contactbook.R
import com.example.contactbook.databinding.FragmentFirstLoginBinding
import kotlinx.android.synthetic.main.fragment_first_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.network.api.MiriadaApiRetrofitFactory
import net.gas.gascontact.utils.ORGANIZATIONUNITLIST
import retrofit2.HttpException

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
            Log.e("KEY", "$downloadType")
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
            if (!it.isNullOrBlank()) {
                viewModel.afterSuccessLoginCallback?.invoke()
                viewModel.downloadSpinnerState.value = true

                val apiService = MiriadaApiRetrofitFactory.makeRetrofitService()
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    val response = apiService.requestGetDownloadDB(
                        ORGANIZATIONUNITLIST.find { it.name == spinner.selectedItem }?.code!!,
                        "refresh_token","Bearer $it")
                    try {
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                response.body()?.let {
                                   // Toast.makeText(context, "Response code is ${response.code()}", Toast.LENGTH_LONG).show()
                                    if (downloadType == "DOWNLOAD")
                                        viewModel.startDownloadingDbTest(response)
                                    else
                                        viewModel.startUpdatingDbTest(response)
                                }
                                viewModel.realmSpinnerPosition = spinner.selectedItemPosition
                                Toast.makeText(context, "Success! Response code is ${response.code()}", Toast.LENGTH_LONG).show()
                            } else {
                                Log.e("Controller", "DownloadDatabase Not success")
                                Toast.makeText(context, "Не удаётся скачать базу данных!", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: HttpException) {
                        Log.e("Controller", "Exception in DownloadDatabase ${e.message}")
                        Toast.makeText(context, "Ошибка авторизации", Toast.LENGTH_LONG).show()
                    } catch (e: Throwable) {
                        Log.e("Controller", "Ooops: Something else went wrong ${e.message}")
                    }
                }
            }
        })
    }


}