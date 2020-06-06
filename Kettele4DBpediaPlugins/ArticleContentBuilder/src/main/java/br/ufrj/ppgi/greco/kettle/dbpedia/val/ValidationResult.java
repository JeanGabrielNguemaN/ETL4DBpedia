package br.ufrj.ppgi.greco.kettle.dbpedia.val;

public class ValidationResult {

	private boolean isValid=false;
	private String derivedValue=null;
	
	public ValidationResult(boolean b, String value) {
		setValid(b);
		setDerivedValue(value);
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getValue() {
		return derivedValue;
	}

	public void setDerivedValue(String derivedValue) {
		this.derivedValue = derivedValue;
	}
	
}
