package com.example.demo.utils;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class IndirectLinkChecker {
    private final JdbcTemplate jdbcTemplate;
    private final Graph<String, DefaultEdge> graph;

    public IndirectLinkChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
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
        if (!graph.containsVertex(toTable)) {
            return false;
        }
        GraphPath<String, DefaultEdge> shortestPath = DijkstraShortestPath.findPathBetween(graph, fromTable, toTable);
        return shortestPath != null && shortestPath.getLength() > 1;
    }
}