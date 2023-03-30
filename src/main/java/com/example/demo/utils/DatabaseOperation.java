package com.example.demo.utils;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public void IndirectLinkChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        initGraph();
    }

    private void initGraph() {
        String query = "SELECT " +
                "    tc.table_name AS from_table, " +
                "    ccu.table_name AS to_table " +
                "FROM " +
                "    information_schema.table_constraints tc " +
                "    JOIN information_schema.key_column_usage kcu " +
                "        ON tc.constraint_name = kcu.constraint_name " +
                "    JOIN information_schema.constraint_column_usage ccu " +
                "        ON ccu.constraint_name = tc.constraint_name " +
                "WHERE " +
                "    tc.constraint_type = 'FOREIGN KEY';";
        List<String[]> rows = jdbcTemplate.query(query, (rs, rowNum) -> new String[]{rs.getString("from_table"), rs.getString("to_table")});
        for (String[] row : rows) {
            String fromTable = row[0];
            String toTable = row[1];
            graph.addVertex(fromTable);
            graph.addVertex(toTable);
            graph.addEdge(fromTable, toTable);
        }
    }

    public boolean hasIndirectLink(String fromTable, String toTable) {
        GraphPath<String, DefaultEdge> shortestPath = DijkstraShortestPath.findPathBetween(graph, fromTable, toTable);
        return shortestPath != null && shortestPath.getLength() > 1;
    }


}