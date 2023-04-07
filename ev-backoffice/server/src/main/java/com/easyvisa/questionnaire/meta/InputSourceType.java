package com.easyvisa.questionnaire.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InputSourceType {
	String type;
	List<ValueMap> values;

	public InputSourceType() {
	}

	public InputSourceType(String type) {
		this.type = type;
		this.values = new ArrayList<>();
	}

	public InputSourceType(String type, List<ValueMap> values) {
		this.type = type;
		this.values = values;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ValueMap> getValues() {
		return values;
	}

	public void setValues(List<ValueMap> values) {
		this.values = values;
	}

	public static class ValueMap {
		private String label;
		private String value;
		private String printValue;

		public ValueMap() {

		}

		public ValueMap(String data) {
			this.label = data;
			this.value = data;
			this.printValue = data;
		}

		public ValueMap(String label, String value, String printValue) {
			this.label = label;
			this.value = value;
			this.printValue = printValue;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

        public String getPrintValue() {
            return printValue;
        }

        public void setPrintValue(String printValue) {
            this.printValue = printValue;
        }

        public ValueMap clone() {
            return new ValueMap(this.label, this.value, this.printValue);
        }
    }
}
