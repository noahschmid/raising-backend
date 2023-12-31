package ch.raising.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.raising.models.AccountDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Class which handles login logic and jwt token operations
 * 
 * @author Noah Schmid
 * @version 1.0
 */
@Service
public class JwtUtil {
	private final String SECRET_KEY = "vzoY2faTegwQl4aaJ8L1J2FXAxDM8eafbBltW4Hpc6WTfcJKs1MMVq2mTB8NNNm9r51mBQXSkSWdiTVacwRWzo5st0fN2b63"
			+ "urGTiKqHjOy8ZXiz6fuBH8qsUb0dtPsTcHHXVCZ9jvwamZkQ2L22OftBpaIprermmIsGJNzLqcBUYuTp5cYpX4tijPnoWvhu7lXUfcBqMV8O4rRbjn99oEZS"
			+ "n5U6mw2tfyfyL5o1mR7fegDqXvThA5B0PkxshJ5R";
    private final PasswordEncoder encoder;
    
    @Autowired
	public JwtUtil(PasswordEncoder encoder) {
		this.encoder = encoder;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public long extractId(String token) {
		long id = Long.parseUnsignedLong(extractAllClaims(token).get("id").toString());
		return id;
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public boolean extractIsStartup(String token) {
		return (boolean)extractAllClaims(token).get("isStartup");
	}
	
	public boolean extractIsInvestor(String token) {
		return (boolean)extractAllClaims(token).get("isInvestor");
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}

	public String generateToken(AccountDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", userDetails.getId());
		claims.put("username", userDetails.getUsername());
		claims.put("isStartup", userDetails.getStartup());
		claims.put("isInvestor", userDetails.getInvestor());
		return createToken(claims, userDetails.getUsername());
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (encoder.matches(username, userDetails.getUsername()) && !isTokenExpired(token));
	}


	/**
	 * Get token out of request
	 * @param request
	 * @return
	 */
	public String getToken(HttpServletRequest request) throws AuthException {
		final String authHeader = request.getHeader("Authorization");
        
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		
		throw new AuthException();
	}
}