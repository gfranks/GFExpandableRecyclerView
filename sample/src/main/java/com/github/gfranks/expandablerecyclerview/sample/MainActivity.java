package com.github.gfranks.expandablerecyclerview.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.gfranks.expandablerecyclerview.GFExpandableRecyclerViewAdapter;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class MainActivity extends AppCompatActivity implements GFExpandableRecyclerViewAdapter.OnChildClickListener {

    private RecyclerView mRecyclerView;
    private GFExpandableRecyclerViewAdapter mAdapter;
    private SharedPreferences mPrefs;
    private Orientation mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = getSharedPreferences(this.getClass().toString(), Context.MODE_PRIVATE);
        mOrientation = Orientation.values()[mPrefs.getInt(Orientation.EXTRA, Orientation.VERTICAL.ordinal())];
        mRecyclerView = (RecyclerView) findViewById(R.id.reycler_view);
        loadBasedOnOrientation();

        if (savedInstanceState != null) {
            mAdapter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_vertical:
                if (mOrientation != Orientation.VERTICAL) {
                    mOrientation = Orientation.VERTICAL;
                    mPrefs.edit().putInt(Orientation.EXTRA, mOrientation.ordinal()).apply();
                    loadBasedOnOrientation();
                }
                break;
            case R.id.action_horizontal:
                if (mOrientation != Orientation.HORIZONTAL) {
                    mOrientation = Orientation.HORIZONTAL;
                    mPrefs.edit().putInt(Orientation.EXTRA, mOrientation.ordinal()).apply();
                    loadBasedOnOrientation();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChildClick(GFExpandableRecyclerViewAdapter adapter, View v, int groupPosition, int childPosition) {
        switch (mOrientation) {
            case VERTICAL: {
                Intent intent = new Intent(this, VerticalChildActivity.class);
                intent.putExtra(VerticalGroupItem.EXTRA, (VerticalGroupItem) adapter.getGroupItem(groupPosition));
                intent.putExtra(VerticalChildItem.EXTRA, (VerticalChildItem) adapter.getChildItem(groupPosition, childPosition));
                startActivity(intent);
                break;
            }
            case HORIZONTAL: {
                Intent intent = new Intent(this, HorizontalChildActivity.class);
                intent.putExtra(HorizontalGroupItem.EXTRA, (HorizontalGroupItem) adapter.getGroupItem(groupPosition));
                intent.putExtra(HorizontalChildItem.EXTRA, (HorizontalChildItem) adapter.getChildItem(groupPosition, childPosition));
                startActivity(intent);
                break;
            }
        }
    }

    private void loadBasedOnOrientation() {
        switch (mOrientation) {
            case VERTICAL:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mAdapter = new VerticalExpandableRecyclerViewAdapter();
                mAdapter.setOnChildClickListener(this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case HORIZONTAL:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                mAdapter = new HorizontalExpandableRecyclerViewAdapter();
                mAdapter.setOnChildClickListener(this);
                mRecyclerView.setAdapter(mAdapter);
                break;
        }
    }
}
