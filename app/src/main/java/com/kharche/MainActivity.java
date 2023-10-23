package com.kharche;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kharche.adapters.CategoryAdapter;
import com.kharche.dao.CategoryDao;
import com.kharche.model.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_spent);

        if (savedInstanceState == null) {
            replaceAndRemoveFragments(new SpentListFragment());
        }
    }

    private List<Category> getCategories() {
        List<Category> cateList = new ArrayList<>();

        CategoryDao categoryDao = new CategoryDao(this);
        List<Category> categories = categoryDao.getAllCategories();
        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();

            Category setCat = new Category();
//            setCat.setId(categoryId);
            setCat.setCategoryName(categoryName);

            cateList.add(setCat);
        }
        return cateList;
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
            replaceAndRemoveFragments(new CategoryListFragment());
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