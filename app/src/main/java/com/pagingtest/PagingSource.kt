package com.pagingtest

import android.graphics.Color
import androidx.paging.PagingSource
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias IntColor = Int

class MyPagingSource : PagingSource<Int, IntColor>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IntColor> {
        delay(600)
        return Random(System.currentTimeMillis()).let { rng ->
            List(rng.nextInt(1, 20)) {
                Color.argb(1f, rng.nextFloat(), rng.nextFloat(), rng.nextFloat())
            }
        }.let {
            LoadResult.Page(it, null, null)
        }
    }

}