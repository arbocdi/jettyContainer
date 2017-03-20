
package kg.megacom.as.jettycontainer.async;

import java.io.ByteArrayOutputStream;
import javax.servlet.AsyncContext;

public interface AsyncAction {
    void doAction(ByteArrayOutputStream fromClient,ByteArrayOutputStream toClient,AsyncContext ac) throws Exception;
}
