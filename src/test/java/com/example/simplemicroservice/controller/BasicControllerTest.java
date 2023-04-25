package com.example.simplemicroservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class BasicControllerTest {
    /**
     * Method under test: {@link BasicController#getHome(Model)}
     */
    @Test
    void testGetHome() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R015 Method may be nondeterministic.
        //   The execution of the created test may depend on the runtime environment.
        //   See https://diff.blue/R015 to resolve this issue.

        BasicController basicController = new BasicController();
        assertEquals("home", basicController.getHome(new ConcurrentModel()));
    }
}

