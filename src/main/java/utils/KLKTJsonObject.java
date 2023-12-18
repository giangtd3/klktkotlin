package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KLKTJsonObject {
    private final JsonNode jsData;
    private final boolean isContainerNode;

    public static KLKTJsonObject newArr() {
        return new KLKTJsonObject(new Object[] { "[]" });
    }

    public static KLKTJsonObject newObj() {
        return new KLKTJsonObject(new Object[0]);
    }

    public static KLKTJsonObject build(final Object inputs) {
        return new KLKTJsonObject(new Object[] { inputs });
    }

    public KLKTJsonObject(final Object... params) {
        this.jsData = JsUtils.toJsonNode(params);
        this.isContainerNode = this.jsData.isContainerNode();
    }

    private KLKTJsonObject makeNull() {
        return new KLKTJsonObject(new Object[] { JsUtils.nullVal() });
    }

    public boolean isNullOrEmpty() {
        return JsUtils.isNullOrEmpty(this.jsData);
    }

    public boolean isNull() {
        return JsUtils.isIsNull(this.jsData);
    }

    public boolean isJson() {
        return this.isContainerNode;
    }

    public boolean isArray() {
        return this.isJson() && this.jsData.isArray();
    }

    public boolean isObject() {
        return this.isJson() && this.jsData.isObject();
    }

    public boolean isNumber() {
        return !this.isNull() && this.jsData.isNumber();
    }

    public boolean isText() {
        return !this.isNull() && this.jsData.isTextual();
    }

    public boolean isBoolean() {
        return !this.isNull() && this.jsData.isBoolean();
    }

    public int size() {
        return this.isArray() ? this.jsData.size() : 0;
    }

    public JsonNode toJsonNode() {
        return this.jsData;
    }

    public Map<String, KLKTJsonObject> fields() {
        final Map<String, KLKTJsonObject> mapField = new ConcurrentHashMap<String, KLKTJsonObject>();
        if (!this.isObject()) {
            return mapField;
        }
        final Iterator<Map.Entry<String, JsonNode>> enTry = (Iterator<Map.Entry<String, JsonNode>>)this.jsData.fields();
        while (enTry.hasNext()) {
            final Map.Entry<String, JsonNode> node = enTry.next();
            mapField.put(node.getKey(), build(node.getValue()));
        }
        return mapField;
    }

    public List<KLKTJsonObject> asList() {
        final List<KLKTJsonObject> lstNode = new ArrayList<KLKTJsonObject>();
        if (!this.isArray()) {
            return lstNode;
        }
        for (final JsonNode js : this.jsData) {
            lstNode.add(build(js));
        }
        return lstNode;
    }

    public List<String> objectKeys() {
        final List<String> keys = new ArrayList<String>();
        if (this.isObject()) {
            final Iterator<Map.Entry<String, JsonNode>> enTry = (Iterator<Map.Entry<String, JsonNode>>)this.jsData.fields();
            while (enTry.hasNext()) {
                keys.add(enTry.next().getKey());
            }
        }
        return keys;
    }

    public boolean isExist(final String keyPath) {
        return JsUtils.isExist(this.jsData, keyPath);
    }

    public KLKTJsonObject clone() {
        return new KLKTJsonObject(new Object[] { this.jsData.deepCopy() });
    }

    public KLKTJsonObject append(final String keyPath, final Object value) {
        JsUtils.append(this.get(keyPath).toJsonNode(), value);
        return this;
    }

    public KLKTJsonObject append(final Object value) {
        JsUtils.append(this.jsData, value);
        return this;
    }

    @Override
    public String toString() {
        return JsUtils.toString(this.jsData);
    }

    public String toString(final String defaultValue) {
        final String val = this.toString();
        return (null == val || val.isEmpty()) ? defaultValue : val;
    }

    public String toStringPretty() {
        if (this.isNullOrEmpty()) {
            return this.toString();
        }
        return JsUtils.toPrettyString(this.jsData);
    }

    public Integer toInteger() {
        return this.toInteger(0);
    }

    public Integer toInteger(final Integer defaultValue) {
        try {
            final String val = this.toString();
            if (null != val && !val.isEmpty()) {
                return Integer.parseInt(val);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public Long toLong() {
        return this.toLong(0L);
    }

    public Long toLong(final Long defaultValue) {
        try {
            final String val = this.toString();
            if (null != val && !val.isEmpty()) {
                return Long.parseLong(val);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public Float toFloat() {
        return this.toFloat(0.0f);
    }

    public Float toFloat(final Float defaultValue) {
        try {
            final String val = this.toString();
            if (null != val && !val.isEmpty()) {
                return Float.parseFloat(val);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public Double toDouble() {
        return this.toDouble(0.0);
    }

    public Double toDouble(final Double defaultValue) {
        try {
            final String val = this.toString();
            if (null != val && !val.isEmpty()) {
                return Double.parseDouble(val);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public Boolean toBoolean() {
        return this.toBoolean(false);
    }

    public Boolean toBoolean(final Boolean defaultValue) {
        try {
            final String val = this.toString();
            if (null != val && !val.isEmpty()) {
                return Boolean.parseBoolean(val);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return defaultValue;
    }

    public KLKTJsonObject get(final int idx) {
        if (this.isContainerNode && this.jsData.has(idx)) {
            return new KLKTJsonObject(new Object[] { this.jsData.get(idx) });
        }
        return this.makeNull();
    }

    public KLKTJsonObject get(final String keyPath) {
        final KLKTJsonObject defaultValue = this.makeNull();
        if (!this.isContainerNode || null == keyPath || keyPath.isEmpty()) {
            return defaultValue;
        }
        JsonNode tmp = this.jsData;
        final int idxArr = -1;
        for (final String s : keyPath.split("\\/")) {
            if (!s.isEmpty()) {
                if (tmp.has(s)) {
                    tmp = tmp.get(s);
                }
                else {
                    if (!s.matches("^\\d+$") || !tmp.has(Integer.parseInt(s))) {
                        return defaultValue;
                    }
                    tmp = tmp.get(Integer.parseInt(s));
                }
            }
        }
        return new KLKTJsonObject(new Object[] { tmp });
    }

    public KLKTJsonObject put(final Object value) {
        JsUtils.addNode(this.jsData, null, value);
        return this;
    }

    public KLKTJsonObject put(final String keyPath, final Object value) {
        if (!this.isContainerNode) {
            return this;
        }
        JsonNode tmp = this.jsData;
        String lastField = "";
        if (keyPath.indexOf("/") < 0) {
            lastField = keyPath;
        }
        else {
            for (String s : keyPath.split("\\/")) {
                s = s.trim();
                if (!s.isEmpty()) {
                    if (lastField.isEmpty()) {
                        lastField = s;
                    }
                    else {
                        if (!tmp.has(lastField)) {
                            JsUtils.addNode(tmp, lastField, JsUtils.newObj());
                        }
                        tmp = tmp.at("/".concat(lastField));
                        lastField = s;
                    }
                }
            }
        }
        JsUtils.addNode(tmp, lastField, value);
        return this;
    }

    public KLKTJsonObject remove(final String keyName) {
        if (!this.isContainerNode || null == keyName || keyName.isEmpty()) {
            return this;
        }
        if (this.jsData.has(keyName)) {
            ((ObjectNode)this.jsData).remove(keyName);
            return this;
        }
        return this;
    }

    public KLKTJsonObject remove(final int index) {
        if (!this.isContainerNode) {
            return this;
        }
        if (this.jsData.has(index)) {
            ((ArrayNode)this.jsData).remove(index);
            return this;
        }
        return this;
    }

    public <T> T mapTo(final Class<T> classMap) {
        return JsUtils.mapTo(this.jsData, classMap);
    }
}
