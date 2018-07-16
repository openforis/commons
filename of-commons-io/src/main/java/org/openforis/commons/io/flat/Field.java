package org.openforis.commons.io.flat;

public class Field {
	
	public enum Type {
		STRING, INTEGER, DECIMAL, DATE, TIME
	}
	
	String name;
	int index;
	Type type;
	
	public Field(String name) {
		this(name, Type.STRING);
	}
	
	public Field(String name, Type type) {
		this(name, type, 0);
	}
	
	public Field(String name, Type type, int index) {
		super();
		this.name = name;
		this.type = type;
		this.index = index;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}