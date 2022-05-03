package it.mineblock.basementx.persistence.maria.structure;

import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaTable;
import it.mineblock.basementx.persistence.maria.structure.column.MariaColumn;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MariaTable implements AbstractMariaTable {

    /*
        State
     */

    private final MariaDatabase database;
    private final String tableName;

    private final List<MariaColumn> primaryKeys  = new ArrayList<>();
    private final List<MariaColumn> foreignKeys  = new ArrayList<>();
    private final List<MariaColumn> columns      = new ArrayList<>();

    /*
        Internal Use
     */

        public void addPrimaryKey(MariaColumn column) {
            primaryKeys.add(column);
        }

        public void addForeignKey(MariaColumn column) {
            foreignKeys.add(column);
        }

        public void addColumn(MariaColumn column) {
            columns.add(column);
        }

    /*
        Super Use
     */

    @Override
    public String getName() {
        return tableName;
    }

}
