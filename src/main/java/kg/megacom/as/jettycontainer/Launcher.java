package kg.megacom.as.jettycontainer;

import lombok.extern.log4j.Log4j;
import net.sf.selibs.utils.misc.UHelper;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;

@Log4j
public class Launcher {

    public static void main(String[] args) throws Exception {
        DOMConfigurator.configure("config/log4j.xml");
        // Optionally remove existing handlers attached to j.u.l root logger
        //SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)
        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        //SLF4JBridgeHandler.install();
        ServerFactory sf = new ServerFactory();
        final Server srv = sf.produce();
        final HikariPoolReader poolReader = new HikariPoolReader();
        poolReader.start();
        JettyUtils.registerResources(poolReader.getDsList(), null);
        srv.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutting down");
                UHelper.callNoArgMethod(srv, "stop");
                poolReader.stop();
            }
        });
        srv.join();
    }
}
