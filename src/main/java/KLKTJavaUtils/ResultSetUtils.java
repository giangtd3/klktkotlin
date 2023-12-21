package KLKTJavaUtils;


import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResultSetUtils {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


    /**
     * @param @param obj
     * @return Map<String, Object> return type
     * @throws
     * @Title: objectToMap
     * @Description: Convert object to map, the default value is not reserved
     */
    public static Map objectToMap(Object obj) {

        Map<String, Object> map = new HashMap<String, Object>();
        map = objectToMap(obj, false);
        return map;
    }

    public static Map objectToMap(Object obj, boolean keepNullVal) {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (keepNullVal == true) {
                    map.put(field.getName(), field.get(obj));
                } else {
                    if (field.get(obj) != null && !"".equals(field.get(obj).toString())) {
                        map.put(field.getName(), field.get(obj));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static Map<String, Object> getResultMap(ResultSet rs) throws Exception {
        Map<String, Object> hm = new HashMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String key = rsmd.getColumnLabel(i);
            String value = rs.getString(i);
            hm.put(key, value);
        }
        return hm;
    }

    public static List<Map<String, ?>> getResultToListMap(ResultSet rs) throws Exception {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, ?>> results = new ArrayList<Map<String, ?>>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<String, Object>();
            for (int i = 1; i <= columns; i++) {
                row.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
            }
            results.add(row);
        }
        return results;
    }

    public static List<Map<String, Object>> convertResultSetToMap(ResultSet rs) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();

        while (rs.next()) {
            int numColumns = rsmd.getColumnCount();
            Map<String, Object> obj = new HashMap<String, Object>();

            for (int i = 1; i < numColumns + 1; i++) {
                String column_name = rsmd.getColumnName(i).toLowerCase();

                if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
                    obj.put(column_name, rs.getArray(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
                    obj.put(column_name, rs.getLong(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.REAL) {
                    obj.put(column_name, rs.getFloat(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
                    obj.put(column_name, rs.getBoolean(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
                    obj.put(column_name, rs.getBlob(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
                    obj.put(column_name, rs.getDouble(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
                    obj.put(column_name, rs.getDouble(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
                    obj.put(column_name, rs.getInt(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
                    obj.put(column_name, rs.getNString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
                    obj.put(column_name, rs.getString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.CHAR) {
                    obj.put(column_name, rs.getString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.NCHAR) {
                    obj.put(column_name, rs.getNString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.LONGNVARCHAR) {
                    obj.put(column_name, rs.getNString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.LONGVARCHAR) {
                    obj.put(column_name, rs.getString(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
                    obj.put(column_name, rs.getByte(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
                    obj.put(column_name, rs.getShort(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
                    obj.put(column_name, rs.getDate(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.TIME) {
                    obj.put(column_name, rs.getTime(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
                    obj.put(column_name, rs.getTimestamp(column_name).getTime());
                } else if (rsmd.getColumnType(i) == java.sql.Types.BINARY) {
                    obj.put(column_name, rs.getBytes(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.VARBINARY) {
                    obj.put(column_name, rs.getBytes(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.LONGVARBINARY) {
                    obj.put(column_name, rs.getBinaryStream(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.BIT) {
                    obj.put(column_name, rs.getBoolean(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.CLOB) {
                    obj.put(column_name, rs.getClob(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.NUMERIC) {
                    obj.put(column_name, rs.getBigDecimal(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.DECIMAL) {
                    obj.put(column_name, rs.getBigDecimal(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.DATALINK) {
                    obj.put(column_name, rs.getURL(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.REF) {
                    obj.put(column_name, rs.getRef(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.STRUCT) {
                    obj.put(column_name, rs.getObject(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.DISTINCT) {
                    obj.put(column_name, rs.getObject(column_name));
                } else if (rsmd.getColumnType(i) == java.sql.Types.JAVA_OBJECT) {
                    obj.put(column_name, rs.getObject(column_name));
                } else {
                    obj.put(column_name, rs.getString(i));
                }//from w w w .j  ava  2  s .  c o  m
            }

            res.add(obj);
        }
        return res;

    }

    public static Map<String, Object> convertToMap(Map<String, Integer> metaData, ResultSet rs)
            throws SQLException {
        fillRowNames(metaData, rs);//from  w  ww.jav a 2  s  . c  o m
        Map<String, Object> row = new HashMap<String, Object>();
        for (Map.Entry<String, Integer> entry : metaData.entrySet()) {
            Object value = null;
            if (Types.DATE == entry.getValue()) {
                value = formartDate(rs.getDate(entry.getKey()));
            } else if (Types.TIMESTAMP == entry.getValue()) {
                value = formartDateTime(rs.getTimestamp(entry.getKey()));
            } else if (Types.TIME == entry.getValue()) {
                value = formartTime(rs.getTime(entry.getKey()));
            } else {
                value = rs.getObject(entry.getKey());
            }
            row.put(entry.getKey(), value);
        }
        return row;
    }

    private static void fillRowNames(Map<String, Integer> metaData, ResultSet rs) throws SQLException {
        if (metaData.isEmpty()) {
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                metaData.put(rs.getMetaData().getColumnName(i), rs.getMetaData().getColumnType(i));
            }
        }
    }

    /**
     * pattern : yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String formartDate(Date date) {
        if (null == date) {
            return null;
        }
        return dateFormat.format(date);
    }

    /**
     * pattern : yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String formartDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return dateTimeFormat.format(date);
    }

    /**
     * pattern : HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String formartTime(Date date) {
        if (null == date) {
            return null;
        }
        return timeFormat.format(date);
    }

    public static String resultSetToHtmlTable(java.sql.ResultSet rs) throws SQLException {
        int rowCount = 0;
        final StringBuilder result = new StringBuilder();
        result.append("<P ALIGN='center'>\n<TABLE BORDER=1>\n");
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        //header
        result.append("\t<TR>\n");
        for (int i = 0; i < columnCount; ++i) {
            result.append("\t\t<TH>").append(rsmd.getColumnLabel(i + 1)).append("</TH>\n");
        }
        result.append("\t</TR>\n");
        //data
        while (rs.next()) {
            ++rowCount;
            result.append("\t<TR>\n");
            for (int i = 0; i < columnCount; ++i) {
                String value = rs.getString(i + 1);
                if (rs.wasNull()) {
                    value = "&#060;null&#062;";
                }
                result.append("\t\t<TD>").append(value).append("</TD>\n");
            }
            result.append("\t</TR>\n");
        }
        result.append("</TABLE>\n</P>\n");
        return result.toString();
    }

    public static String resultSetToXML(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        StringBuffer ret = new StringBuffer();

        while (rs.next()) {
            ret.append("<Table>");
            for (int i = 1; i <= colCount; i++) {
                String columnName = rsmd.getColumnName(i).replaceAll(" ", "&nbsp;");
                String value = rs.getString(i);
                if (value != null && (value.indexOf("<") != -1 || value.indexOf(">") != -1)) {
                    value = "<![CDATA[" + value + "]]>";
                }
                ret.append("<" + columnName + ">" + value);
                ret.append("</" + columnName + ">");
            }
            ret.append("</Table>");
        }
        return ret.toString();

    }
}

