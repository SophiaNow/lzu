package org.hbrs.lzu;


import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RuntimeEnvironmentTest {

    private static RuntimeEnvironment rte = RuntimeEnvironment.getInstance();
    ;
    private static String jarPath = "C:\\Users\\sophi\\Informatik\\2\\OOKA\\Uebungen\\Uebung2\\lzu\\component\\target\\component-1.0-SNAPSHOT.jar";
    private static UUID comp1ID = null;
    private static UUID comp2ID = null;
    private static String comp1Name = "component1";
    private static String comp2Name = "component2";


    @Test
    @Order(1)
    void deploy() {
        try {
            comp1ID = rte.deployComponent(jarPath, comp1Name);
            Component comp1 = rte.getComponents().get(comp1ID);
            assert rte.getComponents().size() == 1;
            assertEquals(comp1Name, comp1.getName(), "Component name false!");
            assert comp1.getState() instanceof Deployed;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(2)
    void deploySame() {
        try {
            comp2ID = rte.deployComponent(jarPath, comp2Name);
            assert rte.getComponents().size() == 2;
            Component comp2 = rte.getComponents().get(comp2ID);
            assertEquals(comp2Name, comp2.getName(), "Component name false!");
            assert comp2.getState() instanceof Deployed;
            Class<?> class2 = comp2.startingClass;
            Class<?> class1 = rte.getComponents().get(comp2ID).startingClass;
            // assertNotEquals(class1, class2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(3)
    void stopBeforeDeployed() {
        rte.stopComponent(comp1ID);
        assertTrue(rte.getComponents().get(comp1ID).getState() instanceof Deployed);
    }

    @Test
    @Order(4)
    void startSame() {
        try {
            rte.startComponent(comp1ID);
            rte.startComponent(comp2ID);
            Thread.sleep(5);
            Component comp2 = rte.getComponents().get(comp2ID);
            assertTrue(comp2.getState() instanceof Running);
            assert rte.getComponents().get(comp1ID).getState() instanceof Running;

        } catch (InvocationTargetException | IllegalAccessException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(5)
    void deleteBeforeStopped() {
        rte.deleteComponent(comp1ID);
        assertTrue(rte.getComponents().get(comp1ID).getState() instanceof Running);
        assertEquals(rte.getThreads().size(), 2);
        assertEquals(rte.getComponents().size(), 2);
        rte.getThreads().get(comp1ID); // Sonst exception
        rte.stopComponent(comp1ID);
    }

    @Test
    @Order(6)
    void startAfterStop() {
        assertTrue(rte.getComponentState(comp1ID) instanceof Stopped);
        try {
            rte.startComponent(comp1ID);
            Thread.sleep(5);
            assertTrue(rte.getComponentState(comp1ID) instanceof Running);
            rte.stopComponent(comp1ID);
        } catch (InvocationTargetException | IllegalAccessException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(7)
    void delete() {
        assertTrue(rte.deleteComponent(comp1ID));
        assertTrue(rte.getComponentState(comp1ID) instanceof Disposed);
        Component lastComp = rte.getComponents().get(comp2ID);
        assertTrue(lastComp.getState() instanceof Running);
        assertNotNull(rte.getThreads().get(comp2ID));
        assertNull(rte.getThreads().get(comp1ID));
    }

    @AfterAll
    static void tearDown() {
        rte = null;
        comp1ID = null;
        comp2ID = null;
    }
}