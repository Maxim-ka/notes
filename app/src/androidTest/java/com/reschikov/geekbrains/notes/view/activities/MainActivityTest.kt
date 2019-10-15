package com.reschikov.geekbrains.notes.view.activities

import android.content.Intent
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.reschikov.geekbrains.notes.view.navigation.RouterSupportMessage
import com.reschikov.geekbrains.notes.viewmodel.activity.MainViewModel

import org.junit.Assert.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import com.reschikov.geekbrains.notes.R
import com.reschikov.geekbrains.notes.view.navigation.Screens
import io.mockk.*
import org.junit.*
import org.koin.androidx.viewmodel.dsl.viewModel


class MainActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java , true , false)

    private val mockRouterSupportMessage = mockk<RouterSupportMessage>(relaxUnitFun = true)
    private val mockMainViewModel = mockk<MainViewModel>()
    private val listNotesScreen = Screens.ListNotesScreen()

    companion object{

        @AfterClass @JvmStatic
        fun toStop(){
            stopKoin()
        }
    }

    @Before
    fun setup(){
        loadKoinModules(listOf(
                module {
                    single (override = true){ mockRouterSupportMessage }
                    viewModel (override = true) { mockMainViewModel }
                }))

        every { mockRouterSupportMessage.replaceScreen(listNotesScreen) } answers {
            mockMainViewModel.currentScreen = listNotesScreen
        }

        every { mockMainViewModel.currentScreen?.screenKey } returns listNotesScreen.screenKey

        Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.let {
            activityTestRule.launchActivity(it)
        }
    }



//    @Test
//    fun getNavigator() {
//
//    }

    @Test
    fun onCreate() {

        onView(withId(R.id.fb_fab)).check{view, _ ->
            view as FloatingActionButton
            assertEquals(R.drawable.ic_add_24dp, view.tag )
        }
    }

//    @Test
//    fun onOptionsItemSelected() {
//
//    }
//
//    @Test
//    fun onBackPressed() {
//
//    }

    @Test
    fun toBegin() {
        onView(withId(R.id.progress_bar)).check{view, _ ->
            activityTestRule.activity.toBegin()
            assertTrue(view.isVisible )
        }
    }

    @Test
    fun toFinish() {
        onView(withId(R.id.progress_bar)).check{view, _ ->
            activityTestRule.activity.toFinish()
            assertFalse(view.isVisible )
        }
    }
}