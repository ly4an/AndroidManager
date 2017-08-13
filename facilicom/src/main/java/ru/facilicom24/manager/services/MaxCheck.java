package ru.facilicom24.manager.services;

import java.util.regex.Pattern;

public class MaxCheck {
	static final private String EMAIL = "^[-!#$%&''*+/0-9=?A-Z^_a-z{|}~](\\.?[-!#$%&''*+/0-9=?A-Z^_a-z{|}~])*@[a-zA-Z](-?[a-zA-Z0-9])*(\\.[a-zA-Z](-?[a-zA-Z0-9])*)+$";

	static public boolean email(String value) {
		return Pattern.matches(EMAIL, value);
	}

	static public boolean required(String value) {
		return value != null && !value.isEmpty();
	}
}
