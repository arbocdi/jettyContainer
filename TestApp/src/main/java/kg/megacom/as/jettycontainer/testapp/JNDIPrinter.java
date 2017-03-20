package kg.megacom.as.jettycontainer.testapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j;
import net.sf.selibs.utils.misc.UHelper;

/**
 *
 * @author root
 */
@Log4j
public class JNDIPrinter extends HttpServlet {

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
            InitialContext ctx = null;
            try {
                ctx = new InitialContext();
                StringBuilder sb = new StringBuilder();
                this.printContext(ctx, sb, new LinkedList());
                out.print(sb.toString());
            } catch (Exception ex) {
                log.warn("Cant print jndi objects", ex);
            } finally {
                UHelper.close(ctx);
            }
        }
    }

    protected void printContext(Context ctx, StringBuilder sb, List<String> prefix) throws NamingException {
        NamingEnumeration<Binding> bindings = ctx.listBindings("");
        while (bindings.hasMore()) {
            Binding binding = bindings.next();
            sb.append(JNDIPrinter.prefixToString(prefix));
            sb.append(binding.getName());
            sb.append(" = ");
            sb.append(binding.getClassName());
            sb.append("\t");
            sb.append(binding.isRelative());
            if (binding.getObject() instanceof Context) {
                sb.append("\t");
                sb.append("subcontext");
                sb.append("\r\n");
                prefix.add(binding.getName());
                this.printContext((Context) binding.getObject(), sb,prefix);
                prefix.remove(prefix.size()-1);
            } else {
                sb.append("\r\n");
            }
        }
    }

    public static String prefixToString(List<String> prefix) {
        StringBuilder sb = new StringBuilder();
        for (String str : prefix) {
            sb.append(str);
            sb.append(".");
        }
        return sb.toString();
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
