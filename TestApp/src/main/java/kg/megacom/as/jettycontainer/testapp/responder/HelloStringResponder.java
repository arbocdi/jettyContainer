package kg.megacom.as.jettycontainer.testapp.responder;

import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

@Default
public class HelloStringResponder implements StringResponder,Serializable {

    @Override
    public String getResponse() {
        return "Hello!";
    }
    
}
