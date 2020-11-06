package jpulsar;

import jpulsar.lifecycle.TestLifecycleOperations;

public class DynamicTest extends TestLifecycleOperations<Void, DynamicTest> {
    private String name;
    private Runnable runnable;
    private String reportTable;
    private Object tableColumn;
    private Object tableRow;

    public DynamicTest(Runnable runnable) {
        this.runnable = runnable;
    }

    public DynamicTest(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public DynamicTest(String tableColumn,
                       Object tableRow,
                       Runnable runnable) {
        this.runnable = runnable;
        this.tableColumn = tableColumn;
        this.tableRow = tableRow;
    }

    public DynamicTest(
            String tableColumn,
            Object tableRow,
            String name,
            Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
        this.tableColumn = tableColumn;
        this.tableRow = tableRow;
    }

    public static DynamicTest row(Object tableRow, Runnable runnable) {
        return row(tableRow, null, runnable);
    }

    public static DynamicTest row(Object tableRow, String name, Runnable runnable) {
        DynamicTest test = new DynamicTest(name, runnable);
        test.tableRow = tableRow;
        return test;
    }

    public static DynamicTest column(Object tableColumn, Runnable runnable) {
        return column(tableColumn, null, runnable);
    }

    public static DynamicTest column(Object tableColumn, String name, Runnable runnable) {
        DynamicTest test = new DynamicTest(name, runnable);
        test.tableColumn = tableColumn;
        return test;
    }

    public DynamicTest getThis() {
        return this;
    }

    public DynamicTest setReportTable(String column, Object row) {
        tableColumn = column;
        tableRow = row;
        return this;
    }

    public String getName() {
        return name;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public String getReportTable() {
        return reportTable;
    }

    public Object getTableColumn() {
        return tableColumn;
    }

    public Object getTableRow() {
        return tableRow;
    }
}