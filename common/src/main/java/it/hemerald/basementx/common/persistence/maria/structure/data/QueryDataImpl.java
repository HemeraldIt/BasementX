package it.hemerald.basementx.common.persistence.maria.structure.data;

import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryDataImpl implements QueryData {

    private final List<Map<String, Object>> dataByName = new ArrayList<>();
    private final List<Map<Integer, Object>> dataById = new ArrayList<>();

    private int index = -1;

    public QueryDataImpl(ResultSet resultSet) {
        try (resultSet) {
            Map<String, Object> rowByName;
            Map<Integer, Object> rowById;

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                rowByName = new HashMap<>();
                rowById = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    Object element = resultSet.getObject(i);
                    rowByName.put(metaData.getColumnName(i), element);
                    rowById.put(i, element);
                }
                dataByName.add(rowByName);
                dataById.add(rowById);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private <T> T getDataByName(String columnLabel, Class<T> type) {
        return type.cast(dataByName.get(index).get(columnLabel));
    }

    private <T> T getDataById(int columnIndex, Class<T> type) {
        return type.cast(dataById.get(index).get(columnIndex));
    }

    @Override
    public boolean next() {
        return ++index < dataByName.size();
    }

    @Override
    public boolean hasNext() {
        return index+1 < dataByName.size();
    }

    @Override
    public byte getByte(String columnLabel) {
        return getDataByName(columnLabel, byte.class);
    }

    @Override
    public byte getByte(int columnIndex) {
        return getDataById(columnIndex, byte.class);
    }

    @Override
    public short getShort(String columnLabel) {
        return getDataByName(columnLabel, short.class);
    }

    @Override
    public short getShort(int columnIndex) {
        return getDataById(columnIndex, short.class);
    }

    @Override
    public int getInt(String columnLabel) {
        return getDataByName(columnLabel, int.class);
    }

    @Override
    public int getInt(int columnIndex) {
        return getDataById(columnIndex, int.class);
    }

    @Override
    public long getLong(String columnLabel) {
        return getDataByName(columnLabel, long.class);
    }

    @Override
    public long getLong(int columnIndex) {
        return getDataById(columnIndex, long.class);
    }

    @Override
    public float getFloat(String columnLabel) {
        return getDataByName(columnLabel, float.class);
    }

    @Override
    public float getFloat(int columnIndex) {
        return getDataById(columnIndex, float.class);
    }

    @Override
    public double getDouble(String columnLabel) {
        return getDataByName(columnLabel, double.class);
    }

    @Override
    public double getDouble(int columnIndex) {
        return getDataById(columnIndex, double.class);
    }

    @Override
    public boolean getBoolean(String columnLabel) {
        return getDataByName(columnLabel, boolean.class);
    }

    @Override
    public boolean getBoolean(int columnIndex) {
        return getDataById(columnIndex, boolean.class);
    }

    @Override
    public Object getObject(String columnLabel) {
        return dataByName.get(index).get(columnLabel);
    }

    @Override
    public Object getObject(int columnIndex) {
        return dataById.get(index).get(columnIndex);
    }

    @Override
    public String getString(String columnLabel) {
        return getDataByName(columnLabel, String.class);
    }

    @Override
    public String getString(int columnIndex) {
        return getDataById(columnIndex, String.class);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) {
        return getDataByName(columnLabel, BigDecimal.class);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) {
        return getDataById(columnIndex, BigDecimal.class);
    }

    @Override
    public Date getDate(String columnLabel) {
        return getDataByName(columnLabel, Date.class);
    }

    @Override
    public Date getDate(int columnIndex) {
        return getDataById(columnIndex, Date.class);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) {
        return getDataByName(columnLabel, Timestamp.class);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) {
        return getDataById(columnIndex, Timestamp.class);
    }
}
