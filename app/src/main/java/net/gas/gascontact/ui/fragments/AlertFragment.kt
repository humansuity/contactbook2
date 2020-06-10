package net.gas.gascontact.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.alert_fragment.*
import net.gas.gascontact.R
import net.gas.gascontact.business.viewmodel.BranchListViewModel

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

        viewModel.optionMenuStateCallback?.invoke("INVISIBLE")

        lvblock.setViewColor(Color.parseColor("#89b2c9"))
        lvblock.isShadow(true)
        lvblock.setShadowColor(R.color.black_overlay)

        viewModel.onNetworkErrorCallback = {
            when (it) {
                "DOWNLOAD_ERROR" -> {
                    Snackbar.make(
                        root, "Не удалось скачать базу данных!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    alertContainer.visibility = View.VISIBLE
                }
                "UPDATING_ERROR" -> {
                    Snackbar.make(
                        root, "Не удалось скачать базу данных!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                "ACCESS_DENIED_ERROR" -> {
                    Snackbar.make(
                        root, "Вы не можете скачать базу данных!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else -> {
                    Snackbar.make(
                        root, "Проверьте подключение к интернету!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    alertContainer.visibility = View.VISIBLE
                }
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
            } else {
                lvblock.stopAnim()
                progressContainer.visibility = View.GONE
            }
        })
        viewModel.dbDownloadingProgressState.observe(viewLifecycleOwner, Observer {
            stepProgressBar.currentProgress = it.x.toInt()
            textPercentage.text = "${it.x.toInt()}%"
        })
    }

}

