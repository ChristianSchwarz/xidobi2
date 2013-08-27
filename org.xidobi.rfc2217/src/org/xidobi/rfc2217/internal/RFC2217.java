package org.xidobi.rfc2217.internal;
/**
 * RFC 2217 constants.
 */
@SuppressWarnings("javadoc")
public final class RFC2217 {

    // COM-PORT-OPTION telnet option
    public static final int COM_PORT_OPTION = 44;

    /** COM-PORT-OPTION commands*/
    public static final int SIGNATURE_REQ = 0;
	public static final int SET_BAUDRATE_REQ = 1;
    public static final int SET_DATASIZE_REQ = 2;
    public static final int SET_PARITY_REQ = 3;
    public static final int SET_STOPSIZE_REQ = 4;
    public static final int SET_CONTROL_REQ = 5;
    public static final int PURGE_DATA_REQ = 12;
    
    public static final int SIGNATURE_RESP = 100;
    public static final int SET_BAUDRATE_RESP = 101;
    public static final int SET_DATASIZE_RESP = 102;
    public static final int SET_PARITY_RESP = 103;
    public static final int SET_STOPSIZE_RESP = 104;
    public static final int SET_CONTROL_RESP = 105;
    public static final int PURGE_DATA_RESP = 12;
    
    private RFC2217() {}
}