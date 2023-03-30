package com.example.demo;

import com.example.demo.utils.IndirectLinkChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		DataSource dataSource = new DriverManagerDataSource("jdbc:postgresql://localhost:5432/db", "postgres", "qwert");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);


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