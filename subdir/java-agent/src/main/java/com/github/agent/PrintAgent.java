package com.github.agent;

import com.github.agent.common.JavaAgentCommonVersion;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;

public class PrintAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent premain Thread " + Thread.currentThread().getName());
        System.out.println("Agent premain ClassLoader " + Thread.currentThread().getContextClassLoader());
        JavaAgentCommonVersion.printVersion();
        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("com.github"))
                .transform(new MyTransformer())
                .installOn(inst);
    }

    public static class MyTransformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            // 方式1
//            return builder.method(ElementMatchers.isAnnotatedWith(ElementMatchers.nameStartsWith("com.github.agent.common.annotation.Print")))
//                    .intercept(MethodDelegation.to(MethodInterceptor.class));
// 方式2
//
            System.out.println("Agent Transformer Thread " + Thread.currentThread().getName());
            System.out.println("TypeDescription " + typeDescription);
            System.out.println("Agent Transformer ClassLoader " + classLoader);
            Class<?> printClass = null;
            try {
                printClass = classLoader.loadClass("com.github.agent.common.annotation.Print");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return builder.method(ElementMatchers.isAnnotatedWith((Class<? extends Annotation>) printClass))
                    .intercept(MethodDelegation.to(PrintMethodInterceptor.class));
        }
    }
}