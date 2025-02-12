package com.jj.swm.global.filter;

import lombok.RequiredArgsConstructor;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@Profile("local")
@RequiredArgsConstructor
public class QueryInspector implements StatementInspector {

    private final QueryCounter queryCounter;

    @Override
    public String inspect(String sql) {
        if (isInRequestScope()) {
            queryCounter.increase();
        }

        return sql;
    }

    private boolean isInRequestScope() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    public int getQueryCount() {
        return queryCounter.getCount();
    }
}
