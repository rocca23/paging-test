package com.pagingtest

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private val pager = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        initialKey = 0
    ) {
        MyPagingSource()
    }

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }
    private val fab by lazy { findViewById<FloatingActionButton>(R.id.floatingActionButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MyPagerDataAdapter()

        recyclerView.adapter = adapter

        val flow = pager.flow.cachedIn(lifecycleScope)
        lifecycleScope.launchWhenStarted {
            flow.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading) {
                    recyclerView.isVisible = false
                    progressBar.isVisible = true
                } else {
                    recyclerView.isVisible = true
                    progressBar.isVisible = false
                }
            }
        }

        fab.setOnClickListener {
            adapter.refresh()
        }
    }

    class MyPagerDataAdapter : PagingDataAdapter<IntColor, MyPagingDataViewHolder>(
        object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean =
                oldItem == newItem
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagingDataViewHolder {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.view_list_item, parent, false)
                .let {
                    MyPagingDataViewHolder(it)
                }
        }

        override fun onBindViewHolder(holder: MyPagingDataViewHolder, position: Int) {
            holder.cardView.setCardBackgroundColor(ColorStateList.valueOf(getItem(position)!!))
        }
    }

    class MyPagingDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardView)
    }
}