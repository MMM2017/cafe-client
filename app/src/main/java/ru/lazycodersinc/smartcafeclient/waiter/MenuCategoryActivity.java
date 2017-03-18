package ru.lazycodersinc.smartcafeclient.waiter;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import ru.lazycodersinc.smartcafeclient.R;

public class MenuCategoryActivity extends AppCompatActivity implements MenuListFragment.OnFragmentInteractionListener
{
	public static final String ARG_CAT_ID = "catID";

	MenuListFragment listFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_category);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		listFragment = (MenuListFragment) getSupportFragmentManager().findFragmentById(R.id.menuListFragment);
		int catId = getIntent().getExtras().getInt(ARG_CAT_ID);
		if (listFragment != null)
			listFragment.filterByCategory(catId);
	}

	@Override
	public void onFragmentInteraction(Uri uri)
	{

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == android.R.id.home)
		{
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
