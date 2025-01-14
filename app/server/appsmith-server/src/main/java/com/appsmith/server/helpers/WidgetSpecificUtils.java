package com.appsmith.server.helpers;

import com.appsmith.server.constants.FieldName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class WidgetSpecificUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JSONParser jsonParser = new JSONParser(JSONParser.MODE_PERMISSIVE);

    public static JSONObject escapeTableWidgetPrimaryColumns(JSONObject dsl, Set<String> escapedWidgetNames) {
        Set<String> keySet = dsl.keySet();

        if (keySet.contains(FieldName.PRIMARY_COLUMNS)) {
            Map primaryColumns = (Map) dsl.get(FieldName.PRIMARY_COLUMNS);

            Map newPrimaryColumns = new HashMap();

            Boolean updateRequired = false;

            for (String columnName : (Set<String>) primaryColumns.keySet()) {
                if (columnName.equals(FieldName.MONGO_UNESCAPED_ID)) {
                    updateRequired = true;
                    newPrimaryColumns.put(FieldName.MONGO_ESCAPE_ID, primaryColumns.get(columnName));
                } else if (columnName.equals(FieldName.MONGO_UNESCAPED_CLASS)) {
                    updateRequired = true;
                    newPrimaryColumns.put(FieldName.MONGO_ESCAPE_CLASS, primaryColumns.get(columnName));
                } else {
                    newPrimaryColumns.put(columnName, primaryColumns.get(columnName));
                }
            }
            if (updateRequired) {
                dsl.put(FieldName.PRIMARY_COLUMNS, newPrimaryColumns);
                escapedWidgetNames.add(dsl.getAsString(FieldName.WIDGET_NAME));
            }
        }
        return dsl;
    }

    public static JSONObject unEscapeTableWidgetPrimaryColumns(JSONObject dsl) {

        String dslAsString;
        try {
            dslAsString = objectMapper.writeValueAsString(dsl);
            dslAsString = dslAsString.replaceAll(FieldName.MONGO_ESCAPE_ID, FieldName.MONGO_UNESCAPED_ID);
            dslAsString = dslAsString.replaceAll(FieldName.MONGO_ESCAPE_CLASS, FieldName.MONGO_UNESCAPED_CLASS);

            return (JSONObject) jsonParser.parse(dslAsString);

        } catch (JsonProcessingException | ParseException e) {
            // Something went wrong in parsing the DSL. Return as is
            return dsl;
        }

    }
}
