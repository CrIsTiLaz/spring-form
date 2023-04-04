package com.example.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForeignKey {
    private String sourceTable;
    private String sourceColumn;
    private String referencedTable;
    private String referencedColumn;
    private String keyName;
}