package dk.codella.demo;

import dk.codella.nucleo.Nucleo;

import java.util.Set;

public class Demo {

    public static void main(String[] args) {
        System.out.println("PID: " + ProcessHandle.current().pid());

        new Nucleo()
            .withBeanClasses(DemoRoutes.class)
            .withHttpServer()
            .start();
    }

//    public static class Resource {
//        @GET
//        @Path("/somepath")
//        @Produces("text/plain")
//        public String context() {
//            return "the-response";
//        }
//    }
}
