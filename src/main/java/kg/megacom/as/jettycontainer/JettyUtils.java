package kg.megacom.as.jettycontainer;

import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import net.sf.selibs.utils.misc.UHelper;
import org.eclipse.jetty.plus.jndi.Resource;

public class JettyUtils {

    /**
     * jndi = java:comp:/env/resourceName is object = server|webcontext, 
     * otherwise jndi = resourceName
     * @param resources
     * @param scope
     * @throws NamingException
     */
    public static <T> void registerResources(List<CustomResource<T>> resources, Object scope) throws NamingException {
        Context ctx = new InitialContext();
        try {
            for (CustomResource resource : resources) {
                Resource res = new Resource(scope, resource.name, resource.resource);
            }
        } finally {
            UHelper.close(ctx);
        }
    }
}
