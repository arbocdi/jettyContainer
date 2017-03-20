/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kg.megacom.as.jettycontainer.testapp.responder;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import lombok.extern.log4j.Log4j;
import net.sf.selibs.utils.misc.UHelper;

/**
 *
 * @author root
 */
public class JndiProducer {

    protected Context ctx;

    @PostConstruct
    public void init() {
        try {
            ctx = new InitialContext();
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @PreDestroy
    public void close() {
        UHelper.close(ctx);
    }

    @Produces
    @EmfPool1
    @ApplicationScoped
    public EntityManagerFactory createEmf1() throws NamingException {
        return (EntityManagerFactory) ctx.lookup("java:comp/env/jdbc/pool1PU");
    }
}
