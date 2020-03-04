package net.gas.gascontact.business.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.contactbook.R
import com.example.contactbook.databinding.BranchItemBinding
import net.gas.gascontact.business.database.entities.Departments
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.utils.GlideApp

class DepartmentListAdapter(private val viewModel: BranchListViewModel) :
    DataBoundListAdapter<Departments>(diffCallback = object : DiffUtil.ItemCallback<Departments>() {
        override fun areItemsTheSame(oldItem: Departments, newItem: Departments)
                : Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Departments, newItem: Departments)
                : Boolean = oldItem == newItem
    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        val inflater = LayoutInflater.from(parent.context)
        return BranchItemBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ViewDataBinding, item: Departments, position: Int) {
        when (binding) {
            is BranchItemBinding -> {
                if (position % 2 == 0)
                    binding.root.setBackgroundColor(Color.parseColor("#fffef7"))
                else binding.root.setBackgroundColor(Color.parseColor("#F6F4F4"))
                binding.viewModel = viewModel
                binding.id = item.id.toInt()
                binding.unit = item.name
                binding.listType = Departments::class.java.name
                GlideApp.with(binding.root.context)
                    .asDrawable()
                    .load(binding.root.context.resources.getDrawable(R.drawable.ic_group_25))
                    .into(binding.imageView)
                viewModel.spinnerState.value = false
            }
        }
    }


}