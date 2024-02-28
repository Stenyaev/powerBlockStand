package can.powerBlock;

import can.errorHandler.ErrorHandler;

import java.util.ArrayList;

public class CanHandler {

    private static ArrayList<PowerBlock> arrayBlocks = new ArrayList<>();
    public static void addBlock(PowerBlock powerBlock) {
        arrayBlocks.add(powerBlock);
    }

    public static void checkBlock() {
        arrayBlocks.forEach(System.out::println);
    }

    public static byte[][] parseMsg(String str) {
        String[] arrayStr = new String[3];
        byte[][] arrayByte = new  byte[7][];
        if (str.length() == 26) {
            String msg = str.substring(1);
            arrayStr[0] = msg.substring(0, 8);                                                   //id
            arrayStr[1] = msg.substring(8, 9);                                                   //dlc
            arrayStr[2] = msg.substring(9);                                            //data

            int id = Integer.parseInt(arrayStr[0], 16);
            //byte protocol = (byte) ((id & 0x1E000000) >>> 25);
            arrayByte[0] = new byte[] {(byte) ((id & 0x1E00000) >>> 21)};                        //monitor address
            arrayByte[1] = new byte[] {(byte) ((id & 0x1FC000) >>> 14)};                         //module address

            arrayByte[2] = new byte[] {(byte) Integer.parseInt(arrayStr[2].substring(0,1), 16)};     //group address
            arrayByte[3] = new byte[] {(byte) Integer.parseInt(arrayStr[2].substring(1,2), 16)};     //message type
            arrayByte[4] = arrayStr[2].getBytes();                                               //data
            ///arrayByte[5] =  new byte[] {(byte) ((id & 0x3E00) >> 9)};                            //productionDate
            ///arrayByte[6] = new byte[] {(byte) (id & 0x1FF), (byte) ((id & 0x1FF) >> 8)};         //serialNumberLowerPart
        }
        return arrayByte;
    }
    private static void setParameter(PowerBlock block, byte msgType, byte[] data) {
        PowerBlock.MessageType messageType = null;
        PowerBlock.CommandType commandType = null;
        for (PowerBlock.MessageType aType: PowerBlock.MessageType.values()) {
            if (msgType == aType.getCode()) {
                messageType = aType;
                break;
            }
        }
        if (messageType != null) {
            String dataStr = new String(data);
            try {
                switch (messageType) {
                    case SET_DATA_RESPONSE:
                    case READ_DATA_RESPONSE:
                    case READ_SERIAL_NUMBER_RESPONSE:
                        byte comType = (byte) Integer.parseInt(dataStr.substring(2, 4), 16);
                        for (PowerBlock.CommandType aType : PowerBlock.CommandType.values()) {
                            if (comType == aType.getCode()) {
                                commandType = aType;
                                break;
                            }
                        }
                        if (commandType != null) {
                            switch (commandType) {
                                case Vout:
                                case Vout_fast:
                                    block.setVout((float) Integer.parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case Iout_slow:
                                case Iout_fast:
                                case Iout_fastest:
                                    block.setIout((float) Integer.parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case VoutReference:
                                    block.setVoutReference((float) Integer
                                            .parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case IoutLimit:
                                    block.setIoutLimit((float) Integer
                                            .parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case ShutDownDCDC:
                                    block.setShutDownDCDC((byte) Integer.parseInt(dataStr.substring(8), 16));
                                    break;
                                case ModuleStatus:
                                    block.setModuleStatus(Long
                                            .parseLong(dataStr.substring(8), 16));
                                    break;
                                case Vab:
                                    block.setVab((float) Integer.parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case Vbc:
                                    block.setVbc((float) Integer.parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case Vca:
                                    block.setVca((float) Integer.parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case VfanReference:
                                    block.setVfanReference((float) Integer
                                            .parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case Tin:
                                    block.setTin((float)(int) Long.parseLong(dataStr.substring(8), 16) / 1000);
                                    break;
                                case GroupAddress:
                                    block.setGroupAddress((byte) Integer.parseInt(dataStr.substring(8), 16));
                                    break;
                                case HiMode_LoMode_Status:
                                case HiMode_LoMode_Selection:
                                case TrueHiLo_Status:
                                    block.setHiMode_LoMode_Status((byte) Integer
                                            .parseInt(dataStr.substring(8), 16));
                                    break;
                                case CurrentCapability:
                                    block.setCurrentCapability((float) Integer
                                            .parseInt(dataStr.substring(8), 16) / 1000);
                                    break;
                                case CurrentAndCapablity:
                                    break;
                                case ReadSN:
                                    block.setSerialNumber(Integer
                                            .parseInt(dataStr.substring(4), 16));
                                    break;
                            }
                        }
                        break;
                    case ALL_SET_DATA_RESPONSE:
                        System.out.println(dataStr);
                        byte state = (byte) (Integer.parseInt(dataStr.substring(2, 4), 16) & 3);
                        byte mode = (byte) ((Integer.parseInt(dataStr.substring(2, 4), 16) >>> 6) & 3);

                        if ((state == 0) || (state == 2)) {
                            block.setShutDownDCDC((byte) 0);
                        } else {
                            block.setShutDownDCDC((byte) 1);
                        }

                        if (mode == (byte) 2) {
                            block.setHiMode_LoMode_Status((byte) 1);
                        } else if (mode == 3) {
                            block.setHiMode_LoMode_Status((byte) 2);
                        }

                        block.setIoutLimit((float) Integer
                                .parseInt(dataStr.substring(4, 8), 16) / 10);

                        block.setVoutReference((float) Integer
                                .parseInt(dataStr.substring(12), 16) / 10);
                        break;
                }
            } catch (NumberFormatException e) {
                ErrorHandler.loggerBlock.error(e.getMessage());
            }
        }
    }

    public static void handleMsg(String str) {
//        checkBlock();
        byte[][] msg = parseMsg(str);
        for (PowerBlock block: arrayBlocks) {
            try {
                if (msg[1][0] == block.getModuleAddress()) {
                    setParameter(block, msg[3][0], msg[4]);
                }
            } catch (NullPointerException e) {
                System.out.println("Null Pointer Exception");
            }
        }
    }
}
