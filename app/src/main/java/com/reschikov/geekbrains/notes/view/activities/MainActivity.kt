package com.reschikov.geekbrains.notes.view.activities

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.but_click

class MainActivity : AppCompatActivity(){

    private lateinit var viewModel:MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        viewModel.getViewState().observe(this, Observer {value -> text.text = value })
    }

    fun pushButton(view: View){
        if(view == but_click){
            viewModel.getPressed()
        }
    }
}
