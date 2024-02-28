package can.powerBlock;

import java.util.HashMap;

public class PowerBlock {

    public static final byte DLC = 8;
    public static final int HI_MODE = 1;
    public static final int LOW_MODE = 2;

    //Протокол исользуется в идентификаторе, всегда 1
    private static final byte PROTOCOL = 1;

    public static HashMap<Long, String> StatusFlagAndBits = new HashMap<>();
    static {
        StatusFlagAndBits.put(1L, "AC over voltage");
        StatusFlagAndBits.put(1L << 1, "AC under voltage");
        StatusFlagAndBits.put(1L << 2, "Disconnects from AC (AC over voltage shutdown)");
        StatusFlagAndBits.put(1L << 3, "PFC Bus over voltage");
        StatusFlagAndBits.put(1L << 4, "PFC Bus under voltage");
        StatusFlagAndBits.put(1L << 5, "PFC Bus unbalance");
        StatusFlagAndBits.put(1L << 6, "DC output over voltage");
        StatusFlagAndBits.put(1L << 7, "DC over voltage shutdown");
        StatusFlagAndBits.put(1L << 8, "DC output under voltage");
        StatusFlagAndBits.put(1L << 9, "Fan stops operating");
        StatusFlagAndBits.put(1L << 11, "Fan driven circuit damaged");
        StatusFlagAndBits.put(1L << 12, "Over temperature (Ambient temperature)");
        StatusFlagAndBits.put(1L << 13, "Low ambient temperature");
        StatusFlagAndBits.put(1L << 14, "PFC over temperature protection 1");
        StatusFlagAndBits.put(1L << 15, "Output relay failure");
        StatusFlagAndBits.put(1L << 16, "DC over-temperature protection 1");
        StatusFlagAndBits.put(1L << 17, "DC over-temperature protection 2");
        StatusFlagAndBits.put(1L << 18, "Communication failure between PFC and DCDC");
        StatusFlagAndBits.put(1L << 19, "DCDC not get PFC OK signal");
        StatusFlagAndBits.put(1L << 20, "PFC failure");
        StatusFlagAndBits.put(1L << 21, "DCDC failure");
        StatusFlagAndBits.put(1L << 24, "off state of PFC");
        StatusFlagAndBits.put(1L << 25, "Module turn off");
        StatusFlagAndBits.put(1L << 27, "Current loop");
        StatusFlagAndBits.put(1L << 28, "DC output voltage unbalance");
        StatusFlagAndBits.put(1L << 29, "Get same SN among modules");
        StatusFlagAndBits.put(1L << 31, "Bleeder Not Work");
    }
    public enum MessageType {
        SET_DATA((byte) 0),
        SET_DATA_RESPONSE((byte) 1),
        READ_DATA((byte) 2),
        READ_DATA_RESPONSE((byte) 3),
        READ_SERIAL_NUMBER_RESPONSE((byte) 4),
        ALL_SET_DATA((byte) 11),
        ALL_SET_DATA_RESPONSE((byte) 12);

        private final byte code;

        MessageType(byte aCode) {
            this.code = aCode;
        }
        public byte getCode() {
            return code;
        }
    }

    public enum CommandType {
        Vout((byte) 0),
        Iout_slow((byte) 1),
        VoutReference((byte) 2),
        IoutLimit((byte) 3),



        ShutDownDCDC((byte) 4),
        ReadSN((byte) 5),
        ModuleStatus((byte) 8),
        Vab((byte) 20),
        Vbc((byte) 21),
        Vca((byte) 22),
        VfanReference((byte) 26),
        Tin((byte) 30),
        Iout_fastest((byte) 47),
        Iout_fast((byte) 48),
        GroupAddress((byte) 89),
        HiMode_LoMode_Selection((byte) 95),
        HiMode_LoMode_Status((byte) 96),
        Vout_fast((byte) 98),
        TrueHiLo_Status((byte) 101),
        CurrentCapability((byte) 104),
        CurrentAndCapablity((byte) 114);

