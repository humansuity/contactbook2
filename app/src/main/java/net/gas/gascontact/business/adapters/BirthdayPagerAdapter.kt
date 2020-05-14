package net.gas.gascontact.business.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.gas.gascontact.ui.activities.MainListActivity
import net.gas.gascontact.ui.fragments.BirthdayPeriodFragment
import net.gas.gascontact.ui.fragments.BirthdayPersonListFragment

class BirthdayPagerAdapter(fragment: Fragment)
    : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BirthdayPersonListFragment.newInstance("PERIOD", "TODAY")
            1 -> BirthdayPersonListFragment.newInstance("PERIOD", "TOMORROW")
            2 -> BirthdayPersonListFragment.newInstance("PERIOD", "DAY_AFTER_TOMORROW")
            3 -> BirthdayPersonListFragment.newInstance("PERIOD", "IN_A_WEEK")
            else -> Fragment()
        }
    }
}