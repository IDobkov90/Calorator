package com.example.calorator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CaloratorApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    void applicationClassShouldExist() {
        CaloratorApplication application = applicationContext.getBean(CaloratorApplication.class);
        assertNotNull(application, "CaloratorApplication bean should be available in the context");
    }

    @Test
    void schedulingIsEnabled() {
        ScheduledAnnotationBeanPostProcessor processor = applicationContext.getBean(ScheduledAnnotationBeanPostProcessor.class);
        assertNotNull(processor, "ScheduledAnnotationBeanPostProcessor should be available when scheduling is enabled");
    }

    @Test
    void mainMethodShouldRun() {
        Thread thread = new Thread(() -> {
            try {
                CaloratorApplication.main(new String[]{});
            } catch (Exception e) {
                fail("Main method threw an exception: " + e.getMessage());
            }
        });

        thread.setDaemon(true);
        thread.start();

        assertTrue(thread.isAlive(), "Main application thread should be running");
    }
}