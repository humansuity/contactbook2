package net.gas.gascontact.business.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.contactbook.R

class SpinnerRealmAdapter(context: Context, resource: Int,
                          private val realmArray: ArrayList<String>,
                          private val icons: Array<Int>)
    : ArrayAdapter<String>(context, resource) {

    override fun getCount(): Int {
        return realmArray.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = ViewHolder()
        val view: View
        if (convertView == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.spinner_realm_row, parent, false)
            //viewHolder.realmName = view.
        }
        return super.getView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }


    private inner class ViewHolder {
        val icon: ImageView? = null
        val realmName: TextView? = null
    }

}