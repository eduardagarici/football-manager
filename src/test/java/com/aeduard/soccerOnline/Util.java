package com.aeduard.soccerOnline;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

public class Util {

    public static <T> T extractContentAsType(Class<T> clazz, MvcResult mvcResult, ObjectMapper objectMapper) throws Exception {
        String content = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(content, clazz);
    }
}
