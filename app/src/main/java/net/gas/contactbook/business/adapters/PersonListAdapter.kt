package net.gas.contactbook.business.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.contactbook.R
import com.example.contactbook.databinding.UnitsListItemBinding
import net.gas.contactbook.business.database.entities.Persons
import net.gas.contactbook.business.viewmodel.BranchListViewModel

class PersonListAdapter(private val viewModel: BranchListViewModel) :
    DataBoundListAdapter<Persons>(diffCallback = object: DiffUtil.ItemCallback<Persons>() {
        override fun areItemsTheSame(oldItem: Persons, newItem: Persons)
                : Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Persons, newItem: Persons)
                : Boolean = oldItem == newItem
    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.units_list_item, parent, false)
    }

    override fun bind(binding: ViewDataBinding, item: Persons) {
        when(binding) {
            is UnitsListItemBinding -> {
                binding.viewModel = viewModel
                binding.unit = item.lastName + " " + item.firstName + " " + item.patronymic
            }
        }
    }


}