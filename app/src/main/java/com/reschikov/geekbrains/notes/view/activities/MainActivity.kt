package com.reschikov.geekbrains.notes.view.activities

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.view.fragments.ListNotesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_master, ListNotesFragment(), "tag list notes")
                .commit()
        }
    }
}
