package jp.ac.jec.cm0135.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;

public class CategoryListActivity extends AppCompatActivity {
    private ListView categoryListView;
    private ArrayAdapter<String> categoryAdapter;
    private RadioGroup displayModeRadioGroup;

    private String[] categories = {"スポーツニュース", "ITニュース", "芸能ニュース"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        categoryListView = findViewById(R.id.categoryListView);
        displayModeRadioGroup = findViewById(R.id.displayModeRadioGroup);

        // カテゴリのアダプターを作成してListViewにセットする
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        categoryListView.setAdapter(categoryAdapter);

        // カテゴリリストのアイテムクリック時の処理
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];

                int checkedRadioButtonId = displayModeRadioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.listModeRadioButton) {
                    // List 모드일 경우 MainActivity로 이동
                    Intent intent = new Intent(CategoryListActivity.this, MainActivity.class);
                    intent.putExtra("category", selectedCategory);
                    startActivity(intent);
                } else if (checkedRadioButtonId == R.id.pageModeRadioButton) {
                    // Page 모드일 경우 Main2Activity로 이동
                    Intent intent = new Intent(CategoryListActivity.this, Main2Activity.class);
                    intent.putExtra("category", selectedCategory);
                    startActivity(intent);
                }

//                // MainActivityに遷移するIntentを作成し、選択されたカテゴリを渡す
//                Intent intent = new Intent(CategoryListActivity.this, MainActivity.class);
//                intent.putExtra("category", selectedCategory);
//                startActivity(intent);
            }
        });
    }
}
