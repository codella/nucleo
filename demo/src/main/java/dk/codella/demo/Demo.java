package dk.codella.demo;

import dk.codella.nucleo.Nucleo;

public class Demo {

    public static void main(String[] args) {
        System.out.println("PID: " + ProcessHandle.current().pid());

        new Nucleo()
            .withBeanClasses(DemoRoutes.class, DemoResteasyResource.class)
            .withRoutesHttpServer()
            .withResteasyHttpServer()
            .start();
    }

}
