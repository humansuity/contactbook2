package net.gas.contactbook.utils

import androidx.fragment.app.Fragment
import com.example.contactbook.R
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class FragmentManagerHelper(activity: FragmentActivity) {

    private val mActivity = activity

    fun replaceFragment(fragment: Fragment, container: Int, childFragmentManager: FragmentManager) {

        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction
            .setCustomAnimations(
                R.animator.enter_from_right, R.animator.exit_to_left,
                R.animator.enter_from_left, R.animator.exit_to_right
            )
            .addToBackStack(null)
            .add(container, fragment)
            .commit()
    }


    fun addFragment(fragment: Fragment, container: Int) {
        if (!mActivity.isDestroyed) {
            val fragmentTransaction = mActivity.supportFragmentManager.beginTransaction()
            fragmentTransaction
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(container, fragment)
                .commit()
        }
    }

}