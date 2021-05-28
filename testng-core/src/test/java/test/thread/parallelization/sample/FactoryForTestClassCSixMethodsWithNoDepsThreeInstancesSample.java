package test.thread.parallelization.sample;

import org.testng.TestNGException;
import org.testng.annotations.Factory;
import org.testng.internal.objects.InstanceCreator;

import java.util.ArrayList;
import java.util.List;

public class FactoryForTestClassCSixMethodsWithNoDepsThreeInstancesSample {
    @Factory
    public Object[] init() {
        List<Object> instances = new ArrayList<>();

        try {
            instances.add(InstanceCreator.newInstance(TestClassCSixMethodsWithNoDepsSample.class));
            instances.add(InstanceCreator.newInstance(TestClassCSixMethodsWithNoDepsSample.class));
            instances.add(InstanceCreator.newInstance(TestClassCSixMethodsWithNoDepsSample.class));
        } catch (TestNGException e) {
            throw new RuntimeException(
                    "Could not instantiate an instance of TestClassCSixMethodsWithNoDepsSample", e
            );
        }

        return instances.toArray();
    }
}
