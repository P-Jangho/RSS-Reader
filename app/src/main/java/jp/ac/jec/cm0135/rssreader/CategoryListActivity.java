package jp.ac.jec.cm0135.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoryListActivity extends AppCompatActivity {
    private ListView categoryListView;
    private ArrayAdapter<String> categoryAdapter;

    private String[] categories = {"スポーツニュース", "ITニュース", "芸能ニュース"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        categoryListView = findViewById(R.id.categoryListView);

        // カテゴリのアダプターを作成してListViewにセットする
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        categoryListView.setAdapter(categoryAdapter);

        // カテゴリリストのアイテムクリック時の処理
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];

                // MainActivityに遷移するIntentを作成し、選択されたカテゴリを渡す
                Intent intent = new Intent(CategoryListActivity.this, MainActivity.class);
                intent.putExtra("category", selectedCategory);
                startActivity(intent);
            }
        });
    }
}
