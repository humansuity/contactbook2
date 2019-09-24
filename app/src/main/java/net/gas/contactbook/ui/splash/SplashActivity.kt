package net.gas.contactbook.ui.splash

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.contactbook_kotlin.R

//import net.gas.contactbook_kotlin.R
//import net.gas.contactbook_kotlin.ui.organizations_list.OrganizationsListActivity


class SplashActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000 //2 seconds

    internal val mRunnable: Runnable = Runnable {
        /*if (!isFinishing) {

            val intent = Intent(applicationContext, OrganizationsListActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

}
