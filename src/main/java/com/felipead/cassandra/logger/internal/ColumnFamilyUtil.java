package com.felipead.cassandra.logger.internal;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.db.Cell;
import org.apache.cassandra.db.ColumnFamily;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class ColumnFamilyUtil {
    
    public static String getKeyspaceName(ColumnFamily columnFamily) {
        return columnFamily.metadata().ksName;
    }

    public static String getTableName(ColumnFamily columnFamily) {
        return columnFamily.metadata().cfName;
    }

    public static String getKeyText(ColumnFamily columnFamily, ByteBuffer key) {
        return columnFamily.metadata().getKeyValidator().getString(key);
    }

    public static Set<String> getCellNames(ColumnFamily columnFamily) {
        Set<String> cellNames = new HashSet<>();
        CFMetaData metadata = columnFamily.metadata();
        for (Cell cell : columnFamily) {
            if (cell.value().remaining() > 0) {
                String cellName = metadata.comparator.getString(cell.name());
                cellNames.add(normalizeCellName(cellName));
            }
        }
        return cellNames;
    }

    public static boolean isDeleted(ColumnFamily columnFamily) {
        return columnFamily.isMarkedForDelete();
    }
    
    private static String normalizeCellName(String cellName) {
        return cellName.trim().toLowerCase();
    }
}