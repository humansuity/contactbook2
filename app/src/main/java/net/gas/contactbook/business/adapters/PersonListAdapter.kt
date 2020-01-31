package net.gas.contactbook.business.adapters

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonItemBinding
import net.gas.contactbook.business.database.entities.Persons
import net.gas.contactbook.business.viewmodel.BranchListViewModel
import net.gas.contactbook.utils.GlideApp

class PersonListAdapter(private val viewModel: BranchListViewModel, private val lifecycleOwner: LifecycleOwner) :
    DataBoundListAdapter<Persons>(diffCallback = object: DiffUtil.ItemCallback<Persons>() {
        override fun areItemsTheSame(oldItem: Persons, newItem: Persons)
                : Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Persons, newItem: Persons)
                : Boolean = oldItem == newItem
    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.person_item, parent, false)
    }

    @ExperimentalStdlibApi
    override fun bind(binding: ViewDataBinding, item: Persons) {
        when(binding) {
            is PersonItemBinding -> {
                val context = binding.root.context
                if (item.photoID != null) {
                    viewModel.setupPhotoEntity(item.photoID)
                    viewModel.photoEntity.observe(lifecycleOwner, Observer {
                        val decodedString = it.photo!!.decodeToString()
                        val byteArray = Base64.decode(decodedString, Base64.DEFAULT)
                        GlideApp.with(context)
                            .asBitmap()
                            .placeholder(R.drawable.ic_user_30)
                            .load(byteArray)
                            .apply(RequestOptions().transform(RoundedCorners(30)))
                            .into(binding.image)
                    })
                } else {
                    GlideApp.with(context)
                        .asDrawable()
                        .load(context.resources.getDrawable(R.drawable.ic_user_30))
                        .into(binding.image)
                }

                binding.viewModel = viewModel
                binding.id = item.id
                binding.name = item.lastName + " " + item.firstName + " " + item.patronymic
                viewModel.setupPostEntity(item.postID?.toInt())
                viewModel.postEntity.observe(lifecycleOwner, Observer {
                    binding.post = it.name
                })
            }
        }
    }


}