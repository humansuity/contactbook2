package net.gas.gascontact.ui.fragments

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_about_app.*
import net.gas.gascontact.R
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.ui.AlarmHelper
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


        val preferences = requireContext().getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
        notificationSwitch.isChecked = AlarmHelper.getNotificationState(requireContext())

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlarmHelper.setupNotificationState(requireContext(), state = true)
                Snackbar.make(root, "Уведомления активны", Snackbar.LENGTH_LONG).show()
            } else {
                AlarmHelper.setupNotificationState(requireContext(), state = false)
                Snackbar.make(root, "Уведомления отключены", Snackbar.LENGTH_LONG).show()
            }
        }

        try {
            val packageInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            versionTextField.text = packageInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            versionTextField.text = "Не определена"
        }

        val databaseUpdateTime = requireContext()
            .getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")
        dbUpdateTimeTextField.text = databaseUpdateTime


        alarmSettingWidget.setOnClickListener {
            openNotificationDialog()
        }

    }


    private fun openNotificationDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_setup_alarm, null)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        val background = ColorDrawable(Color.TRANSPARENT)
        val margins = InsetDrawable(background, 50)
        dialog.window?.setBackgroundDrawable(margins)

        view.findViewById<Button>(R.id.btnSetupForWeekDaysNotifs)
            .setOnClickListener { openTimePicker(AlarmHelper.HOLIDAYS) }

        view.findViewById<Button>(R.id.btnSetupForHolidaysNotifs)
            .setOnClickListener { openTimePicker(AlarmHelper.WEEKDAYS) }

        dialog.show()
    }


    private fun openTimePicker(flag: Int) {
        val date = DateTime()
        TimePickerDialog(requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                //setupNewAlarmTime(hourOfDay, minute, flag)
            },
            date.hourOfDay,
            date.minuteOfHour,
            true).show()
    }

}
