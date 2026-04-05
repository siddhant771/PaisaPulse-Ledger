package com.paisapulse.app

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.databinding.ActivityMainBinding
import com.paisapulse.app.domain.models.CategorySum
import android.view.View

import com.paisapulse.app.presentation.ui.TransactionAdapter
import com.paisapulse.app.presentation.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure this layout contains a <androidx.fragment.app.FragmentContainerView>
        setContentView(R.layout.activity_main)
    }
}