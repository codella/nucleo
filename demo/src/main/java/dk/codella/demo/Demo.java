package dk.codella.demo;

import dk.codella.nucleo.Nucleo;
import lombok.extern.flogger.Flogger;

@Flogger
public class Demo {

    public static void main(String[] args) {
        log.atInfo().log("PID: %s", ProcessHandle.current().pid());

        new Nucleo()
            .withBeanClasses(DemoRoutes.class, DemoResteasyResource.class)
            .withRoutesHttpServer()
            .withResteasyHttpServer()
            .start();
    }

}
