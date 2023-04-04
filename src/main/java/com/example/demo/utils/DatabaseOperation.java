package com.example.demo.utils;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseOperation {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private Graph<String, DefaultEdge> graph;

    public String formatName(String initialName, String format) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        return initialName.concat(timestamp).concat(format);

    }

    public List<String> getTableNames() {
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'";
        return jdbcTemplate.queryForList(query, String.class);
    }

    public List<Map<String, Object>> getRowsForTable(String tableName) {
        String query = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(query);
    }

    public boolean existaLegaturaDirectaSauIndirecta(String tabela1, String tabela2) {

        // Verificăm dacă există o legătură directă între tabela1 și tabela2
        var sqlDirect = String.format("SELECT EXISTS (SELECT 1 FROM information_schema.table_constraints " +
                "WHERE constraint_type = 'FOREIGN KEY' AND table_name = '%s' " +
                "AND EXISTS (SELECT 1 FROM information_schema.constraint_column_usage " +
                "WHERE constraint_name = table_constraints.constraint_name " +
                "AND table_name = '%s'))", tabela1, tabela2);
        System.out.println("testtttttttttttttttttttttttttttttttttttttttttttt");
        System.out.println(sqlDirect);

        boolean existaLegaturaDirecta = Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlDirect, new Object[]{tabela1, tabela2}, Boolean.class));

        if (existaLegaturaDirecta) {
            return true;
        } else {
            // Verificăm dacă există o legătură indirectă între tabela1 și tabela2
            var sqlIndirect = "SELECT EXISTS (SELECT 1 FROM information_schema.table_constraints tc "
                    + "JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name "
                    + "JOIN information_schema.constraint_column_usage ccu ON ccu.constraint_name = tc.constraint_name "
                    + "WHERE tc.constraint_type = 'FOREIGN KEY' AND ccu.table_name = '" + tabela2 + "' "
                    + "AND kcu.table_name = '" + tabela1 + "');";

            boolean existaLegaturaIndirecta = Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlIndirect, new Object[]{tabela1, tabela2, tabela2}, Boolean.class));


            return existaLegaturaIndirecta;
        }
    }
    public Map<String, String> getForeignKeys(String tableName) {
        String sql = "SELECT tc.constraint_name, tc.table_name, kcu.column_name, "
                + "ccu.table_name AS foreign_table_name, ccu.column_name AS foreign_column_name "
                + "FROM information_schema.table_constraints AS tc "
                + "JOIN information_schema.key_column_usage AS kcu ON tc.constraint_name = kcu.constraint_name "
                + "JOIN information_schema.constraint_column_usage AS ccu ON ccu.constraint_name = tc.constraint_name "
                + "WHERE constraint_type = 'FOREIGN KEY' AND tc.table_name = " + tableName +  ";";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, tableName);

        Map<String, String> foreignKeys = new HashMap<>();
        for (Map<String, Object> row : results) {
            String constraintName = (String) row.get("constraint_name");
            String columnName = (String) row.get("column_name");
            String foreignTableName = (String) row.get("foreign_table_name");
            String foreignColumnName = (String) row.get("foreign_column_name");

            foreignKeys.put(constraintName, "Foreign key column: " + columnName + " references Primary key column: " + foreignColumnName + " in table: " + foreignTableName);
        }

        return foreignKeys;
    }

}