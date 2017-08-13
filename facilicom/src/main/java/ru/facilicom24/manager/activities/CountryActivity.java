package ru.facilicom24.manager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import database.Country;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoCountryRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CountryActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;
	EditText searchField;

	CountryListAdapter adapter;
	List<Country> countries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_country);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		countries = DaoCountryRepository.getAllCountry(this);
		adapter = new CountryListAdapter(this, countries);
		listView.setAdapter(adapter);

		searchField = (EditText) findViewById(R.id.searchField);
		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int arg1, int arg2, int arg3) {
				CountryActivity.this.adapter.getFilter().filter(charSequence);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Country country = (Country) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("CountryUID", country.getCountryUID());
		intent.putExtra("CountryName", country.getCountryName());
		intent.putExtra("NeedPatentOrPermission", country.getNeedPatentOrPermission());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class CountryListAdapter
			extends BaseAdapter
			implements Filterable {

		LayoutInflater inflater;

		List<Country> objects;
		List<Country> _objects;

		CountryListAdapter(Context context, List<Country> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			this.objects = objects;
			_objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public Country getItem(int i) {
			return objects.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = inflater.inflate(R.layout.list_item, parent, false);
			}

			((TextView) view.findViewById(R.id.name)).setText(objects.get(position).getCountryName());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<Country> FilteredArrayNames = new ArrayList<>();

					String pattern = constraint.toString().toLowerCase();

					for (int i = 0; i < _objects.size(); i++) {
						String dataNames = String.valueOf(_objects.get(i).getCountryName());

						if (dataNames.toLowerCase().contains(pattern)) {
							FilteredArrayNames.add(_objects.get(i));
						}
					}

					results.count = FilteredArrayNames.size();
					results.values = FilteredArrayNames;

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					objects = (List<Country>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
