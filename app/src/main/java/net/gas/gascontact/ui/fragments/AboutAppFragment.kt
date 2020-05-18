package net.gas.gascontact.ui.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_about_app.*
import net.gas.gascontact.R
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.utils.Var
import org.joda.time.DateTime

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
        viewModel.onUnitFragmentBackPressed = {
            if (viewModel.parentId == 0) {
                viewModel.appToolbarStateCallback?.invoke("Филиалы", false)
            }
        }

        notification_switch.setOnCheckedChangeListener { _, isChecked ->
            Snackbar.make(root, if (isChecked) "Уведомления включены" else "Уведомления отключены", Snackbar.LENGTH_SHORT).show()
        }

        try {
            val pkgInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            app_version_text.text = pkgInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            app_version_text.text = "1.0.0"
        }

        val databaseUpdateTime = requireContext()
            .getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")
        textDatabaseUpdateTime.text = databaseUpdateTime


        alarmSettingsButton.setOnClickListener {
            val date = DateTime()
            TimePickerDialog(requireContext(),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    Snackbar.make(root, "Time is: $hourOfDay:$minute", Snackbar.LENGTH_SHORT).show()
                },
                date.hourOfDay,
                date.minuteOfHour,
                true).show()
        }

    }

}
