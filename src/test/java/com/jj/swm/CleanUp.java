package com.jj.swm;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanUp {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    @Autowired
    public CleanUp(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Transactional
    public void all() {
        entityManager.getMetamodel().getEntities().forEach(e -> {
            String tableName = e.getJavaType().getAnnotation(Table.class).name();
            if(!tableName.split("_")[0].startsWith("external") && !tableName.startsWith("users")) {
                jdbcTemplate.execute("TRUNCATE table " + tableName + " CASCADE");
                jdbcTemplate.execute("ALTER SEQUENCE " + tableName + "_id_seq RESTART WITH 1");
            }
        });

        jdbcTemplate.execute("TRUNCATE table users CASCADE");
    }
}
