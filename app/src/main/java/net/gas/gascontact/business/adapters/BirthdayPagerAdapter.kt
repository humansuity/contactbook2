package net.gas.gascontact.business.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import net.gas.gascontact.ui.fragments.BirthdayPersonListFragment

class BirthdayPagerAdapter(fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                val fragment = BirthdayPersonListFragment()
                fragment.arguments = Bundle().apply { putString("PERIOD", "TODAY") }
                fragment
            }
            1 -> {
                val fragment = BirthdayPersonListFragment()
                fragment.arguments = Bundle().apply { putString("PERIOD", "TOMORROW") }
                fragment
            }
            2 -> {
                val fragment = BirthdayPersonListFragment()
                fragment.arguments = Bundle().apply { putString("PERIOD", "DAY_AFTER_TOMORROW") }
                fragment
            }
            3 -> {
                val fragment = BirthdayPersonListFragment()
                fragment.arguments = Bundle().apply { putString("PERIOD", "IN_A_WEEK") }
                fragment
            }
            else -> Fragment()
        }
    }

    override fun getCount() : Int = 4

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> { "Сегодня" }
            1 -> { "Завтра" }
            2 -> { "Послезавтра" }
            3 -> { "В течение недели" }
            else -> { return "" }
        }
    }
}