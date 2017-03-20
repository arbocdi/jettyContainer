package kg.megacom.as.jettycontainer;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class ServerFactory {

    @Element
    @Setter
    @Getter
    protected File cfgDir=new File("jettyConfig");
    @Setter
    @Getter
    @ElementList
    protected List<String> loadOrger = new LinkedList();

    public ServerFactory() {
        //server module
        //loadOrger.add("jetty.xml");
        //http module
        loadOrger.add("jetty-http.xml");
        //jmx
        loadOrger.add("jetty-jmx.xml");
        //servlet
        //security
        //webapp
        //deploy
        loadOrger.add("jetty-deploy.xml");
        //jndi
        //plus
        loadOrger.add("jetty-plus.xml");
        //annotations
        loadOrger.add("jetty-annotations.xml");
        //jsp
        //cdi
        loadOrger.add("jetty-cdi.xml");
    }

    public Server produce() throws Exception {
        XmlConfiguration configuration = new XmlConfiguration(new FileInputStream(this.getServerConfig()));
        Server srv = (Server) configuration.configure();
        for (String fileName : this.loadOrger) {
            Map<String, Object> idMap = configuration.getIdMap();
            File configFile = this.getConfigFile(fileName);
            configuration = new XmlConfiguration(new FileInputStream(configFile));
            configuration.getIdMap().putAll(idMap);
            configuration.configure(srv);
        }
        return srv;
    }

    protected File getServerConfig() {
        return new File(this.cfgDir, "jetty.xml");
    }

    protected File getConfigFile(String fileName) {
        return new File(this.cfgDir, fileName);
    }
}
