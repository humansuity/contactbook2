package net.gas.contactbook.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contactbook.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.alert_fragment.*
import net.gas.contactbook.business.viewmodel.BranchListViewModel

class AlertFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.alert_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity())
            .get(BranchListViewModel::class.java)

        lvblock.setViewColor(Color.parseColor("#89b2c9"))
        lvblock.isShadow(true)
        lvblock.setShadowColor(R.color.black_overlay)

        viewModel.onNetworkErrorCallback = {
            if (it == "DOWNLOAD_ERROR") {
                Snackbar.make(
                    root, "Не удалось скачать базу данных!",
                    Snackbar.LENGTH_LONG
                ).show()
                alertContainer.visibility = View.VISIBLE
            }
            else if (it == "UPDATING_ERROR") {
                Snackbar.make(
                    root, "Не удалось скачать базу данных!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            else {
                Snackbar.make(
                    root, "Проверьте подключение к интернету!",
                    Snackbar.LENGTH_LONG
                ).show()
                alertContainer.visibility = View.VISIBLE
            }
            viewModel.spinnerState.value = false
        }
        viewModel.floatingButtonState.value = false
        button.setOnClickListener {
            viewModel.downloadDatabase()
        }

        viewModel.downloadSpinnerState.observe(viewLifecycleOwner, Observer {
            if (it) {
                lvblock.startAnim(800)
                alertContainer.visibility = View.GONE
                progressContainer.visibility = View.VISIBLE
            }
            else {
                lvblock.stopAnim()
                progressContainer.visibility = View.GONE
            }
        })
    }

}