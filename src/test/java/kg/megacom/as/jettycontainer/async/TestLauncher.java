package kg.megacom.as.jettycontainer.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import kg.megacom.as.jettycontainer.HikariPoolReader;
import kg.megacom.as.jettycontainer.JettyUtils;
import kg.megacom.as.jettycontainer.ServerFactory;
import lombok.extern.log4j.Log4j;
import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import net.sf.selibs.utils.locator.ImmServiceName;
import net.sf.selibs.utils.locator.ServiceLocator;
import net.sf.selibs.utils.service.AbstractService;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

@Log4j
public class TestLauncher implements AbstractService {

    protected Server server;
    protected ExecutorService es;
    protected HikariPoolReader poolReader;

    private static TestLauncher singleton;

    static {
        try {
            singleton = new TestLauncher();
            singleton.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        singleton.stop();
                        singleton.join();
                    } catch (Exception ex) {
                        System.out.println("Cant properly stop jettyContainer");
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println("Cant start container,exiting");
            ex.printStackTrace();
            System.exit(1);
        }

    }

    private TestLauncher() throws Exception {
        BasicConfigurator.configure();
        Logger.getLogger("org").setLevel(Level.INFO);
        Logger.getLogger("com.zaxxer").setLevel(Level.INFO);
        ServerFactory sf = new ServerFactory();
        this.server = sf.produce();
        ThreadPoolFactory factory = new ThreadPoolFactory();
        factory.minThreads = 10;
        factory.maxThreads = 10;
        factory.queueType = ThreadPoolFactory.QueueType.LINKED;
        this.es = factory.produce();
        ServiceLocator.put(new ImmServiceName("es"), es);
        poolReader = new HikariPoolReader();
    }

    @Override
    public void start() throws Exception {
        poolReader.start();
        JettyUtils.registerResources(poolReader.getDsList(), null);
        this.server.start();
    }

    @Override
    public void stop() {
        try {
            this.server.stop();
            this.es.shutdownNow();
            this.poolReader.stop();
        } catch (Exception ex) {
            System.out.println("Cant stop jetty container");
            ex.printStackTrace();
        }
    }

    @Override
    public void join() throws InterruptedException {
        this.server.join();
        this.es.awaitTermination(1, TimeUnit.DAYS);
        this.poolReader.join();
    }
}