        private final byte code;

        CommandType(byte aCode) {
            this.code = aCode;
        }
        public byte getCode() {
            return code;
        }
    }

    private final CanBus canBus;

    //Поля идентификатора, индивидуальны для блоков. Исключением является moduleAddress - адрес контроллера
    private byte productionDate;
    private short serialNumberLowerPart;
    private byte monitorAddress;
    private byte moduleAddress;

    private byte groupAddress;
    private float Vout;
    private float Iout;
    private float VoutReference;
    private float IoutLimit;

    private boolean shutDownDCDC;
    private int serialNumber;
    private long moduleStatus;
    private float Vab;
    private float Vbc;
    private float Vca;
    private float VfanReference;
    private float Tin;
    private byte HiMode_LoMode_Status;
    private float CurrentCapability;

    public PowerBlock(byte address) {
        canBus = new CanBus();
        this.moduleAddress = address;
        this.monitorAddress = 1;
        this.productionDate = 0;
        this.serialNumberLowerPart = 0;
        this.groupAddress = 1;
        this.shutDownDCDC = false;
        CanHandler.addBlock(this);
//        CanHandler.checkBlock();
    }



    public void setGroupAddress(byte address) {this.groupAddress = address;}
    public byte getGroupAddress() {return this.groupAddress;}

    public void setModuleAddress(byte address) {this.moduleAddress = address;}
    public byte getModuleAddress() {return this.moduleAddress;}

    public void setMonitorAddress(byte address) {this.monitorAddress = address;}
    public byte getMonitorAddress() {return this.monitorAddress;}

    public void setSerialNumberLowerPart(short serialNumber) {this.serialNumberLowerPart = serialNumber;}
    public short getSerialNumberLowerPart() {return this.serialNumberLowerPart;}

    public void setProductionDate(byte date) {this.productionDate = date;}
    public byte getProductionDate() {return this.productionDate;}

    public void setVout(float v) {this.Vout = v;}
    public float getVout() {return this.Vout;}

    public void setIout(float i) {this.Iout = i;}
    public float getIout() {return this.Iout;}

    public void setVoutReference(float v_ref) {this.VoutReference = v_ref;}
    public float getVoutReference() {return this.VoutReference;}

    public void setIoutLimit(float i_lim) {this.IoutLimit = i_lim;}
    public float getIoutLimit() {return this.IoutLimit;}

    public void setShutDownDCDC(byte state) {this.shutDownDCDC = (state == 0);}
    public boolean getShutDownDCDC() {return this.shutDownDCDC;}

    public void setSerialNumber(int sNumber) {this.serialNumber = sNumber;}
    public int getSerialNumber() {return this.serialNumber;}

    public void setModuleStatus(long status) {this.moduleStatus = status;}
    public long getModuleStatus() {return this.moduleStatus;}

    public void setVab(float vab) {this.Vab = vab;}
    public float getVab() {return this.Vab;}

    public void setVbc(float vbc) {this.Vbc = vbc;}
    public float getVbc() {return this.Vbc;}

    public void setVca(float vca) {this.Vca = vca;}
    public float getVca() {return this.Vca;}

    public void setVfanReference(float v_fan) {this.VfanReference = v_fan;}
    public float getVfanReference() {return this.VfanReference;}

    public void setTin(float tin) {this.Tin = tin;}
    public float getTin() {return this.Tin;}

    public void setHiMode_LoMode_Status(byte mode_status) {this.HiMode_LoMode_Status = mode_status;}
    public float getHiMode_LoMode_Status() {return this.HiMode_LoMode_Status;}

    public void setCurrentCapability(float i_cap) {this.CurrentCapability = i_cap;}
    public float getCurrentCapability() {return this.CurrentCapability;}

