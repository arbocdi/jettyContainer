package kg.megacom.as.jettycontainer.async;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.selibs.utils.locator.ImmServiceName;
import net.sf.selibs.utils.locator.ServiceLocator;

public class TestAsyncServlet extends HttpServlet {

    protected ExecutorService es;
    
    public void init(){
        this.es = (ExecutorService) ServiceLocator.get(new ImmServiceName("es"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext ctx = req.startAsync();
        ctx.setTimeout(60000);
        AsyncAction action = new AsyncAction() {

            @Override
            public void doAction(ByteArrayOutputStream fromClient, ByteArrayOutputStream toClient, AsyncContext ac) throws Exception {
                toClient.write(fromClient.toByteArray());
            }
        };
        AsyncServletProcessor asp = new AsyncServletProcessor(es, ctx, action);
        req.getInputStream().setReadListener(asp);
    }

}
