package com.easyvisa.questionnaire.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIStyleMeta {
    UIStyle style;
    List<String> wrappers = new ArrayList<>();

    public UIStyle getStyle() {
        return style;
    }

    public void setStyle(UIStyle style) {
        this.style = style;
    }

    public List<String> getWrappers() {
        return wrappers;
    }

    public void setWrappers(List<String> wrappers) {
        this.wrappers = wrappers;
    }

    public static class UIStyle {
        private String header;
        private String styleClassName;
        private String fieldGroupClassName;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getStyleClassName() {
            return styleClassName;
        }

        public void setStyleClassName(String styleClassName) {
            this.styleClassName = styleClassName;
        }

        public String getFieldGroupClassName() {
            return fieldGroupClassName;
        }

        public void setFieldGroupClassName(String fieldGroupClassName) {
            this.fieldGroupClassName = fieldGroupClassName;
        }
    }
}
