package KLKTJavaUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.klkt.klktkotlin.utils.KLKTStringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class JsUtils {
    public static ObjectMapper getJsMapper(final boolean isDefConfig) {
        final ObjectMapper mapper = new ObjectMapper();
        if (!isDefConfig) {
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
        }
        return mapper;
    }

    public static ObjectMapper getJsMapper() {
        return getJsMapper(false);
    }

    public static JsonNode toJsonNode(final Object[] params) {
        try {
            if (null == params) {
                return nullVal();
            }
            if (params.length < 1) {
                return newObj();
            }
            final Object input = params[0];
            if (null == input) {
                return nullVal();
            }
            if (input instanceof JsonNode) {
                return (JsonNode)input;
            }
            if (input instanceof KLKTJsonObject) {
                return ((KLKTJsonObject)input).toJsonNode();
            }
            final ObjectMapper mapper = getJsMapper();
            if (input instanceof String) {
                if ("[]".equals(input)) {
                    return newArr();
                }
                return mapper.readTree((String)input);
            }
            else {
                if (input instanceof File) {
                    return (JsonNode)mapper.readValue((File)input, (Class)JsonNode.class);
                }
                return (JsonNode)mapper.convertValue(input, (Class)JsonNode.class);
            }
        }
        catch (Exception ex) {
            return nullVal();
        }
    }

    public static JsonNode nullVal() {
        final ObjectMapper mapper = new ObjectMapper();
        return (JsonNode)mapper.convertValue((Object)null, (Class)JsonNode.class);
    }

    public static JsonNode newObj() {
        final ObjectMapper mapper = new ObjectMapper();
        return (JsonNode)mapper.createObjectNode();
    }

    public static JsonNode newArr() {
        final ObjectMapper mapper = new ObjectMapper();
        return (JsonNode)mapper.createArrayNode();
    }

    public static boolean isIsNull(final JsonNode jsNode) {
        return jsNode == null || jsNode.isNull();
    }

    public static boolean isNullOrEmpty(final JsonNode jsNode) {
        if (isIsNull(jsNode)) {
            return true;
        }
        if (jsNode.isArray()) {
            return jsNode.size() < 1;
        }
        if (jsNode.isContainerNode()) {
            return !jsNode.fieldNames().hasNext();
        }
        return jsNode.asText().isEmpty();
    }

    public static boolean isExist(final JsonNode jsNode, final String keyPath) {
        try {
            if (null == keyPath || !jsNode.isContainerNode()) {
                return false;
            }
            JsonNode tmp = jsNode;
            for (final String s : keyPath.split("\\/")) {
                if (!s.isEmpty()) {
                    if (tmp.has(s)) {
                        tmp = tmp.get(s);
                    }
                    else {
                        if (!s.matches("^\\d+$") || !tmp.has(Integer.parseInt(s))) {
                            return false;
                        }
                        tmp = tmp.get(Integer.parseInt(s));
                    }
                }
            }
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static void addNode(final JsonNode js, final String keyName, final Object value) {
        if (!js.isContainerNode()) {
            return;
        }
        if (js.isArray()) {
            if (value instanceof KLKTJsonObject) {
                ((ArrayNode)js).add(((KLKTJsonObject)value).toJsonNode());
            }
            else if (value instanceof JsonNode) {
                ((ArrayNode)js).add((JsonNode)value);
            }
            else {
                ((ArrayNode)js).addPOJO(value);
            }
            return;
        }
        if (KLKTStringUtils.Companion.isEmpty(keyName)) {
            return;
        }
        final ObjectNode jNode = (ObjectNode)js;
        if (null == value) {
            jNode.set(keyName, (JsonNode)null);
            return;
        }
        if (value instanceof String) {
            jNode.put(keyName, value.toString());
            return;
        }
        if (value instanceof KLKTJsonObject) {
            jNode.set(keyName, ((KLKTJsonObject)value).toJsonNode());
            return;
        }
        if (value instanceof JsonNode) {
            jNode.set(keyName, (JsonNode)value);
            return;
        }
        jNode.putPOJO(keyName, parseDecimal(value));
    }

    public static boolean append(final JsonNode js, final Object value) {
        if (js == null || !js.isContainerNode() || value == null) {
            return false;
        }
        if (value instanceof ArrayNode) {
            if (js.isArray()) {
                ((ArrayNode)js).addAll((ArrayNode)value);
                return true;
            }
            return false;
        }
        else {
            if (value instanceof ObjectNode) {
                if (js.isArray()) {
                    ((ArrayNode)js).add((JsonNode)value);
                }
                else {
                    ((ObjectNode)js).setAll((ObjectNode)value);
                }
                return true;
            }
            return value instanceof KLKTJsonObject && append(js, ((KLKTJsonObject)value).toJsonNode());
        }
    }

    private static Object parseDecimal(final Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Double || value instanceof Float) {
            final DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(340);
            return df.format(value);
        }
        return value;
    }

    public static String parseDoubleJs(final Object value) {
        if (null != value && (value instanceof Double || value instanceof Float)) {
            final DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(340);
            return df.format(value);
        }
        return "";
    }

    public static String toString(final JsonNode jsonNode) {
        if (null == jsonNode) {
            return null;
        }
        if (jsonNode.isContainerNode()) {
            return jsonNode.toString();
        }
        if (jsonNode.isNull()) {
            return null;
        }
        final String nodeVal = jsonNode.asText();
        if (jsonNode.isNumber() && nodeVal.contains("E")) {
            return new BigDecimal(nodeVal).toPlainString();
        }
        return nodeVal;
    }

    public static String toPrettyString(final JsonNode jsonNode) {
        if (isNullOrEmpty(jsonNode)) {
            return "";
        }
        if (jsonNode.isContainerNode()) {
            return jsonNode.toPrettyString();
        }
        final String nodeVal = jsonNode.asText();
        if (jsonNode.isNumber() && nodeVal.contains("E")) {
            return new BigDecimal(nodeVal).toPlainString();
        }
        return nodeVal;
    }

    public static <T> T mapTo(final JsonNode jsonNode, final Class<T> classMap) {
        if (!jsonNode.isContainerNode()) {
            return null;
        }
        final ObjectMapper mapper = getJsMapper();
        return (T)mapper.convertValue((Object)jsonNode, (Class)classMap);
    }
}
