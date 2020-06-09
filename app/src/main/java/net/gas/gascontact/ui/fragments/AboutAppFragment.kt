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

        notificationSwitch.isChecked = AlarmHelper.getNotificationState(requireContext())
        weekdayTimeText.text = AlarmHelper.getWeekdayScheduleTime(requireContext())
        holidayTimeText.text = AlarmHelper.getHolidayScheduleTime(requireContext())

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlarmHelper.setupNotificationState(requireContext(), state = true)
                AlarmHelper.setupNotificationAlarmForNextDay(requireContext())
                Snackbar.make(root, "Уведомления активны", Snackbar.LENGTH_LONG).show()
            } else {
                AlarmHelper.setupNotificationState(requireContext(), state = false)
                AlarmHelper.cancelNotificationAlarm(requireContext())
                Snackbar.make(root, "Уведомления отключены", Snackbar.LENGTH_LONG).show()
            }
        }

        try {
            val packageInfo =
                context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            versionTextField.text = packageInfo?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            versionTextField.text = "Не определена"
        }

        val databaseUpdateTime = requireContext()
            .getSharedPreferences(Var.APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Var.APP_DATABASE_UPDATE_TIME, "Не определена")
        dbUpdateTimeTextField.text = databaseUpdateTime


        alarmSettingWidget.setOnClickListener {
            if (AlarmHelper.getNotificationState(requireContext()))
                openNotificationDialog()
            else
                Snackbar.make(root, "Уведомления отключены", Snackbar.LENGTH_LONG).show()
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
            .setOnClickListener { openTimePicker(dialog, AlarmHelper.WEEKDAYS) }

        view.findViewById<Button>(R.id.btnSetupForHolidays)
            .setOnClickListener { openTimePicker(dialog, AlarmHelper.HOLIDAYS) }

        dialog.show()
    }


    private fun openTimePicker(mDialog: AlertDialog, flag: Int) {
        val date = DateTime()
        val datePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                setupNewAlarmTime("$hourOfDay:$minute", flag)
                Snackbar.make(root, "Время установлено: $hourOfDay:$minute", Snackbar.LENGTH_LONG)
                    .show()
                weekdayTimeText.text = AlarmHelper.getWeekdayScheduleTime(requireContext())
                holidayTimeText.text = AlarmHelper.getHolidayScheduleTime(requireContext())
                AlarmHelper.cancelNotificationAlarm(requireContext())
                AlarmHelper.setupNotificationAlarmForNextDay(requireContext())
                mDialog.dismiss()
            },
            date.hourOfDay,
            date.minuteOfHour,
            true
        )
        datePickerDialog.setTitle("Настройка для ${if (flag == AlarmHelper.WEEKDAYS) "будних" else "выходных"}")
        datePickerDialog.setOnCancelListener {
            datePickerDialog.dismiss()
            mDialog.dismiss()
        }
        datePickerDialog.show()
    }


    private fun setupNewAlarmTime(scheduleTime: String, flag: Int) {
        when (flag) {
            AlarmHelper.HOLIDAYS -> {
                AlarmHelper.setupNewScheduleTimeForHolidays(requireContext(), scheduleTime)
            }
            AlarmHelper.WEEKDAYS -> {
                AlarmHelper.setupNewScheduleTimeForWeekdays(requireContext(), scheduleTime)
            }
        }
    }

}
