package ru.facilicom24.manager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.facilicom24.manager.R;

public class InteractionItem extends Fragment {

	RelativeLayout interaction_panel;

	TextView interaction_title;
	TextView interaction_option;

	public InteractionItem() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.interaction_item, container, false);

		interaction_panel = (RelativeLayout) view.findViewById(R.id.interaction_panel);

		interaction_title = (TextView) view.findViewById(R.id.interaction_title);
		interaction_option = (TextView) view.findViewById(R.id.interaction_option);

		return view;
	}

	public int getVisibility() {
		return interaction_panel.getVisibility();
	}

	public void setVisibility(int visibility) {
		interaction_panel.setVisibility(visibility);
	}

	public void setTitle(String title) {
		interaction_title.setText(title);
	}

	public void setOption(String title) {
		interaction_option.setText(title);
	}

	public void setClickListener(View.OnClickListener clickListener) {
		interaction_panel.setOnClickListener(clickListener);
	}
}
