package kg.megacom.as.jettycontainer.async;

import java.net.URI;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.module.AdvHEchoClientProcessor;
import net.sf.selibs.tcp.nio.module.ClientModule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncServletProcessorIntegratedTest {


    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName(TestLauncher.class.getName());
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void generalTest() throws Exception {
        HMessage request = HMessage.createRequest(HMethods.POST, HVersions.V11,
                new URI("http://127.0.0.1:8484/app/testAsync"), "127.0.0.1:8484","hello".getBytes("UTF-8"));
        request.addHeader(HNames.CONTENT_TYPE, "text/plain; charset=utf-8");
        AdvHEchoClientProcessor cltProcessor = new AdvHEchoClientProcessor(HMethods.POST, "http://127.0.0.1:8484/app/testAsync", "127.0.0.1:8484");
        TCPConfig cfg = new TCPConfig("127.0.0.1", 8484);
        ClientModule cm = ClientModule.generateHttp(cfg, cltProcessor, request);
        cm.setClientCount(500);
        cm.setIterationCount(2);
        cm.setClientMessageQuantity(100);
        cm.start();
        cm.startTest();
        cm.join();
    }

}
