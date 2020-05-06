package net.gas.gascontact.ui.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.gas.gascontact.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_about_app.*
import net.gas.gascontact.business.viewmodel.BranchListViewModel

class AboutAppFragment : Fragment() {

    private lateinit var viewModel: BranchListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about_app, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(BranchListViewModel::class.java)


        viewModel.appToolbarStateCallback?.invoke("Настройки", true)
        viewModel.optionMenuStateCallback?.invoke("INVISIBLE")
        viewModel.floatingButtonState.value = false
        viewModel.isUnitFragmentActive = true

        notification_switch.setOnCheckedChangeListener { _, isChecked ->
            Snackbar.make(root, "Уведомления отключены", Snackbar.LENGTH_SHORT).show()
        }

        try {
            val pkgInfo = context?.packageManager?.getPackageInfo(context!!.packageName, 0)
            app_version_text.text = pkgInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            app_version_text.text = "1.0.0"
        }
        textDatabaseUpdateTime.text = viewModel.databaseUpdateTime

    }

}
