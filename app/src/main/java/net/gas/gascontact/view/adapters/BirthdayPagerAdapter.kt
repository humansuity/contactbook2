package net.gas.gascontact.view.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.gas.gascontact.view.ui.fragments.BirthdayPersonListFragment

class BirthdayPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

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