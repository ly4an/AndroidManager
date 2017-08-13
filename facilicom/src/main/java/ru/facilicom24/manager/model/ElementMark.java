package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties({"id"})
@DatabaseTable(tableName = "element_marks")
public class ElementMark implements Serializable {

	@DatabaseField(generatedId = true)
	private int id;

	@JsonProperty("Value")
	@DatabaseField(columnName = "mark_value")
	private int value;

	@JsonProperty("Comment")
	@DatabaseField(columnName = "mark_comment")
	private String comment;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	private Element element;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private Check check;

	public ElementMark() {
		super();
	}

	public ElementMark(int value, String comment) {
		this.value = value;
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
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
		if (!(o instanceof ElementMark)) return false;

		ElementMark that = (ElementMark) o;

		if (id != that.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + value;
		result = 31 * result + (comment != null ? comment.hashCode() : 0);
		result = 31 * result + (element != null ? element.hashCode() : 0);
		result = 31 * result + (check != null ? check.hashCode() : 0);
		return result;
	}
}
