package com.jotagalilea.xingtest.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jotagalilea.xingtest.model.Repo
import com.jotagalilea.xingtest.R
import com.jotagalilea.xingtest.databinding.RowMainRepoBinding

class ReposAdapter(private val itemLongClickListener: OnItemLongClickListener)
    : RecyclerView.Adapter<ReposAdapter.RepoRowViewHolder>(){

    private var items: MutableLiveData<MutableList<Repo>> = MutableLiveData(mutableListOf())
    var context: Context? = null


    fun addItems(newRepos: MutableList<Repo>){
        items.value?.addAll(newRepos)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoRowViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding: RowMainRepoBinding = RowMainRepoBinding.inflate(inflater, parent, false)
        return RepoRowViewHolder(binding, itemLongClickListener)

    }


    override fun onBindViewHolder(holder: RepoRowViewHolder, position: Int) {
        items.value?.let{
            val item = it[position]
            holder.repoName.text = item.name
            holder.login.text = item.login
            holder.description.text = item.description
            context?.let{ ctxt ->
                holder.itemView.setBackgroundColor(if (item.fork) ctxt.getColor(R.color.teal_200) else ctxt.getColor(R.color.white))
            }

        } ?: run {
            holder.description.text = context?.getString(R.string.got_repos_error)
        }
    }


    override fun getItemCount(): Int {
        return items.value?.size ?: 0
    }


    interface OnItemLongClickListener{
        fun onLongClick(repo: Repo) : Boolean
    }



    //region ViewHolder
    inner class RepoRowViewHolder(
        binding: RowMainRepoBinding,
        private var onItemLongClickListener: OnItemLongClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnLongClickListener{

        val repoName: TextView = binding.liMainRepoName
        val login: TextView = binding.liMainRepoLogin
        val description: TextView = binding.liMainRepoDescription


        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            val selected: Repo = items.value!![adapterPosition]
            return onItemLongClickListener.onLongClick(selected)
        }
    }
    //endregion
}