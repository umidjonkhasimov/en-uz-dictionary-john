package uz.gita.dictionaryumidjon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import uz.gita.dictionaryumidjon.databinding.ActivityMainBinding
import uz.gita.dictionaryumidjon.ui.AllWordsScreen
import uz.gita.dictionaryumidjon.ui.FavoritesScreen

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, AllWordsScreen())
                    .commit()
                navView.setCheckedItem(R.id.nav_all)
            }
            navView.setNavigationItemSelectedListener(this@MainActivity)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen) {
            binding.drawerLayout.close()
        } else {
//            super.onBackPressed()
            finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_all -> supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, AllWordsScreen())
                .commit()

            R.id.nav_favorites -> supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, FavoritesScreen())
                .addToBackStack(null)
                .commit()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}