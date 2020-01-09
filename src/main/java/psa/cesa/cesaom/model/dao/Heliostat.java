package psa.cesa.cesaom.model.dao;

import java.nio.ByteBuffer;

/**
 * DAO which represents a physical heliostat
 */
public class Heliostat {
    /**
     * @param address Represents the modbus slave address
     * @param state Static and dynamic positions representation
     * @param event Operation, security, communications and such events
     * @param diagnosisAz Axis diagnosis
     * @param diagnosisEl Axis diagnosis
     * @param positionAz Actual azimuth position
     * @param positionEL Actual elevation position
     * @param setPointAZ Azimuth set point
     * @param setPointEL Elevation set point
     */

    private int address;
    private int state;
    private int event;
    private int diagnosysAZ, diagnosysEL;
    private int positionAZ, positionEL;
    private int setPointAZ, setPointEL;

    public Heliostat(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getDiagnosysAZ() {
        return diagnosysAZ;
    }

    public void setDiagnosysAZ(int diagnosysAZ) {
        this.diagnosysAZ = diagnosysAZ;
    }

    public int getDiagnosysEL() {
        return diagnosysEL;
    }

    public void setDiagnosysEL(int diagnosysEL) {
        this.diagnosysEL = diagnosysEL;
    }

    public int getPositionAZ() {
        return positionAZ;
    }

    public void setPositionAZ(int positionAZ) {
        this.positionAZ = positionAZ;
    }

    public int getPositionEL() {
        return positionEL;
    }

    public void setPositionEL(int positionEL) {
        this.positionEL = positionEL;
    }

    public int getSetPointAZ() {
        return setPointAZ;
    }

    public void setSetPointAZ(int setPointAZ) {
        this.setPointAZ = setPointAZ;
    }

    public int getSetPointEL() {
        return setPointEL;
    }

    public void setSetPointEL(int setPointEL) {
        this.setPointEL = setPointEL;
    }

    /**
     * Sets the <code>Heliostat</code> attributes
     *
     * @param receivedBuffer
     */
    public void setAttributes(ByteBuffer receivedBuffer) {
        for (int i = 0; i < receivedBuffer.array().length; i++) {
            Byte b = receivedBuffer.get(i);
            switch (i) {
                case 4:
                    setState(b);
                    break;
                case 6:
                    setEvent(b);
                    break;
                case 8:
                    setDiagnosysAZ(b);
                    break;
                case 10:
                    setDiagnosysEL(b);
                    break;
                case 12:
                    setPositionAZ(b);
                    break;
                case 14:
                    setPositionEL(b);
                    break;
                case 16:
                    setSetPointAZ(b);
                    break;
                case 18:
                    setSetPointEL(b);
                    break;
            }
        }
    }

    /**
     * Converts the least significant nibble from state byte to a string message
     *
     * @return string static position message
     */
    public String state0ToString() {
        StringBuilder state0 = new StringBuilder();
        int nibble0 = 0x0f & state;
        switch (nibble0) {
            case 0x0:
                state0.append("Operación local");
                break;
            case 0x1:
                state0.append("Consiga fija");
                break;
            case 0x2:
                state0.append("Busqueda de ceros");
                break;
            case 0x3:
                state0.append("Fuera de servicio");
                break;
            case 0x4:
                state0.append("Posición de defensa");
                break;
            case 0x5:
                state0.append("Abatimiento normal");
                break;
            case 0x6:
                state0.append("Blanco tierra");
                break;
            case 0x7:
                state0.append("Blanco pasillo 1");
                break;
            case 0x8:
                state0.append("Blanco pasillo 2");
                break;
            case 0x9:
                state0.append("Blanco pasillo 3");
                break;
            case 0xa:
                state0.append("Blanco pasillo 4");
                break;
            case 0xb:
                state0.append("Seguimiento desfasado");
                break;
            case 0xc:
                state0.append("Blanco de emergencia");
                break;
            case 0xd:
                state0.append("Seguimiento normal a caldera");
                break;
            case 0xe:
                state0.append("Foco");
                break;
            case 0xf:
                state0.append("Seguimiento normal al sol");
                break;
        }
        return state0.toString();
    }

    /**
     * Converts the most significant nibble from state byte to a string message
     *
     * @return string dynamic position message
     */
    public String state1ToString() {
        StringBuilder state1 = new StringBuilder();
        int nibble1 = 0xf0 & state;
        if ((nibble1 & 0x80) == 0x80) {
            state1.append("Aviso error");
        }
        if ((nibble1 & 0x40) == 0x40) {
            state1.append(" Aviso evento");
        }
        if ((nibble1 & 0x20) == 0x20) {
            state1.append(" Consigna alcanzada EL");
        }
        if ((nibble1 & 0x10) == 0x10) {
            state1.append(" Consigna alcanzada AZ");
        }
        return state1.toString();
    }

    /**
     * Converts the two least significant bits from event byte to a string message
     *
     * @return string operation event message
     */
    public String eventOperationToString() {
        StringBuilder operation = new StringBuilder("Operación ");
        int coupleBits0 = 0x3 & event;
        switch (coupleBits0) {
            case 0x0:
                operation.append("OK");
                break;
            case 0x1:
                operation.append("Fuera de servicio");
                break;
            case 0x2:
                operation.append("Heliostato teleconfigurado");
                break;
        }
        return operation.toString();
    }

    /**
     * Converts the bits at position 2 and 3 from event byte to a string message
     *
     * @return string security event message
     */
    public String eventSecurityToString() {
        StringBuilder security = new StringBuilder("Seguridad ");
        int coupleBits1 = 0xc & event;
        switch (coupleBits1) {
            case 0x0:
                security.append("OK");
                break;
            case 0x4:
                security.append("Código de cliente erróneo");
                break;
        }
        return security.toString();
    }

    /**
     * Converts the bits at position 4 and 5 from event byte to a string message
     *
     * @return string communications event message
     */
    public String eventComToString() {
        StringBuilder communications = new StringBuilder("Comunicaciones ");
        int coupleBits2 = 0x30 & event;
        switch (coupleBits2) {
            case 0x0:
                communications.append("OK");
                break;
            case 0x10:
                communications.append("Fallo de comunicaciones");
                break;
            case 0x20:
                communications.append("No acpeta el comando");
                break;
        }
        return communications.toString();
    }

    /**
     * Converts the bits at position 6 and 7 from event byte to a string message
     *
     * @return string clock event message
     */
    public String eventCLToString() {
        StringBuilder clock = new StringBuilder("Reloj ");
        int coupleBits3 = 0xc0 & event;
        switch (coupleBits3) {
            case 0x0:
                clock.append("OK");
                break;
            case 0x40:
                clock.append("Fallo del micro esclavo");
                break;
            case 0x80:
                clock.append("Fallo batería");
                break;
        }
        return clock.toString();
    }

    /**
     * Converts the two least significant bits from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz0ToString() {
        StringBuilder diagnosysAz0 = new StringBuilder("Movimiento ");
        int coupleBits0 = 0x3 & diagnosysAZ;
        switch (coupleBits0) {
            case 0x0:
                diagnosysAz0.append("OK");
                break;
            case 0x1:
                diagnosysAz0.append("No mueve con motor ON");
                break;
            case 0x2:
                diagnosysAz0.append("Mueve con motor OFF");
                break;
            case 0x3:
                diagnosysAz0.append("Gira al revés");
                break;
        }
        return diagnosysAz0.toString();
    }

    /**
     * Converts the bits at position 2 and 3 from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz1ToString() {
        StringBuilder diagnosysAz1 = new StringBuilder("Oscilación ");
        int coupleBits1 = 0xc & diagnosysAZ;
        switch (coupleBits1) {
            case 0x0:
                diagnosysAz1.append("OK");
                break;
            case 0x4:
                diagnosysAz1.append("Fallo oscila");
                break;
            case 0x8:
                diagnosysAz1.append("Fallo servo");
                break;
        }
        return diagnosysAz1.toString();
    }

    /**
     * Converts the bits at position 4 and 5 from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz2ToString() {
        StringBuilder diagnosysAz2 = new StringBuilder("Posición ");
        int coupleBits2 = 0x30 & diagnosysAZ;
        switch (coupleBits2) {
            case 0x0:
                diagnosysAz2.append("OK");
                break;
            case 0x10:
                diagnosysAz2.append("Posición extrema oeste");
                break;
            case 0x20:
                diagnosysAz2.append("Posición extrema este");
                break;
        }
        return diagnosysAz2.toString();
    }

    /**
     * Converts the bits at position 6 and 7 from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz3ToString() {
        StringBuilder diagnosysAz3 = new StringBuilder("Aviso ");
        int coupleBits3 = 0xc0 & diagnosysAZ;
        switch (coupleBits3) {
            case 0x0:
                diagnosysAz3.append("OK");
                break;
            case 0x40:
                diagnosysAz3.append("Cero encontrado");
                break;
            case 0x80:
                diagnosysAz3.append("Banda ampliada");
                break;
        }
        return diagnosysAz3.toString();
    }

    /**
     * Converts the two least significant bits from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl0ToString() {
        StringBuilder diagnosysEl0 = new StringBuilder("Movimiento ");
        int coupleBits0 = 0x3 & diagnosysEL;
        switch (coupleBits0) {
            case 0x0:
                diagnosysEl0.append("OK");
                break;
            case 0x1:
                diagnosysEl0.append("No mueve con motor ON");
                break;
            case 0x2:
                diagnosysEl0.append("Mueve con motor OFF");
                break;
            case 0x3:
                diagnosysEl0.append("Gira al revés");
                break;
        }
        return diagnosysEl0.toString();
    }

    /**
     * Converts the bits at position 2 and 3 from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl1ToString() {
        StringBuilder diagnosysEl1 = new StringBuilder("Oscilación ");
        int coupleBits1 = 0xc & diagnosysEL;
        switch (coupleBits1) {
            case 0x0:
                diagnosysEl1.append("OK");
                break;
            case 0x4:
                diagnosysEl1.append("Fallo oscila");
                break;
            case 0x8:
                diagnosysEl1.append("Fallo servo");
                break;
        }
        return diagnosysEl1.toString();
    }

    /**
     * Converts the bits at position 4 and 5 from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl2ToString() {
        StringBuilder diagnosysEl2 = new StringBuilder("Posición ");
        int coupleBits2 = 0x30 & diagnosysEL;
        switch (coupleBits2) {
            case 0x0:
                diagnosysEl2.append("OK");
                break;
            case 0x10:
                diagnosysEl2.append("Posición extrema oeste");
                break;
            case 0x20:
                diagnosysEl2.append("Posición extrema este");
                break;
        }
        return diagnosysEl2.toString();
    }

    /**
     * Converts the bits at position 6 and 7 from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl3ToString() {
        StringBuilder diagnosysEl3 = new StringBuilder("Aviso ");
        int coupleBits3 = 0xc0 & diagnosysEL;
        switch (coupleBits3) {
            case 0x0:
                diagnosysEl3.append("OK");
                break;
            case 0x40:
                diagnosysEl3.append("Cero encontrado");
                break;
            case 0x80:
                diagnosysEl3.append("Banda ampliada");
                break;
        }
        return diagnosysEl3.toString();
    }
}
