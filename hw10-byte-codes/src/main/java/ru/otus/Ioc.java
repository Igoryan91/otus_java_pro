package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
final class Ioc {
    private static final Logger log = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {}

    static <T> T createClass(T target) {

        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }

        Class<?> targetClass = target.getClass();
        if (targetClass.getInterfaces().length == 0) {
            throw new IllegalArgumentException("Target must implement at least one interface");
        }

        InvocationHandler handler = new DemoInvocationHandler(target);
        return (T) Proxy.newProxyInstance(Ioc.class.getClassLoader(), targetClass.getInterfaces(), handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final Object target;

        DemoInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Method methodImpl = target.getClass().getMethod(method.getName(), method.getParameterTypes());

            if (methodImpl.isAnnotationPresent(Log.class)) {
                log.info("Executed method: {}, param: {}", method.getName(), args[0]);
            }

            return method.invoke(target, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" + target.getClass().getName() + "=" + target + '}';
        }
    }
}
