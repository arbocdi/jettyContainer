package kg.megacom.as.jettycontainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.utils.service.AbstractService;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Slf4j
public class HikariPoolReader implements AbstractService {

    @Element
    @Setter
    @Getter
    protected File poolConfigDir = new File("pools");
    @Getter
    protected List<CustomResource<HikariDataSource>> dsList = new LinkedList();

    protected void readHikariConfig() {
        for (File file : this.poolConfigDir.listFiles()) {
            HikariConfig hcfg = new HikariConfig(file.getAbsolutePath());
            HikariDataSource ds = new HikariDataSource(hcfg);
            dsList.add(new CustomResource(this.getJndiName(file.getName()), ds));
        }
    }

    protected String getJndiName(String fileName) {
        return "jdbc/" + fileName.split("\\.")[0];
    }

    @Override
    public void start() throws Exception {
        log.info("Starting " + this.getClass().getName());
        //hikariDS launches threads to server pool
        this.readHikariConfig();
    }

    @Override
    public void stop() {
        log.info("Stopping " + this.getClass().getName());
        for (CustomResource<HikariDataSource> ds : this.dsList) {
            ds.resource.close();
            ds.resource.shutdown();
        }
    }

    @Override
    public void join() throws InterruptedException {
    }
}
