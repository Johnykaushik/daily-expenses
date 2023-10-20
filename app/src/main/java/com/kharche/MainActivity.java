package com.kharche;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_spent);

        if (savedInstanceState == null) {
            replaceAndRemoveFragments(new AddSpentFragment());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.dashboard) {
            getSupportActionBar().setTitle(R.string.dashboard);
            replaceAndRemoveFragments(new DashboardFragment());

        } else if (itemId == R.id.add_spent_amount) {
            getSupportActionBar().setTitle(R.string.add_spent);
            replaceAndRemoveFragments(new AddSpentFragment());

        } else if (itemId == R.id.spent_list) {
            getSupportActionBar().setTitle(R.string.spent_list);
            replaceAndRemoveFragments(new SpentListFragment());

        } else if (itemId == R.id.add_category) {
            getSupportActionBar().setTitle(R.string.add_category);
            replaceAndRemoveFragments(new AddCategoryFragment());

        } else if (itemId == R.id.category_list) {
            getSupportActionBar().setTitle(R.string.category_list);
            replaceAndRemoveFragments(new CategoryItemListFragment());
        } else {
            getSupportActionBar().setTitle(R.string.dashboard);
            replaceAndRemoveFragments(new DashboardFragment());
            return super.onContextItemSelected(item);
        }
        return true;
    }

    private void replaceAndRemoveFragments(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}