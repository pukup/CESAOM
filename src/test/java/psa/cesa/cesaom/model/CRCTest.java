package psa.cesa.cesaom.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CRCTest {

    //        for (byte b : (CRC.calculate(new byte[]{01, 03, 00, 10, 00, 0x08}, 6))) {
    //            System.out.println(b);
    //        };

    @Test
    void calculateTest(){
        byte[] bytes = new byte[]{100, 14};
        byte[] crc = CRC.calculate(new byte[]{01, 03, 00, 10, 00, 0x08}, 6);
        assertEquals(bytes[0], crc[0]);
        assertEquals(bytes[1], crc[1]);
    }
}