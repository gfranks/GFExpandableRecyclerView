package com.github.gfranks.expandablerecyclerview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class HorizontalChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_child);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HorizontalGroupItem horizontalGroupItem = getIntent().getParcelableExtra(HorizontalGroupItem.EXTRA);
        setTitle(horizontalGroupItem.getTitle());

        HorizontalChildItem horizontalChildItem = getIntent().getParcelableExtra(HorizontalChildItem.EXTRA);
        ((TextView) findViewById(R.id.child_text)).setText(horizontalChildItem.getText());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
