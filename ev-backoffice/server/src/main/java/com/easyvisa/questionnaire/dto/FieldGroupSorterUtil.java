package com.easyvisa.questionnaire.dto;

import java.util.*;
import java.util.stream.Collectors;

public class FieldGroupSorterUtil {

    public static void sortChildren(IFieldGroup parentFieldGroup, List<String> displayOrderIdList){
        Map<String, Set<IFieldGroup>> fieldGroupMapper = new HashMap<>();
        parentFieldGroup.getFieldGroups().forEach(fieldGroup -> {
            Set<IFieldGroup> fieldGroupSet = null;
            String fieldId = fieldGroup.getFieldId();
            if(fieldGroupMapper.containsKey(fieldId)){
                fieldGroupSet = fieldGroupMapper.get(fieldId);
            }else{
                fieldGroupSet = new TreeSet<>(Comparator.comparingLong(IFieldGroup::getOrderIndex));
                fieldGroupMapper.put(fieldId, fieldGroupSet);
            }
            fieldGroupSet.add(fieldGroup);
        });

        List<IFieldGroup> sortedFieldGroup = new ArrayList<>();
        displayOrderIdList.forEach(fieldId -> {
            if(fieldGroupMapper.containsKey(fieldId)){
                Set<IFieldGroup> fieldGroupSet = fieldGroupMapper.get(fieldId);
                List<IFieldGroup> fieldGroupList = fieldGroupSet.stream().collect(Collectors.toList());
                sortedFieldGroup.addAll(fieldGroupList);
            }
        });

        parentFieldGroup.setFieldGroups(sortedFieldGroup);
    }
}
