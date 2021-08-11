package com.contour.flowofthought.activity

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.contour.flowofthought.activity.adapter.MainPagerAdapter
import com.contour.flowofthought.activity.fragment.EditFragment
import com.contour.flowofthought.activity.fragment.HistoryFragment
import com.contour.flowofthought.custom.log
import com.contour.flowofthought.custom.name
import com.contour.flowofthought.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: MainActivityBinding

    lateinit var adapter: MainPagerAdapter

    private val checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.forEach { pair ->
                if (!pair.value)
                    ContextCompat.checkSelfPermission(this, pair.key)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        adapter = MainPagerAdapter(lifecycle, supportFragmentManager, binding.mainViewpager).apply {
            addFragment(EditFragment.newInstance(), EditFragment.name)
            addFragment(HistoryFragment.newInstance(), HistoryFragment.name)
        }

        binding.mainViewpager.apply {
            adapter = this@MainActivity.adapter
            offscreenPageLimit = 2
            isUserInputEnabled = false
        }

        checkPermission.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }
}