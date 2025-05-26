package ru.myframework.tester.engine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.myframework.tester.annotation.After;
import ru.myframework.tester.annotation.Before;
import ru.myframework.tester.annotation.Test;
import ru.myframework.tester.util.ReflectionHelper;

@SuppressWarnings("java:S1118")
public class TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public static void runTestClass(String testClassName) {
        logger.info("Start running tests for class {}", testClassName);
        TestClassInfo testClassInfo = getTestClassInfo(testClassName);

        if (testClassInfo.getTestMethods().isEmpty()) {
            logger.warn("Test class {} has no test methods.", testClassName);
            return;
        }

        runTests(testClassInfo);

        logger.info("Tests for class {} have been completed. Results:", testClassName);

        logTestResults(testClassInfo.getTestMethods());
    }

    private static TestClassInfo getTestClassInfo(String testClassName) {
        Class<?> clazz = ReflectionHelper.getClassByName(testClassName);
        Object testClassInstance = ReflectionHelper.instantiate(clazz);
        Method[] methods = clazz.getDeclaredMethods();

        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        Map<Method, Boolean> testMethodsWithStatus = new HashMap<>();

        for (Method method : methods) {
            if (method.getAnnotation(Test.class) != null) {
                testMethodsWithStatus.put(method, false);
            } else if (method.getAnnotation(Before.class) != null) {
                beforeMethods.add(method);
            } else if (method.getAnnotation(After.class) != null) {
                afterMethods.add(method);
            }
        }

        return new TestClassInfo(testClassInstance, beforeMethods, afterMethods, testMethodsWithStatus);
    }

    private static void runTests(TestClassInfo testClassInfo) {
        Object testClassInstance = testClassInfo.getTestClassInstance();
        List<Method> beforeMethods = testClassInfo.getBeforeMethods();
        List<Method> afterMethods = testClassInfo.getAfterMethods();
        Map<Method, Boolean> testMethodsWithStatus = testClassInfo.getTestMethods();

        testMethodsWithStatus.forEach((key, value) -> {
            boolean result = executeTestMethod(testClassInstance, key, beforeMethods, afterMethods);
            testMethodsWithStatus.put(key, result);
        });
    }

    private static boolean executeTestMethod(
            Object testClassInstance, Method test, List<Method> beforeMethods, List<Method> afterMethods) {

        try {
            invokeLifecycleMethods(testClassInstance, beforeMethods);
            ReflectionHelper.callMethod(testClassInstance, test.getName());
            logger.info("Test {} passed.", test.getName());

            return true;
        } catch (Exception e) {
            logger.error("Test {} failed: {}", test.getName(), e.getMessage());

            return false;
        } finally {
            invokeLifecycleMethods(testClassInstance, afterMethods);
        }
    }

    private static void invokeLifecycleMethods(Object testClassInstance, List<Method> methods) {
        methods.forEach(method -> ReflectionHelper.callMethod(testClassInstance, method.getName()));
    }

    private static void logTestResults(Map<Method, Boolean> tests) {
        tests.forEach((key, value) -> {
            if (Boolean.TRUE.equals(value)) {
                logger.info("Test {} passed.", key.getName());
            } else {
                logger.info("Test {} failed.", key.getName());
            }
        });
    }

    static final class TestClassInfo {
        private final Object testClassInstance;
        private final List<Method> beforeMethods;
        private final List<Method> afterMethods;
        private final Map<Method, Boolean> testMethods;

        private TestClassInfo(
                Object testClassInstance, List<Method> before, List<Method> after, Map<Method, Boolean> tests) {
            this.testClassInstance = testClassInstance;
            this.beforeMethods = before;
            this.afterMethods = after;
            this.testMethods = tests;
        }

        private Object getTestClassInstance() {
            return testClassInstance;
        }

        private List<Method> getBeforeMethods() {
            return beforeMethods;
        }

        private List<Method> getAfterMethods() {
            return afterMethods;
        }

        private Map<Method, Boolean> getTestMethods() {
            return testMethods;
        }
    }
}
