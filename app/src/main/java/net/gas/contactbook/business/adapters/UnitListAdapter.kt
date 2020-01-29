package net.gas.contactbook.business.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.contactbook.R
import com.example.contactbook.databinding.UnitsListItemBinding
import net.gas.contactbook.business.database.entities.Units
import net.gas.contactbook.business.viewmodel.BranchListViewModel

class UnitListAdapter(private val viewModel: BranchListViewModel) :
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
            R.layout.units_list_item, parent, false)
    }

    override fun bind(binding: ViewDataBinding, item: Units) {
        when(binding) {
            is UnitsListItemBinding -> {
                binding.viewModel = viewModel
                binding.id = item.id.toInt()
                binding.unit = item.name
                binding.listType = Units::class.java.name
            }
        }
    }


}