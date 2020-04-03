package net.gas.gascontact.business.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import net.gas.gascontact.ui.fragments.BirthdayPersonListFragment

class BirthdayPagerAdapter(fragmentManager: FragmentManager)
    : FragmentStatePagerAdapter(fragmentManager) {


    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                BirthdayPersonListFragment.newInstance("PERIOD", "TODAY")
            }
            1 -> {
                BirthdayPersonListFragment.newInstance("PERIOD", "TOMORROW")
            }
            2 -> {
                BirthdayPersonListFragment.newInstance("PERIOD", "DAY_AFTER_TOMORROW")
            }
            3 -> {
                BirthdayPersonListFragment.newInstance("PERIOD", "IN_A_WEEK")
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