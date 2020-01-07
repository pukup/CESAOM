package psa.cesa.cesaom.model.dao;

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
     * @return the <code>Heliostat</code> address as string
     */
    public String getAddressString() {
        return String.valueOf(address);
    }

    /**
     * Converts the least significant nibble from state byte to a string message
     *
     * @return string
     */
    public String state0ToString() {
        String string = "";
        int nibble0 = 0x0f & state;
        switch (nibble0) {
            case 0x0:
                string.concat("Operación local");
                break;
            case 0x1:
                string.concat("Consiga fija");
                break;
            case 0x2:
                string.concat("Busqueda de ceros");
                break;
            case 0x3:
                string.concat("Fuera de servicio");
                break;
            case 0x4:
                string.concat("Posición de defensa");
                break;
            case 0x5:
                string.concat("Abatimiento normal");
                break;
            case 0x6:
                string.concat("Blanco tierra");
                break;
            case 0x7:
                string.concat("Blanco pasillo 1");
                break;
            case 0x8:
                string.concat("Blanco pasillo 2");
                break;
            case 0x9:
                string.concat("Blanco pasillo 3");
                break;
            case 0xa:
                string.concat("Blanco pasillo 4");
                break;
            case 0xb:
                string.concat("Seguimiento desfasado");
                break;
            case 0xc:
                string.concat("Blanco de emergencia");
                break;
            case 0xd:
                string.concat("Seguimiento normal a caldera");
                break;
            case 0xe:
                string.concat("Foco");
                break;
            case 0xf:
                string.concat("Seguimiento normal al sol");
                break;
        }
        return string;
    }

    /**
     * Converts the most significant nibble from state byte to a string message
     *
     * @return string
     */
    public String state1ToString() {
        String string = "";
        int nibble1 = 0xf0 & state;
        if ((nibble1 & 0x80) == 0x80) {
            string.concat("Aviso error");
        }
        if ((nibble1 & 0x40) == 0x40) {
            string.concat(" Aviso evento");
        }
        if ((nibble1 & 0x20) == 0x20) {
            string.concat(" Consigna alcanzada EL");
        }
        if ((nibble1 & 0x10) == 0x10) {
            string.concat(" Consigna alcanzada AZ");
        }
        return string;
    }

    /**
     * Converts the two least significant bits from event byte to a string message
     *
     * @return string
     */
    public String eventOperationToString() {
        String string = "Operación ";
        int coupleBits0 = 0x3 & event;
        switch (coupleBits0) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x1:
                string.concat("Fuera de servicio");
                break;
            case 0x2:
                string.concat("Heliostato teleconfigurado");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 2 and 3 from event byte to a string message
     *
     * @return string
     */
    public String eventSecurityToString() {
        String string = "Seguridad ";
        int coupleBits1 = 0xc & event;
        switch (coupleBits1) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x4:
                string.concat("Código de cliente erróneo");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 4 and 5 from event byte to a string message
     *
     * @return string
     */
    public String eventComToString() {
        String string = "Comunicaciones ";
        int coupleBits2 = 0x30 & event;
        switch (coupleBits2) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x10:
                System.out.println("Fallo de comunicaciones");
                break;
            case 0x20:
                System.out.println("No acpeta el comando");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 6 and 7 from event byte to a string message
     *
     * @return string
     */
    public String eventCLToString() {
        String string = "Fallo CL  ";
        int coupleBits3 = 0xc0 & event;
        switch (coupleBits3) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x40:
                string.concat("Fallo del micro esclavo");
                break;
            case 0x80:
                string.concat("Fallo batería reloj BQ3287");
                break;
        }
        return string;
    }

    /**
     * Converts the two least significant bits from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz0ToString() {
        String string = "";
        int coupleBits0 = 0x3 & diagnosysAZ;
        string.concat("Movimiento ");
        switch (coupleBits0) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x1:
                string.concat("No mueve con motor ON");
                break;
            case 0x2:
                string.concat("Mueve con motor OFF");
                break;
            case 0x3:
                string.concat("Gira al revés");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 2 and 3 from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz1ToString() {
        String string = "";
        int coupleBits1 = 0xc & diagnosysAZ;
        string.concat("Oscilación ");
        switch (coupleBits1) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x4:
                string.concat("F Oscila");
                break;
            case 0x8:
                string.concat("F Servo");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 4 and 5 from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz2ToString() {
        String string = "";
        int coupleBits2 = 0x30 & diagnosysAZ;
        string.concat("Posición ");
        switch (coupleBits2) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x10:
                string.concat("FCoeste");
                break;
            case 0x20:
                string.concat("FCeste");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 6 and 7 from diagnosysAZ byte to a string message
     *
     * @return
     */
    public String diagnosysAz3ToString() {
        String string = "";
        int coupleBits3 = 0xc0 & diagnosysAZ;
        string.concat("Avisos ");
        switch (coupleBits3) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x40:
                string.concat("Z_OK");
                break;
            case 0x80:
                string.concat("BA");
                break;
        }
        return string;
    }

    /**
     * Converts the two least significant bits from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl0ToString() {
        String string = "";
        int coupleBits0 = 0x3 & diagnosysEL;
        string.concat("Movimiento ");
        switch (coupleBits0) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x1:
                string.concat("No mueve con motor ON");
                break;
            case 0x2:
                string.concat("Mueve con motor OFF");
                break;
            case 0x3:
                string.concat("Gira al revés");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 2 and 3 from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl1ToString() {
        String string = "";
        int coupleBits1 = 0xc & diagnosysEL;
        string.concat("Oscilación ");
        switch (coupleBits1) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x4:
                string.concat("F Oscila");
                break;
            case 0x8:
                string.concat("F Servo");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 4 and 5 from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl2ToString() {
        String string = "";
        int coupleBits2 = 0x30 & diagnosysEL;
        string.concat("Posición ");
        switch (coupleBits2) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x10:
                string.concat("FCoeste");
                break;
            case 0x20:
                string.concat("FCeste");
                break;
        }
        return string;
    }

    /**
     * Converts the bits at position 6 and 7 from diagnosysEL byte to a string message
     *
     * @return
     */
    public String diagnosysEl3ToString() {
        String string = "";
        int coupleBits3 = 0xc0 & diagnosysEL;
        string.concat("Avisos ");
        switch (coupleBits3) {
            case 0x0:
                string.concat("OK");
                break;
            case 0x40:
                string.concat("Z_OK");
                break;
            case 0x80:
                string.concat("BA");
                break;
        }
        return string;
    }
}
