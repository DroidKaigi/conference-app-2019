package io.github.droidkaigi.confsched2019.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.session.ui.AllSessionsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AllSessionsFragment.newInstance())
                    .commitNow()
        }
    }

}
