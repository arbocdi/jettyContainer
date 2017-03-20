package kg.megacom.as.jettycontainer.async;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.utils.misc.UHelper;

@Slf4j
public class AsyncServletProcessor implements ReadListener, WriteListener, Runnable {

    //config===========
    protected ExecutorService es;
    protected AsyncContext ac;
    protected AsyncAction action;
    //work=============
    protected ByteArrayOutputStream fromClient = new ByteArrayOutputStream();
    protected ByteArrayOutputStream toClient = new ByteArrayOutputStream();
    
    public AsyncServletProcessor(ExecutorService es, AsyncContext ac, AsyncAction action) {
        this.es = es;
        this.ac = ac;
        this.action = action;
    }
    
    @Override
    public void onDataAvailable() throws IOException {
        ServletInputStream input = ac.getRequest().getInputStream();
        byte[] buff = new byte[1024];
        while (input.isReady()) {
            int len = input.read(buff);
            if (len == -1) {
                return;
            }
            fromClient.write(buff, 0, len);
        }
    }
    
    @Override
    public void onAllDataRead() throws IOException {
        this.es.submit(this);
    }
    
    @Override
    public void onError(Throwable thrwbl) {
        log.warn("Cant async process request", thrwbl);
        ((HttpServletResponse) ac.getResponse()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ac.complete();
    }
    
    @Override
    public void onWritePossible() throws IOException {
        ServletOutputStream out = ac.getResponse().getOutputStream();
        if (out.isReady()) {
            out.write(toClient.toByteArray());
            out.flush();
            ac.complete();
        }
    }
    
    protected void writeError(Exception ex) {
        ((HttpServletResponse) ac.getResponse()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        try {
            this.toClient.write(UHelper.getStackTrace(ex).getBytes("UTF-8"));
            ac.getResponse().setContentType("text/plain; charset=UTF-8");
        } catch (Exception exx) {
            log.warn("Cant write exception text to client", exx);
        }
    }
    
    @Override
    public void run() {
        ServletOutputStream out = null;
        try {
            out = ac.getResponse().getOutputStream();
        } catch (Exception ex) {
            log.warn("Cant get output stream,exiting", ex);
            ac.complete();
            return;
        }
        try {
            this.action.doAction(this.fromClient, this.toClient, ac);
        } catch (Exception ex) {
            this.writeError(ex);
        } finally {
            ac.getResponse().setContentLength(this.toClient.size());
            out.setWriteListener(this);
        }
    }
    
}
