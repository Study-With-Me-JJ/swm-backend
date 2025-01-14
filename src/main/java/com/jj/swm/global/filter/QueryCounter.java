package com.jj.swm.global.filter;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Profile("local")
public class QueryCounter {

    private int count = 0;

    public void increase() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
