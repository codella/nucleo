package dk.codella.demo;

import dk.codella.nucleo.Nucleo;

public class Demo {

    public static void main(String[] args) {
        System.out.println("PID: " + ProcessHandle.current().pid());

        new Nucleo()
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
