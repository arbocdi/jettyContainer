package kg.megacom.as.jettycontainer.testapp.jsf;

import java.io.Serializable;
import javax.enterprise.inject.Default;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import kg.megacom.as.jettycontainer.testapp.responder.StringResponder;
import lombok.Data;

/**
 *
 * @author root
 */
@ViewScoped
@Named
@Data
public class HelloPage implements Serializable {
    @Inject
    @Default
    private StringResponder sr;
    private String message1 = "Hello, jsf is working";
    private String message2 = "This is message2";
}
