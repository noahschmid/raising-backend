package ch.raising.utils;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.raising.models.AccountDetails;
import ch.raising.services.AccountService;

@Component  
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil jwtUtil;
    
    private static Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);
 

    
    /**
     * Check whether request supplies a valid token in the header 
     * @param request the user request
     * @param response the backend response
     * @param chain chain of filters
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain chain) throws ServletException, IOException{
    	long begin = System.currentTimeMillis();
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
        long id = -1;
        boolean isStartup = false;
        boolean isInvestor = false;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
            id = jwtUtil.extractId(token);
            isStartup = jwtUtil.extractIsStartup(token);
            isInvestor = jwtUtil.extractIsInvestor(token);
        }

        if(id != -1 && SecurityContextHolder.getContext().getAuthentication() == null) {
        	AccountDetails userDetails;
			try{
				userDetails = this.accountService.loadUserById(id);
				userDetails.setInvestor(isInvestor);
				userDetails.setStartup(isStartup);
				if(jwtUtil.validateToken(token, userDetails)) {
	                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
	                    new UsernamePasswordAuthenticationToken(userDetails, null, 
	                                                            userDetails.getAuthorities());
	                usernamePasswordAuthenticationToken
	                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                
	                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	                LOGGER.info("Filter Stopwatch: {}ms", System.currentTimeMillis() - begin);
	            }
			} catch (DataAccessException e) {
				throw new ServletException(e.getMessage());
			}
            
        }
        chain.doFilter(request, response);
    }
}