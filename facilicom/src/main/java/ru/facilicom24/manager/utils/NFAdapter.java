package ru.facilicom24.manager.utils;

import android.content.Context;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ru.facilicom24.manager.R;

public class NFAdapter extends BaseAdapter {

	LayoutInflater inflater;
	ArrayList<NFItem> items;

	public NFAdapter(Context context, ArrayList<NFItem> items) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final NFItem item = items.get(position);

		if (item.getVisible()) {
			switch (item.getType()) {
				case Title: {
					convertView = inflater.inflate(R.layout.fragment_nfitem_title, parent, false);
					((TextView) convertView.findViewById(R.id.interaction_title)).setText(item.getName());
				}
				break;

				case Choose: {
					convertView = inflater.inflate(R.layout.fragment_nfitem_choose, parent, false);

					((TextView) convertView.findViewById(R.id.interaction_option)).setText(item.getText());
					((TextView) convertView.findViewById(R.id.interaction_title)).setText(item.getName());
				}
				break;

				case Text:
				case Phone: {
					convertView = inflater.inflate(R.layout.fragment_nfitem_text, parent, false);

					final EditText editText = (EditText) convertView.findViewById(R.id.interaction_option);

					switch (item.getType()) {
						case Phone: {
							editText.setInputType(InputType.TYPE_CLASS_PHONE);
							editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
						}
						break;
					}

					editText.setText(item.getText());

					if (item.getHint().length() > 0) {
						editText.setHint(item.getHint());
					}

					if (item.getMaxLength() > 0) {
						InputFilter[] filters = new InputFilter[1];
						filters[0] = new InputFilter.LengthFilter(item.getMaxLength());
						editText.setFilters(filters);
					}

					editText.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {
								item.setText(editText.getText().toString().trim());
							}
						}
					});

					((TextView) convertView.findViewById(R.id.interaction_title)).setText(item.getName());
				}
				break;

				case Date: {
					convertView = inflater.inflate(R.layout.fragment_nfitem_date, parent, false);

					((TextView) convertView.findViewById(R.id.interaction_option)).setText(item.getText());
					((TextView) convertView.findViewById(R.id.interaction_title)).setText(item.getName());
				}
				break;

				case Layout: {
					convertView = inflater.inflate(item.getLayoutId(), parent, false);

					if (item.getLayoutBase() != null) {
						item.getLayoutBase().OnCreate(convertView);
					}
				}
				break;

				case Value: {
					convertView = inflater.inflate(R.layout.fragment_nfitem_value, parent, false);

					((TextView) convertView.findViewById(R.id.nameTextView)).setText(item.getName());

					TextView valueTextView = (TextView) convertView.findViewById(R.id.valueTextView);

					valueTextView.setText(item.getText());
					if (item.getTag() != NFItem.SHOW_ALL) {
						valueTextView.setMaxLines(2);
						valueTextView.setEllipsize(TextUtils.TruncateAt.END);
					}
				}
				break;

				case Link: {
					convertView = inflater.inflate(R.layout.fragment_nfitem_link, parent, false);

					((TextView) convertView.findViewById(R.id.titleTextView)).setText(item.getName());
					((TextView) convertView.findViewById(R.id.fileTextView)).setText(item.getText());
				}
				break;
			}
		} else {
			convertView = inflater.inflate(R.layout.fragment_nfitem_gone, parent, false);
			convertView.setVisibility(View.GONE);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
