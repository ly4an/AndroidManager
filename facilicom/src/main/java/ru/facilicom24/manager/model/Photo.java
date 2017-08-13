package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "photos")
public class Photo implements Serializable {
	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField(columnName = "photo_uri")
	String uri;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	Check check;

	public Photo() {
		super();
	}

	public Photo(String uri) {
		this.uri = uri;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Check getCheck() {
		return check;
	}

	public void setCheck(Check check) {
		this.check = check;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Photo)) return false;

		Photo photo = (Photo) o;

		if (id != photo.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;

		result = 31 * result + (uri != null ? uri.hashCode() : 0);
		result = 31 * result + (check != null ? check.hashCode() : 0);

		return result;
	}
}
