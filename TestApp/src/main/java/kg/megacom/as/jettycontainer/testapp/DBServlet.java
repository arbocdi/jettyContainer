package kg.megacom.as.jettycontainer.testapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kg.megacom.as.jettycontainer.testapp.responder.EmfPool1;
import lombok.extern.log4j.Log4j;
import net.sf.selibs.utils.misc.UHelper;

@Log4j
public class DBServlet extends HttpServlet {

    @Inject
    @EmfPool1
    protected EntityManagerFactory emf1;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("EMF1 injected " + emf1);
            EntityManager em = emf1.createEntityManager();
            try {
                em.getTransaction().begin();
                out.println("inside transaction");
                List<User> users = em.createNamedQuery("User.findAll", User.class).getResultList();
                out.println(users);
                out.println("found users = " + users.size());
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                log.warn("cant make db interaction", ex);
            } finally {
                UHelper.close(em);
            }
            Context ctx = null;
            try {
                ctx = new InitialContext();
                out.println(ctx.lookup("jdbc/pool1"));
            } catch (Exception ex) {
                log.warn("error while finding datasources", ex);
            } finally {
                UHelper.close(ctx);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
