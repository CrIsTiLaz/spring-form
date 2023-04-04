package com.example.demo;

import com.example.demo.utils.IndirectLinkChecker;
import com.example.demo.utils.JoinInfoExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws SQLException {
		DataSource dataSource = new DriverManagerDataSource("jdbc:postgresql://localhost:5432/db", "postgres", "qwert");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		JoinInfoExtractor joinInfoExtractor = new JoinInfoExtractor(dataSource);
		var testare = joinInfoExtractor.getJoinMetadata("curs", "student");
		var query1 = joinInfoExtractor.generateJoinQuery(testare.get(0).getSourceTable(), testare.get(0).getSourceColumn(), testare.get(0).getReferencedTable(), testare.get(0).getReferencedColumn());
		System.out.println(query1);
		List<String> sourceColumns = Arrays.asList("titlu", "sala");
		List<String> referencedColumns = List.of("nume");
		//var query2 = joinInfoExtractor.generateJoinQuery(sourceColumns, referencedColumns, testare.get(0).getSourceTable(), testare.get(0).getSourceColumn(), testare.get(0).getReferencedTable(), testare.get(0).getReferencedColumn());
		//System.out.println(query2);


		SpringApplication.run(DemoApplication.class, args);
		//JdbcTemplate jdbcTemplate = new JdbcTemplate(); // creare obiect JdbcTemplate
		String fromTable = "student_curs"; // numele tabelei sursa
		String toTable = "angajati"; // numele tabelei destinație

		boolean hasIndirectLink = checkIndirectLink(jdbcTemplate, fromTable, toTable); // apelare funcție

		if (hasIndirectLink) {
			System.out.println("Există o legătură indirectă între " + fromTable + " și " + toTable);
		} else {
			System.out.println("Nu există o legătură indirectă între " + fromTable + " și " + toTable);
		}


	}

	public static boolean checkIndirectLink(JdbcTemplate jdbcTemplate, String fromTable, String toTable) {
		IndirectLinkChecker indirectLinkChecker = new IndirectLinkChecker(jdbcTemplate);
		return indirectLinkChecker.hasIndirectLink(fromTable, toTable);
	}
//		var user = new User();
//		System.out.println(user.getNume());
}