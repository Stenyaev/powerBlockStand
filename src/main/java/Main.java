import can.errorHandler.ErrorHandler;
import can.powerBlock.PowerBlock;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        JoranConfigurator configurator = new JoranConfigurator();
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        configurator.setContext(loggerContext);
//        loggerContext.reset();
//        try {
//            configurator.doConfigure(new File("/home/root/logback.xml"));
//        } catch (JoranException e) {
//            ErrorHandler.logMain(e);
//        }

        int i = 0;
        ArrayList<PowerBlock> powerBlocks = new ArrayList<>();

        for (int j = 1; j < 128; j++) {
            PowerBlock block = new PowerBlock((byte) j);

//            block.sendShutDownDCDC(true);

            block.readModuleStatus();
            Thread.sleep(150);
            System.out.println(block.getModuleStatus());

            if (block.getModuleStatus() != 0) {
                powerBlocks.add(block);
            }

            System.out.println(j);
        }

        powerBlocks.stream().findAny().get().sendShutDownDCDC(false);
        powerBlocks.forEach(System.out::println);
        System.out.println("+++");

        ////////////////////////////////////////////
        //Выполнение нужных команд
        ////////////////////////////////////////////
        powerBlocks.removeAll(powerBlocks);
        powerBlocks.forEach(System.out::println);
        System.out.println("---");






//        while (true) {
//            powerBlocks
//
////            block.readModuleStatus();
////            System.out.println(block.getModuleStatus());
////
////            block.sendVoutReference(250);
////            block.readVout_fast();
////
////            block.sendIoutLimit(14);
////
////            block.readIout_fastest();
////
////            System.out.println("V = " + block.getVout() + "," +
////                    " I = " + block.getIout());
//
//            Thread.sleep(500);
//        }


//        Scanner scanner = new Scanner(System.in);
//
//        while (!scanner.nextLine().equals("0")) {
////            block.sendShutDownDCDC(true);
////            System.out.println("Состояние блока: " + block.getShutDownDCDC());
//            System.out.println("Выберите команду: \n 1) Проверить статус;\n 2) Узнать ток и напряжение;\n 3) Установить ток и напряжение; \n 0) Завершить работу программы");
//
//            switch (scanner.nextLine()) {
//                case "1":
//                    block.sendShutDownDCDC(true);
//                    System.out.println("Состояние блока: " + block.getShutDownDCDC());
//
//                    block.readModuleStatus();
//                    System.out.println(block.getModuleStatus());
//
//                    block.sendShutDownDCDC(false);
//                    System.out.println("Состояние блока: " + block.getShutDownDCDC());
//
//                    break;
//                case "2":
//                    block.sendShutDownDCDC(true);
//                    System.out.println("Состояние блока: " + block.getShutDownDCDC());
//
//                    block.readIout_fastest();
//                    System.out.println(block.getIout());
//
//                    block.readVout_fast();
//                    System.out.println(block.getVout());
//
//                    block.sendShutDownDCDC(false);
//                    System.out.println("Состояние блока: " + block.getShutDownDCDC());
//
//                    break;
//                case "3":
//                    block.sendShutDownDCDC(true);
//                    System.out.println("Состояние блока: " + block.getShutDownDCDC());
//
//                    System.out.println("Установите значение: ");
//
//                    block.sendIoutLimit(scanner.nextFloat());
//                    block.readIout_fastest();
//                    System.out.println(block.getIout());
//
//                    block.sendVoutReference(scanner.nextFloat());
//                    block.readVout_fast();
//                    System.out.println(block.getVout());
//
//                    block.sendShutDownDCDC(false);
//                    System.out.println("Состояние блока: " + block.getShutDownDCDC());
//
//                    break;
//
//                case "0":
//                    block.sendShutDownDCDC(false);
//                    System.out.println("Состояние блока: " + block.getShutDownDCDC());
//
//
//                    break;
//
//            }
//
//
//        }


    }
}
