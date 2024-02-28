package can.errorHandler;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ErrorHandler {
    private static StringWriter sw = new StringWriter();
    private static PrintWriter pw = new PrintWriter(sw);
    public static Logger loggerBlock = (Logger) LoggerFactory.getLogger("POWER_BLOCK");
    public static Logger loggerMain = (Logger) LoggerFactory.getLogger("MAIN");
    public static Logger loggerClientCcs = (Logger) LoggerFactory.getLogger("CCS SEND");
    public static Logger loggerClientChademo = (Logger) LoggerFactory.getLogger("CHADEMO SEND");
    public static Logger loggerClientGbt = (Logger) LoggerFactory.getLogger("GBT SEND");

    public static void logMain(Throwable e) {
        e.printStackTrace(pw);
        loggerMain.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }
    public static void logBlock(String message) {
        loggerBlock.info(message);
    }

    public static void logCcs(Throwable e) {
        e.printStackTrace(pw);
        loggerClientCcs.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }
    public static void logChademo(Throwable e) {
        e.printStackTrace(pw);
        loggerClientCcs.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }
    public static void logGbt(Throwable e) {
        e.printStackTrace(pw);
        loggerClientCcs.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }

}
