package org.openforis.commons.io.csv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openforis.commons.io.flat.Field;

/**
 * @author G. Miceli
 */
public abstract class CsvProcessor {

	private DateFormat dateFormat;
	private Map<String, Field> fieldsByName;
	private List<String> fieldNames;
	
	public DateFormat getDateFormat() {
		if ( dateFormat == null ) {
			setDateFormat("yyyy-MM-dd");
		}
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setDateFormat(String pattern) {
		this.dateFormat = new SimpleDateFormat(pattern);
	}
	
	public List<Field> getFields() {
		return Collections.unmodifiableList(new ArrayList<Field>(fieldsByName.values()));
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}
	
	protected void setFields(List<Field> fields) {
		setFields(fields.toArray(new Field[fields.size()]));
	}
	
	protected void setFields(Field[] fields) {
		fieldNames = new ArrayList<String>(fields.length);
		fieldsByName = new LinkedHashMap<String, Field>(fields.length);
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			String name = f.getName();
			if ( name == null || name.trim().isEmpty() ) {
				throw new IllegalArgumentException("Empty column heading at index: " + i);
			}
			if ( fieldNames.contains(name) ) {
				throw new IllegalArgumentException("Duplicate header: " + name);
			}
			fieldsByName.put(name, f);
			fieldNames.add(name);
		}
	}
	
	protected void setFieldNames(String[] fieldNames) {
		Field[] fields = new Field[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			String name = fieldNames[i];
			Field c = new Field(name, Field.Type.STRING, i);
			fields[i] = c;
		}
		setFields(fields);
	}
}