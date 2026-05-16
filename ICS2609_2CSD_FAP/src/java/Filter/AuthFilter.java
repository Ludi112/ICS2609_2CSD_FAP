package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AuthFilter
 * Applied globally to /* via web.xml.
 *
 * Responsibilities (Authentication & Security):
 *  - Prevent unauthenticated users from accessing protected pages via URL (FR-AUTH-003, FR-AUTH-005)
 *  - Prevent users from going back to protected pages after logout (FR-AUTH-005, FR-AUTH-007)
 *  - Block Guest from accessing Admin-only pages (NFR-SEC-004)
 *  - Set no-cache headers on every response to prevent back-button access
 */
public class AuthFilter implements Filter 
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException 
    {
        // Nothing to initialise
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)throws IOException, ServletException 
    {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        // ── Always set no-cache headers 
        // This prevents the browser from serving a cached copy of protected pages
        // after the user has logged out (back-button protection).
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String contextPath = request.getContextPath();
        String requestURI  = request.getRequestURI();

        // ── Paths that are always public — never require authentication
        if (isPublicPath(requestURI, contextPath)) 
        {
            chain.doFilter(request, response);
            return;
        }

        // ── Check session (FR-AUTH-003) 
        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("username") != null);

        if (!loggedIn) 
        {
            // Unauthenticated user trying to reach a protected page
            response.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        // ── Role check for Admin-only paths (NFR-SEC-004) 
        String role = (String) session.getAttribute("role");
        if (requestURI.startsWith(contextPath + "/admin/")) 
        {
            if (!"Admin".equalsIgnoreCase(role)) 
            {
                // Guest/Student trying to access Admin pages
                response.sendRedirect(contextPath + "/errors/error403.jsp");
                return;
            }
        }

        // ── All checks passed — continue the request
        chain.doFilter(request, response);
    }

    /**
     * Returns true for paths that must never require authentication.
     * Static resources, the login page, CAPTCHA servlet, and custom error pages.
     */
    private boolean isPublicPath(String uri, String contextPath) 
    {
        // Login page and login servlet
        if (uri.equals(contextPath + "/login.jsp"))          
        {
            return true;
        }
        if (uri.equals(contextPath + "/LoginServlet"))    
        {
            return true;
        }

        // CAPTCHA servlet (must be reachable before login)
        if (uri.equals(contextPath + "/CaptchaServlet"))    
        {
            return true;
        }

        // Custom error pages (must be reachable by unauthenticated users)
        if (uri.startsWith(contextPath + "/errors/"))     
        {
            return true;
        }

        // Root index (redirects to login)
        if (uri.equals(contextPath + "/")|| uri.equals(contextPath + "/index.jsp"))  
        {
            return true;
        }

        // Static resources
        if (uri.endsWith(".css"))   
        {
            return true;
        }
        if (uri.endsWith(".js"))  
        {
            return true;
        }
        if (uri.endsWith(".png")) 
        {
            return true;
        }
        if (uri.endsWith(".jpg"))  
        {
            return true;
        }
        if (uri.endsWith(".jpeg")) 
        {
            return true;
        }
        if (uri.endsWith(".ico"))  
        {
            return true;
        }
        if (uri.endsWith(".gif")) 
        {
            return true;
        }
        if (uri.endsWith(".woff")) 
        {
            return true;
        }
        if (uri.endsWith(".woff2")) 
        {
            return true;
        }
        if (uri.endsWith(".svg")) 
        {
            return true;
        }

        return false;
    }

    @Override
    public void destroy()
    {
        // Nothing to clean up
    }
}