    //////////////////////   МЕТОДЫ УСТАНОВКИ И ЧТЕНИЯ ПАРАМЕТРОВ БЛОКОВ
    public void readVout() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Vout);
        sendData(data);
    }
    public void readIout_slow() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Iout_slow);
        sendData(data);
    }
    public void sendVoutReference(float voltage) {
        int mVolt = (int) (voltage*1000);
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.SET_DATA, CommandType.VoutReference, mVolt);
        sendData(data);
    }
    public void readVoutReference() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.VoutReference);
        sendData(data);
    }
    public void sendIoutLimit(float current) {
        int mA = (int) (current*1000);
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.SET_DATA, CommandType.IoutLimit, mA);
        sendData(data);
    }
    public void readIoutLimit() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.IoutLimit);
        sendData(data);
    }
    public void sendShutDownDCDC(boolean state) {
        int stateByte;
        if (state) {
            stateByte = 0;
        } else {
            stateByte = 1;
        }
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.SET_DATA, CommandType.ShutDownDCDC, stateByte);
        sendData(data);
    }
    public void  readSerialNumber() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.ReadSN);
        sendData(data);
    }
    public void readModuleStatus() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.ModuleStatus);
        sendData(data);
    }
    public void  readVab() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Vab);
        sendData(data);
    }
    public void  readVbc() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Vbc);
        sendData(data);
    }
    public void  readVca() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Vca);
        sendData(data);
    }
    public void  readVfanReference() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.VfanReference);
        sendData(data);
    }
    public void  readTin() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Tin);
        sendData(data);
    }
    public void readIout_fastest() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Iout_fastest);
        sendData(data);
    }
    public void readIout_fast() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Iout_fast);
        sendData(data);
    }
    public void sendGroupAddress(int address) {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.SET_DATA, CommandType.GroupAddress,
                address);
        sendData(data);
    }
    public void readGroupAddress() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.GroupAddress);
        sendData(data);
    }
    public void sendHiMode_LoMode(int mode) {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.SET_DATA, CommandType.HiMode_LoMode_Selection,
                mode);
        sendData(data);
    }
    public void readHiMode_LoMode() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.HiMode_LoMode_Status);
        sendData(data);
    }
    public void readVout_fast() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.Vout_fast);
        sendData(data);
    }
    public void readTrueHiLo_Status() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.TrueHiLo_Status);
        sendData(data);
    }
    public void readCurrentCapability() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.CurrentCapability);
        sendData(data);
    }
    public void readCurrentAndCapablity() {
        int id = idBuilder();
        String data = stringBuilder(id, this.groupAddress, MessageType.READ_DATA, CommandType.CurrentAndCapablity);
        sendData(data);
    }

    public void sendAllSetData(byte hiLoModeSelection, boolean onOff, float IoutLimit, float Vbattery,
                               float VoutReference) {
        int id = idBuilder();
        int IoutLim = (int) (IoutLimit*10);
        int Vbat = (int) (Vbattery*10);
        int VoutRef = (int) (VoutReference*10);
        byte stateOnOff;
        if (onOff) {stateOnOff = 0;}
        else {stateOnOff = 1;}
        String data = stringBuilder(id, this.groupAddress, MessageType.ALL_SET_DATA, hiLoModeSelection, stateOnOff,
                IoutLim, Vbat, VoutRef);
        sendData(data);
    }

    //////////////////////   МЕТОДЫ, ИСПОЛЬЗУЕМЫЕ В КЛАССЕ
    private  int idBuilder() {
        return (PROTOCOL << 25) + (monitorAddress << 21) + (this.moduleAddress << 14) + (this.productionDate << 9) +
                (this.serialNumberLowerPart);
    }

    private void sendData(String data) {
        canBus.sendFrame(data);
    }
    private static String stringBuilder(int id, byte groupAddress, MessageType messageType, CommandType commandType) {
        //Компоновка id
        StringBuilder id_hex = new StringBuilder(Integer.toHexString(id));
        while (id_hex.length() < 8) {
            id_hex.insert(0, "0");
        }
        //Компоновка данных
        //Байты 0 - 3
        String[] byteStr = new String[4];
        byteStr[0] = Integer.toHexString((byte) ((groupAddress << 4) + messageType.getCode()));
        byteStr[1] = Integer.toHexString(commandType.getCode());
        byteStr[2] = "00";
        byteStr[3] = "00";
        for (int i = 0; i < byteStr.length; i++) {
            while (byteStr[i].length() < 2) {
                byteStr[i] = "0" + byteStr[i];
            }
        }
        //Байты 4 - 7
        StringBuilder byte4_7 = new StringBuilder("00000000");
        while (byte4_7.length() < 8) {
            byte4_7.insert(0, "0");
        }
        String pdu = byteStr[0] + byteStr[1] + byteStr[2] + byteStr[3] + byte4_7;
        return id_hex.append(Integer.toString(DLC)).append(pdu).toString().toUpperCase();
    }

    private static String stringBuilder(int id, byte groupAddress, MessageType messageType, CommandType commandType,
                                        int data) {
        //Компоновка id
        StringBuilder id_hex = new StringBuilder(Integer.toHexString(id));
        while (id_hex.length() < 8) {
            id_hex.insert(0, "0");
        }
        //Компоновка данных
        //Байты 0 - 3
        String[] byteStr = new String[4];
        byteStr[0] = Integer.toHexString((byte) ((groupAddress << 4) + messageType.getCode()));
        byteStr[1] = Integer.toHexString(commandType.getCode());
        byteStr[2] = "00";
        byteStr[3] = "00";
        for (int i = 0; i < byteStr.length; i++) {
            while (byteStr[i].length() < 2) {
                byteStr[i] = "0" + byteStr[i];
            }
        }
        //Байты 4 - 7
        StringBuilder byte4_7 = new StringBuilder(Integer.toHexString(data));
        while (byte4_7.length() < 8) {
            byte4_7.insert(0, "0");
        }
        String pdu = byteStr[0] + byteStr[1] + byteStr[2] + byteStr[3] + byte4_7;
        return id_hex.append(Integer.toString(DLC)).append(pdu).toString().toUpperCase();
    }
    private static String stringBuilder(int id, byte groupAddress, MessageType messageType, byte hiLoModeSelection,
                                        byte onOff, int IoutLimit, int Vbattery, int VoutReference) {
        //Компоновка id
        StringBuilder id_hex = new StringBuilder(Integer.toHexString(id));
        while (id_hex.length() < 8) {
            id_hex.insert(0, "0");
        }
        //Компоновка данных
        //Байты 0,1
        String[] byteStr = new String[2];
        byteStr[0] = Integer.toHexString((byte) ((groupAddress << 4) + messageType.getCode()));
        byteStr[1] = Integer.toHexString((hiLoModeSelection << 6)+ onOff);
        for (int i = 0; i < byteStr.length; i++) {
            while (byteStr[i].length() < 2) {
                byteStr[i] = "0" + byteStr[i];
            }
        }
        //Байты 2,3
        StringBuilder byte2_3 = new StringBuilder(Integer.toHexString(IoutLimit));
        while (byte2_3.length() < 4) {
            byte2_3.insert(0, "0");
        }
        //Байты 4,5
        StringBuilder byte4_5 = new StringBuilder(Integer.toHexString(Vbattery));
        while (byte4_5.length() < 4) {
            byte4_5.insert(0, "0");
        }
        //Байты 6,7
        StringBuilder byte6_7 = new StringBuilder(Integer.toHexString(VoutReference));
        while (byte6_7.length() < 4) {
            byte6_7.insert(0, "0");
        }
        String pdu = byteStr[0] + byteStr[1] + byte2_3 + byte4_5 + byte6_7;
        return id_hex.append(Integer.toString(DLC)).append(pdu).toString().toUpperCase();
    }
}
