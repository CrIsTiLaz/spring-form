package com.example.demo.controller;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExportTables {

    @Autowired
    private DataSource dataSource;

    public void exportData() throws Exception {

        //Extractia datelor din tabela 1
        String query1 = "SELECT nume, prenume, email FROM student";
        List<Object[]> data1 = getDataFromQuery(query1);

        //Extractia datelor din tabela 2
        String query2 = "SELECT adresa, oras, tara FROM tabela2";
        List<Object[]> data2 = getDataFromQuery(query2);

        //Crearea fisierului Excel si scrierea datelor
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("Tabela 1");
        XSSFSheet sheet2 = workbook.createSheet("Tabela 2");

        //Scrierea datelor din tabela 1
        int rowCount = 0;
        for (Object[] aRow : data1) {
            Row row = sheet1.createRow(rowCount++);
            int columnCount = 0;
            for (Object field : aRow) {
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }

        //Scrierea datelor din tabela 2
        rowCount = 0;
        for (Object[] aRow : data2) {
            Row row = sheet2.createRow(rowCount++);
            int columnCount = 0;
            for (Object field : aRow) {
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }

        //Salvarea fisierului Excel
        String excelFileName = "export.xlsx";
        FileOutputStream outputStream = new FileOutputStream(excelFileName);
        workbook.write(outputStream);
        workbook.close();

        System.out.println("Datele au fost exportate cu succes");

    }

    private List<Object[]> getDataFromQuery(String query) throws SQLException {
        List<Object[]> data = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Object[] row = new Object[] { resultSet.getString(1), resultSet.getString(2), resultSet.getString(3) };
                data.add(row);
            }
        }

        return data;
    }

}