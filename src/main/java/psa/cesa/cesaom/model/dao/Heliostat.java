package psa.cesa.cesaom.model.dao;

import java.nio.ByteBuffer;

/**
 * DAO which represents a physical heliostat.
 */
public class Heliostat {
    /**
     * @param id Represents the modbus slave address.
     * @param state Static and dynamic positions representation.
     * @param event Operation, security, communications and such events.
     * @param diagnosisAz Axis diagnosis.
     * @param diagnosisEl Axis diagnosis.
     * @param positionAz Actual azimuth position.
     * @param positionEL Actual elevation position.
     * @param setPointAZ Azimuth set point.
     * @param setPointEL Elevation set point.
     */

    private int id;
    private int state;
    private int event;
    private int diagnosisAZ, diagnosisEL;
    private int positionAZ, positionEL;
    private int setPointAZ, setPointEL;

    public Heliostat(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return diagnosisAZ;
    }

    public void setDiagnosysAZ(int diagnosisAZ) {
        this.diagnosisAZ = diagnosisAZ;
    }

    public int getDiagnosysEL() {
        return diagnosisEL;
    }

    public void setDiagnosysEL(int diagnosisEL) {
        this.diagnosisEL = diagnosisEL;
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
     * Sets the <code>Heliostat</code> attributes by separating the modbus frame into individual bytes.
     *
     * @param receivedBuffer bytes frame from the modbus RTU.
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
     * Converts the least significant nibble from state byte to a string message.
     *
     * @return static position message.
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
     * Converts the most significant nibble from state byte to a string message.
     *
     * @return dynamic position message.
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
     * Converts the two least significant bits from event byte to a string message.
     *
     * @return operation event message.
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
     * Converts the bits at position 2 and 3 from event byte to a string message.
     *
     * @return security event message.
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
     * Converts the bits at position 4 and 5 from event byte to a string message.
     *
     * @return communications event message.
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
     * Converts the bits at position 6 and 7 from event byte to a string message.
     *
     * @return clock event message.
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
     * Converts the two least significant bits from diagnosis azimuth byte to a string message.
     *
     * @return diagnosis azimuth message.
     */
    public String diagnosisAz0ToString() {
        StringBuilder diagnosisAz0 = new StringBuilder("Movimiento ");
        int coupleBits0 = 0x3 & diagnosisAZ;
        switch (coupleBits0) {
            case 0x0:
                diagnosisAz0.append("OK");
                break;
            case 0x1:
                diagnosisAz0.append("No mueve con motor ON");
                break;
            case 0x2:
                diagnosisAz0.append("Mueve con motor OFF");
                break;
            case 0x3:
                diagnosisAz0.append("Gira al revés");
                break;
        }
        return diagnosisAz0.toString();
    }

    /**
     * Converts the bits at position 2 and 3 from diagnosisAZ byte to a string message.
     *
     * @return diagnosis azimuth message.
     */
    public String diagnosisAz1ToString() {
        StringBuilder diagnosisAz1 = new StringBuilder("Oscilación ");
        int coupleBits1 = 0xc & diagnosisAZ;
        switch (coupleBits1) {
            case 0x0:
                diagnosisAz1.append("OK");
                break;
            case 0x4:
                diagnosisAz1.append("Fallo oscila");
                break;
            case 0x8:
                diagnosisAz1.append("Fallo servo");
                break;
        }
        return diagnosisAz1.toString();
    }

    /**
     * Converts the bits at position 4 and 5 from diagnosisAZ byte to a string message.
     *
     * @return diagnosis azimuth message.
     */
    public String diagnosisAz2ToString() {
        StringBuilder diagnosisAz2 = new StringBuilder("Posición ");
        int coupleBits2 = 0x30 & diagnosisAZ;
        switch (coupleBits2) {
            case 0x0:
                diagnosisAz2.append("OK");
                break;
            case 0x10:
                diagnosisAz2.append("Posición extrema oeste");
                break;
            case 0x20:
                diagnosisAz2.append("Posición extrema este");
                break;
        }
        return diagnosisAz2.toString();
    }

    /**
     * Converts the bits at position 6 and 7 from diagnosis azimuth byte to a string message.
     *
     * @return diagnosis azimuth message.
     */
    public String diagnosisAz3ToString() {
        StringBuilder diagnosisAz3 = new StringBuilder("Aviso ");
        int coupleBits3 = 0xc0 & diagnosisAZ;
        switch (coupleBits3) {
            case 0x0:
                diagnosisAz3.append("OK");
                break;
            case 0x40:
                diagnosisAz3.append("Cero encontrado");
                break;
            case 0x80:
                diagnosisAz3.append("Banda ampliada");
                break;
        }
        return diagnosisAz3.toString();
    }

    /**
     * Converts the two least significant bits from diagnosis elevation byte to a string message.
     *
     * @return diagnosis elevation message.
     */
    public String diagnosisEl0ToString() {
        StringBuilder diagnosisEl0 = new StringBuilder("Movimiento ");
        int coupleBits0 = 0x3 & diagnosisEL;
        switch (coupleBits0) {
            case 0x0:
                diagnosisEl0.append("OK");
                break;
            case 0x1:
                diagnosisEl0.append("No mueve con motor ON");
                break;
            case 0x2:
                diagnosisEl0.append("Mueve con motor OFF");
                break;
            case 0x3:
                diagnosisEl0.append("Gira al revés");
                break;
        }
        return diagnosisEl0.toString();
    }

    /**
     * Converts the bits at position 2 and 3 from diagnosis elevation byte to a string message.
     *
     * @return diagnosis elevation message.
     */
    public String diagnosisEl1ToString() {
        StringBuilder diagnosisEl1 = new StringBuilder("Oscilación ");
        int coupleBits1 = 0xc & diagnosisEL;
        switch (coupleBits1) {
            case 0x0:
                diagnosisEl1.append("OK");
                break;
            case 0x4:
                diagnosisEl1.append("Fallo oscila");
                break;
            case 0x8:
                diagnosisEl1.append("Fallo servo");
                break;
        }
        return diagnosisEl1.toString();
    }

    /**
     * Converts the bits at position 4 and 5 from diagnosis elevation byte to a string message.
     *
     * @return diagnosis elevation message.
     */
    public String diagnosisEl2ToString() {
        StringBuilder diagnosisEl2 = new StringBuilder("Posición ");
        int coupleBits2 = 0x30 & diagnosisEL;
        switch (coupleBits2) {
            case 0x0:
                diagnosisEl2.append("OK");
                break;
            case 0x10:
                diagnosisEl2.append("Posición extrema oeste");
                break;
            case 0x20:
                diagnosisEl2.append("Posición extrema este");
                break;
        }
        return diagnosisEl2.toString();
    }

    /**
     * Converts the bits at position 6 and 7 from diagnosis elevation byte to a string message.
     *
     * @return diagnosis elevation message.
     */
    public String diagnosisEl3ToString() {
        StringBuilder diagnosisEl3 = new StringBuilder("Aviso ");
        int coupleBits3 = 0xc0 & diagnosisEL;
        switch (coupleBits3) {
            case 0x0:
                diagnosisEl3.append("OK");
                break;
            case 0x40:
                diagnosisEl3.append("Cero encontrado");
                break;
            case 0x80:
                diagnosisEl3.append("Banda ampliada");
                break;
        }
        return diagnosisEl3.toString();
    }

}
