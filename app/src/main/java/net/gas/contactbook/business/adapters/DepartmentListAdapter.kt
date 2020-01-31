package net.gas.contactbook.business.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.contactbook.R
import com.example.contactbook.databinding.BranchItemBinding
import net.gas.contactbook.business.database.entities.Departments
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.utils.GlideApp

class DepartmentListAdapter(private val viewModel: BranchListViewModel) :
    DataBoundListAdapter<Departments>(diffCallback = object: DiffUtil.ItemCallback<Departments>() {
        override fun areItemsTheSame(oldItem: Departments, newItem: Departments)
                : Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Departments, newItem: Departments)
                : Boolean = oldItem == newItem
    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.branch_item, parent, false)
    }

    override fun bind(binding: ViewDataBinding, item: Departments) {
        when(binding) {
            is BranchItemBinding -> {
                binding.viewModel = viewModel
                binding.id = item.id.toInt()
                binding.unit = item.name
                binding.listType = Departments::class.java.name
                GlideApp.with(binding.root.context)
                    .asDrawable()
                    .load(binding.root.context.resources.getDrawable(R.drawable.ic_group_25))
                    .into(binding.imageView)
            }
        }
    }


}