package it.mineblock.basementx.persistence.maria.structure.column;

import it.mineblock.basementx.api.persistence.maria.queries.builders.table.QueryBuilderCreateTable;
import it.mineblock.basementx.api.persistence.maria.structure.column.MariaType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MariaColumn {

    private final String name;
    private final MariaType type;
    private final Integer size;

    private final String defaultValue;
    private final String constraint;
    private final QueryBuilderCreateTable.ColumnData[] columnData;

    public String toString() {

        StringBuilder builder = new StringBuilder(name).append(" ");

        builder.append(type.toString()).append(" ");

        if (size != null)
            builder.append("(").append(size).append(")").append(" ");

        for (QueryBuilderCreateTable.ColumnData columnDatum : columnData) {
            if (columnDatum == null) continue;
            builder.append(columnDatum.getName()).append(" ");
        }


        if (defaultValue != null)
            builder.append("DEFAULT ").append(defaultValue).append(" ");

        if (constraint != null)
            builder.append(constraint);

        return builder.toString();
    }

}
