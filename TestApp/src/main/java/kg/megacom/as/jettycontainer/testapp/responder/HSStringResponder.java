package kg.megacom.as.jettycontainer.testapp.responder;

import javax.enterprise.context.RequestScoped;

@HSResponder
@RequestScoped
public class HSStringResponder implements StringResponder{

    @Override
    public String getResponse() {
        return "Hello from Servlet!";
    }
    
}
