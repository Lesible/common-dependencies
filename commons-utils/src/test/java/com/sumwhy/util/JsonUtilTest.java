package com.sumwhy.util;

import com.fasterxml.jackson.databind.JavaType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>  </p>
 * <p> created at 2021-11-13 15:42 by lesible </p>
 *
 * @author 何嘉豪
 */
public class JsonUtilTest {

    private static final Logger log = LoggerFactory.getLogger(JsonUtilTest.class);

    @Test
    public void testIfJsonStrIsEmpty() {
        // String json = null;
        String json = "";
        TestType testType = JsonUtil.parseJson(json, TestType.class);
        log.info("testType: {}", testType);
    }

    @Test
    public void writeJson() {
        TestType testType = new TestType("test", "test");
        List<TestType> testTypes = Collections.singletonList(testType);
        log.info("JsonUtil.jsonValue(testType): {}", JsonUtil.jsonValue(testTypes));
    }

    @Test
    public void deepClone() {
        String jsonStr = "[{\"username\":\"test\",\"password\":\"test\"}]";
        JavaType mapType = JsonUtil.constructMapType(HashMap.class, String.class, String.class);
        List<Map<String, String>> list = JsonUtil.parseList(jsonStr, mapType);
        List<TestType> testType = JsonUtil.deepClone(list, List.class, TestType.class);
        log.info("testType: {}", testType);
    }

    private static class TestType {
        private String username;

        private String password;

        public TestType() {
        }

        public TestType(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public String toString() {
            return "TestType{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


}
