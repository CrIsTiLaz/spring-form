package com.example.demo.utils;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class JoinInfoExtractor {
    private final JdbcTemplate jdbcTemplate;

    public JoinInfoExtractor(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<ForeignKey> getJoinMetadata(String sourceTable, String targetTable) {
        List<ForeignKey> foreignKeys = new ArrayList<>();

        String sql = "SELECT tc.table_name, kcu.column_name, ccu.table_name AS referenced_table_name," +
                "ccu.column_name AS referenced_column_name, tc.constraint_name " +
                "FROM information_schema.table_constraints AS tc " +
                "JOIN information_schema.key_column_usage AS kcu " +
                "  ON tc.constraint_name = kcu.constraint_name " +
                "JOIN information_schema.constraint_column_usage AS ccu " +
                "  ON ccu.constraint_name = tc.constraint_name " +
                "WHERE constraint_type = 'FOREIGN KEY' " +
                "  AND tc.table_name = ? " +
                "  AND ccu.table_name = ?";

        jdbcTemplate.query(sql, new Object[]{sourceTable, targetTable}, rs -> {
            ForeignKey foreignKey = new ForeignKey();
            foreignKey.setSourceTable(rs.getString("table_name"));
            foreignKey.setSourceColumn(rs.getString("column_name"));
            foreignKey.setReferencedTable(rs.getString("referenced_table_name"));
            foreignKey.setReferencedColumn(rs.getString("referenced_column_name"));
            foreignKey.setKeyName(rs.getString("constraint_name"));
            foreignKeys.add(foreignKey);
        });

        return foreignKeys;
    }

    public String generateJoinQuery(String sourceTable, String sourceColumn, String referencedTable, String referencedColumn) {
        return "SELECT * FROM " + sourceTable + " JOIN " + referencedTable + " ON " + sourceTable + "." + sourceColumn + " = " + referencedTable + "." + referencedColumn;
    }

    public String generateJoinQuery(List<String> sourceColumns, List<String> referencedColumns, String sourceTable, String sourceColumn, String referencedTable, String referencedColumn) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String column : sourceColumns) {
            joiner.add(sourceTable + "." + column);
        }
        for (String column : referencedColumns) {
            joiner.add(referencedTable + "." + column);
        }
        String query = "SELECT " + joiner.toString() + " FROM " + sourceTable + " JOIN " + referencedTable + " ON " + sourceTable + "." + sourceColumn + " = " + referencedTable + "." + referencedColumn;
        return query;
    }


}