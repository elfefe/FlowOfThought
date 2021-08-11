package com.contour.flowofthought.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.contour.flowofthought.custom.log

class MainPagerAdapter(lifecycle: Lifecycle, fragment: FragmentManager, val viewPager: ViewPager2) :
    FragmentStateAdapter(fragment, lifecycle) {
    val fragments = arrayListOf<Pair<String, Fragment>>()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position].second

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(Pair(title, fragment))
    }

    fun getItemPosition(id: String): Int {
        val filtered = fragments.filter { it.first == id }
        return if (filtered.isEmpty())
            -1
        else fragments.indexOf(filtered[0])
    }

    fun slideTo(id: String) {
        val position = getItemPosition(id)
        if (position != -1)
            viewPager.currentItem = position
    }
}