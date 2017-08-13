package ru.facilicom24.manager.utils;

import android.view.View;

public class NFItem {

	final static public int SHOW_ALL = 1;

	int tag;

	String name;
	String text;
	String hint;

	Type type;
	ILayoutBase layoutBase;

	int layoutId;
	int maxLength;
	boolean visible;

	public NFItem(Type type, String name) {
		this(type, name, "", 0, true, "", 0, null, 0);
	}

	public NFItem(Type type, String name, String text, int tag) {
		this(type, name, text, 0, true, "", 0, null, tag);
	}

	public NFItem(Type type, int layoutId, ILayoutBase layoutBase) {
		this(type, "", "", 0, true, "", layoutId, layoutBase, 0);
	}

	public NFItem(Type type, String name, boolean visible) {
		this(type, name, "", 0, visible, "", 0, null, 0);
	}

	public NFItem(Type type, String name, String text) {
		this(type, name, text, 0, true, "", 0, null, 0);
	}

	public NFItem(Type type, String name, String text, boolean visible) {
		this(type, name, text, 0, visible, "", 0, null, 0);
	}

	public NFItem(Type type, String name, int maxLength) {
		this(type, name, "", maxLength, true, "", 0, null, 0);
	}

	public NFItem(
			Type type,
			String name,
			String text,
			int maxLength,
			boolean visible,
			String hint,
			int layoutId,
			ILayoutBase layoutBase,
			int tag
	) {
		this.type = type;
		this.name = name;
		this.text = text;
		this.maxLength = maxLength;
		this.visible = visible;
		this.hint = hint;
		this.layoutId = layoutId;
		this.layoutBase = layoutBase;
		this.tag = tag;
	}

	public Type getType() {
		return type;
	}

	public int getTag() {
		return tag;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHint() {
		return hint;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getLayoutId() {
		return layoutId;
	}

	public ILayoutBase getLayoutBase() {
		return layoutBase;
	}

	public enum Type {
		Title,
		Choose,
		Text,
		Date,
		Phone,
		Layout,
		Value,
		Link
	}

	public interface ILayoutBase {
		void OnCreate(View view);
	}
}
