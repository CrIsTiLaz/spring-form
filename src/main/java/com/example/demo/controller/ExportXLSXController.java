package com.example.demo.controller;

import com.example.demo.utils.DatabaseOperation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

@Controller
public class ExportXLSXController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DatabaseOperation databaseOperation;
    @GetMapping("/exportXLSX")
    public void export() throws IOException, SQLException {

        List<String> tableNames = databaseOperation.getTableNames();

        for (String tableName : tableNames) {
            String selectQuery = "SELECT * FROM " + tableName;
            ResultSet resultSet = jdbcTemplate.getDataSource().getConnection().createStatement().executeQuery(selectQuery);

            FileOutputStream outputStream;
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet(tableName);

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                Row headerRow = sheet.createRow(0);
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Cell cell = headerRow.createCell(i - 1);
                    cell.setCellValue(columnName);
                }

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = resultSet.getObject(i);
                        Cell cell = row.createCell(i - 1);
                        cell.setCellValue(value.toString());
                    }
                }

                String fileName = databaseOperation.formatName(tableName, ".xlsx");
                outputStream = new FileOutputStream(fileName);
                workbook.write(outputStream);
            }
            outputStream.close();
        }
    }
}