package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items;

    Button btnAdd;
    EditText eItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        eItem  = findViewById(R.id.eItem);
        rvItems = findViewById(R.id.rvItems);

        //eItem.setText("I'm doing this from java");

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener()  {
            @Override
            public void onItemLongClicked(int position) {
                //delet item from model
                items.remove(position);
                //notify adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
    ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
        @Override
        public void onItemClicked(int position) {
            Log.d("MainActivity", "Single Click at position" + position);
            //create new activity
            Intent i =  new Intent(MainActivity.this, EditActivity.class);
            //pass the data being edited
            i.putExtra(KEY_ITEM_TEXT, items.get(position));
            i.putExtra(KEY_ITEM_POSITION, position);
            //display the edit activity
            startActivityForResult(i, EDIT_TEXT_CODE) ;
        }
    };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = eItem.getText().toString();
                //add item to the model
                items.add(todoItem);
                //notify adapter that we inserted an item
                itemsAdapter.notifyItemInserted(items.size()-1);
                eItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
            saveItems();
            }
        });

    }

    //update list after saving
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE)   {
            //Retrieve updated text value.
            String itemText =  data.getStringExtra(KEY_ITEM_TEXT);
            //Extract the original position
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update model at the correct position
            items.set(position, itemText);
            //notify adapter
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        }
        else    {
            Log.w("Main Activity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile()  {
        return new File(getFilesDir(), "data.txt");
    }
    //function that loads items by reading every line of file

    private void loadItems() {
        try {
            items = new ArrayList<>(org.apache.commons.io.FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }
        private void saveItems() {
            try {
                org.apache.commons.io.FileUtils.writeLines(getDataFile(), items);
            } catch (IOException e) {
                Log.e("MainActivity", "Error reading items", e);
            }

        }
    }

