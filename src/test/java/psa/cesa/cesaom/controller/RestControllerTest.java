package psa.cesa.cesaom.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestControllerTest {


    RestController restController = mock(RestController.class);

    @Test
    void getNumber() {
        when(restController.getNumber()).thenReturn(1);
        assertEquals(1, restController.getNumber());
    }

    @Test
    void testGetCache() {
    }

    @Test
    void command() {
    }

    @Test
    void focus() {
    }
}