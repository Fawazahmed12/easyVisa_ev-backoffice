package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.meta.InputSourceType;
import com.easyvisa.questionnaire.meta.UIStyleMeta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FormUIMetaDataDAO {

    private Map<String, InputSourceType> inputSourceTypeMap;
    private Map<String, UIStyleMeta> uiStyleMetaMap;

    @PostConstruct
    public void loadUIMeta() {
        try {
            File file = ResourceUtils.getFile("classpath:uimeta/input-type-source.json");
            ObjectMapper objectMapper = new ObjectMapper();
            List<InputSourceType> inputSourceTypeList = objectMapper.readValue(file, new TypeReference<List<InputSourceType>>() {
            });
            inputSourceTypeMap = inputSourceTypeList.stream().collect(Collectors.toMap(InputSourceType::getType, Function.identity()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load inputTypeSource Json");
        }
    }

    @PostConstruct
    public void loadUIStyleMeta() {
        try {
            File file = ResourceUtils.getFile("classpath:uimeta/ui-style-meta.json");
            ObjectMapper objectMapper = new ObjectMapper();
            this.uiStyleMetaMap = objectMapper.readValue(file, new TypeReference<Map<String, UIStyleMeta>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load inputTypeSource Json");
        }
    }

    public InputSourceType getInputSourceTypeModel(String inputSourceType) {
        return inputSourceTypeMap.get(inputSourceType);
    }

    public UIStyleMeta getUIStyleMeta(String type) {
        return uiStyleMetaMap.get(type);
    }
}
