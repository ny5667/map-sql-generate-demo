package com.example.demo1.vo;

import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

import java.util.ArrayList;
import java.util.List;

public class RowBO {

    private TInsertSqlStatement insertSqlStatement;

    private List<String> columns;

    private List<String> values;

    {
        columns = new ArrayList<>();
        values = new ArrayList<>();
    }

    public RowBO(List<String> columns, List<String> values,TInsertSqlStatement insertSqlStatement) {
        this.columns = columns;
        this.values = values;
        this.insertSqlStatement = insertSqlStatement;
    }

    public TInsertSqlStatement getInsertSqlStatement() {
        return insertSqlStatement;
    }

    public void setInsertSqlStatement(TInsertSqlStatement insertSqlStatement) {
        this.insertSqlStatement = insertSqlStatement;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
