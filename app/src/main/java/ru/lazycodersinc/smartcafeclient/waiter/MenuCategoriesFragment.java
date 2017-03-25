package ru.lazycodersinc.smartcafeclient.waiter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import ru.lazycodersinc.smartcafeclient.R;
import ru.lazycodersinc.smartcafeclient.model.Dish;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuCategoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuCategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuCategoriesFragment extends Fragment
{
	private OnFragmentInteractionListener mListener;

	public MenuCategoriesFragment()
	{
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MenuCategoriesFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MenuCategoriesFragment newInstance()
	{
		MenuCategoriesFragment fragment = new MenuCategoriesFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View cats = inflater.inflate(R.layout.menu_cat_view_layout, container, false);

		final ArrayAdapter<Dish.Type> adapter = new ArrayAdapter<>(
			getContext(),
			R.layout.menu_category_view,
			R.id.menuCategoryLabel,
			Dish.Type.values());
		GridView catsView = (GridView) cats.findViewById(R.id.menuCatsGridView);
		catsView.setAdapter(adapter);

		catsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Dish.Type cat = adapter.getItem(i);
				showMenuCategory(cat);
			}
		});

		return cats;
	}

	private void showMenuCategory(Dish.Type cat)
	{
		mListener.onCategorySelected(cat);
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener)
		{
			mListener = (OnFragmentInteractionListener) context;
		}
		else
		{
			throw new RuntimeException(context.toString()
				+ " must implement OnOrdersListUpdatedListener");
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener
	{
		// TODO: Update argument type and name
		void onCategorySelected(Dish.Type cat);
	}
}
