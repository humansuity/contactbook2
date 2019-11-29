package net.gas.contactbook.business.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.contactbook.R
import com.example.contactbook.databinding.UnitListItemBinding
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.business.viewmodel.UnitListViewModel

class UnitListAdapter(private val viewModel: UnitListViewModel) :
    DataBoundListAdapter<Units>(diffCallback = object: DiffUtil.ItemCallback<Units>() {

    override fun areItemsTheSame(oldItem: Units, newItem: Units): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: Units, newItem: Units): Boolean {
        return oldItem == newItem
    }

}) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.unit_list_item, parent, false)
    }

    override fun bind(binding: ViewDataBinding, item: Units) {
        when(binding) {
            is UnitListItemBinding -> {
                binding.id = item.id.toInt()
                binding.unit = item.name
                binding.viewModel = viewModel
            }
        }
    }


}