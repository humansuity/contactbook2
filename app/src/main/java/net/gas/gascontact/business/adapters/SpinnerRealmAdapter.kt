package net.gas.gascontact.business.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import net.gas.gascontact.R


class SpinnerRealmAdapter(context: Context, resource: Int,
                          private val realmArray: ArrayList<String>,
                          private val icons: ArrayList<Int>)
    : ArrayAdapter<String>(context, resource) {

    override fun getCount(): Int {
        return realmArray.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder = ViewHolder()
        var view: View = View(context)
        if (convertView == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.spinner_realm_row, parent, false)
            viewHolder.realmName = view.findViewById(R.id.realmDescription) as TextView
            viewHolder.icon = view.findViewById(R.id.realmIcon) as ImageView
            view.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.icon?.setImageResource(icons[position])
        viewHolder.realmName?.text = realmArray[position]

        return convertView ?: view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }


    private final inner class ViewHolder {
        var icon: ImageView? = null
        var realmName: TextView? = null
    }

}
