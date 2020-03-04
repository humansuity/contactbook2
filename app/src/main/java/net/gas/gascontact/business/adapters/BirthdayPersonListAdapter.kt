package net.gas.gascontact.business.adapters

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.contactbook.R
import com.example.contactbook.databinding.PersonItemBirthdayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gas.gascontact.business.database.entities.Persons
import net.gas.gascontact.business.viewmodel.BranchListViewModel
import net.gas.gascontact.utils.GlideApp

class BirthdayPersonListAdapter(viewModel: ViewModel, private val lifecycleOwner: LifecycleOwner) :
    DataBoundListAdapter<Persons>(diffCallback = object: DiffUtil.ItemCallback<Persons>() {
        override fun areItemsTheSame(oldItem: Persons, newItem: Persons)
                : Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Persons, newItem: Persons)
                : Boolean = oldItem == newItem
    }) {

    private val mViewModel = viewModel as BranchListViewModel

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.person_item_birthday, parent, false)
    }

    @ExperimentalStdlibApi
    override fun bind(binding: ViewDataBinding, item: Persons, position: Int) {
        when(binding) {
            is PersonItemBirthdayBinding -> {
                val context = binding.root.context
                if (item.photoID != null) {
                    mViewModel.setupPhotoEntity(item.photoID)
                    mViewModel.photoEntity.observe(lifecycleOwner, Observer {
                        GlobalScope.launch(Dispatchers.Default) {
                            val decodedString = withContext(Dispatchers.Default) {
                                it.photo!!.decodeToString()
                            }
                            val byteArray = withContext(Dispatchers.Default) {
                                Base64.decode(decodedString, Base64.DEFAULT)
                            }
                            launch(Dispatchers.Main) {
                                GlideApp.with(context)
                                    .asBitmap()
                                    .placeholder(R.drawable.ic_user_30)
                                    .load(byteArray)
                                    .apply(RequestOptions().transform(RoundedCorners(30)))
                                    .into(binding.image)
                            }
                        }
                    })
                } else {
                    GlideApp.with(context)
                        .asDrawable()
                        .load(context.resources.getDrawable(R.drawable.ic_user_30))
                        .into(binding.image)
                }

                binding.viewModel = mViewModel
               // binding.id = item.id
               // binding.name = item.lastName + " " + item.firstName + " " + item.patronymic
                binding.textBirthday.text = item.birthday
                mViewModel.setupPostEntity(item.postID?.toInt())
                mViewModel.postEntity.observe(lifecycleOwner, Observer {
                  //  binding.post = it.name
                })
            }
        }
    }


}