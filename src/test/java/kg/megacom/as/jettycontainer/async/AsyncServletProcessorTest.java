package kg.megacom.as.jettycontainer.async;

import kg.megacom.as.jettycontainer.async.AsyncAction;
import kg.megacom.as.jettycontainer.async.AsyncServletProcessor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author ashevelev
 */
public class AsyncServletProcessorTest {

    AsyncContext ac;
    HttpServletRequest request;
    ServletInputStream in;
    byte[] data;
    AsyncServletProcessor asp;

    HttpServletResponse response;
    TServletOutputStream out;
    
     AsyncAction action;

    public AsyncServletProcessorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        data = "Hello".getBytes("UTF-8");
        ac = Mockito.mock(AsyncContext.class);
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(ac.getRequest()).thenReturn(request);
        in = new TServletInputStream(data);
        Mockito.when(request.getInputStream()).thenReturn(in);
        
        action = Mockito.mock(AsyncAction.class);
        
        asp = new AsyncServletProcessor(null, ac, action);
        
        response = Mockito.mock(HttpServletResponse.class);
        this.out = new TServletOutputStream();
        Mockito.when(response.getOutputStream()).thenReturn(out);
        Mockito.when(ac.getResponse()).thenReturn(response);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of onDataAvailable method, of class AsyncServletProcessor.
     */
    @Test
    public void testOnDataAvailable() throws Exception {
        System.out.println("==========testOnDataAvailable============");
        this.asp.onDataAvailable();
        Assert.assertArrayEquals(data, asp.fromClient.toByteArray());
    }

    /**
     * Test of onWritePossible method, of class AsyncServletProcessor.
     */
    @Test
    public void testOnWritePossible() throws Exception {
        System.out.println("==========testOnWritePossible============");
        this.asp.toClient.write(data);
        asp.onWritePossible();
        Assert.assertArrayEquals(data, out.out.toByteArray());
        Mockito.verify(ac).complete();
    }

    /**
     * Test of run method, of class AsyncServletProcessor.
     */
    @Test
    public void testRun() throws Exception{
        System.out.println("===========testRun=============");
        this.asp.toClient.write(data);
        this.asp.run();
        Mockito.verify(this.response).setContentLength(data.length);
        Assert.assertSame(asp, this.out.wl);
        
    }
    @Test
    public void testRunExc() throws Exception{
        System.out.println("===========testRunExc=============");
        this.asp.toClient.write(data);
        Mockito.doThrow(new Exception("Test error")).when(action)
                .doAction(Mockito.any(ByteArrayOutputStream.class),
                          Mockito.any(ByteArrayOutputStream.class), 
                          Mockito.any(AsyncContext.class));
        this.asp.run();
        this.asp.onWritePossible();
        Mockito.verify(ac).complete();
        Mockito.verify(this.response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String errorResponse=new String(this.out.out.toByteArray(),"UTF-8");
        System.out.println(errorResponse);
        Assert.assertTrue(errorResponse.contains("Test error"));
    }

    public class TServletInputStream extends ServletInputStream {

        protected ByteArrayInputStream bin;

        public TServletInputStream(byte[] data) {
            this.bin = new ByteArrayInputStream(data);
        }

        @Override
        public boolean isFinished() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isReady() {
            return bin.available() > 0;
        }

        @Override
        public void setReadListener(ReadListener rl) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int read() throws IOException {
            return bin.read();
        }

    }
    public class TServletOutputStream extends ServletOutputStream{
        
        public ByteArrayOutputStream out = new ByteArrayOutputStream();
        public WriteListener wl;

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener wl) {
            this.wl = wl;
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
        }
        
        
    }

}
