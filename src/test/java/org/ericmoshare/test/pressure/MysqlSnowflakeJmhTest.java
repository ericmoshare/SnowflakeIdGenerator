package org.ericmoshare.test.pressure;

import org.ericmoshare.uidgenerator.idgenerator.IdGenerator;
import org.ericmoshare.uidgenerator.idgenerator.MysqlSnowflakeIdGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;

/**
 * Created by eric.mo on 2017/9/20.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
@Threads(value = 50)
public class MysqlSnowflakeJmhTest {

    private IdGenerator idGenerator;

    static ConfigurableApplicationContext context;

    @Setup(Level.Trial)
    public void init() {
        try {
            if (context == null) {
                context = new ClassPathXmlApplicationContext("classpath*:/src/main/resources/spring-demo.xml");
            }
            idGenerator = context.getBean(MysqlSnowflakeIdGenerator.class);
            System.out.println("init:" + idGenerator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TearDown
    public void destory() {
        // destory
        context.close();
    }

    public static void main(String[] args) throws RunnerException {

        URLClassLoader classLoader = (URLClassLoader) MysqlSnowflakeJmhTest.class.getClassLoader();
        StringBuilder classpath = new StringBuilder();
        for (URL url : classLoader.getURLs()) {
            classpath.append(url.getPath()).append(File.pathSeparator);
        }
        classpath.append("/Users/mozengsheng/git/ericmoshare/uid-generator/src/main/resources/").append(File.pathSeparator);
        System.setProperty("java.class.path", classpath.toString());

        Options opt = new OptionsBuilder()
                .include(".*" + MysqlSnowflakeJmhTest.class.getSimpleName() + ".*")
                .forks(1)
                .shouldFailOnError(true)
                .build();
        new Runner(opt).run();

    }

    @Benchmark
    public void benchPrecondition() {
        try {

            idGenerator.nextId();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
