package br.com.sidineisilva.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.sidineisilva.todolist.user.IUserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * FilterTaskAuth
 */
@Component
public class FilterTaskAuth extends OncePerRequestFilter {
  @Autowired
  private IUserRepository userRepository;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    
    var servletPath = request.getServletPath();
    
    if(servletPath.startsWith("/users")) {
      filterChain.doFilter(request, response);
      return;
    }
    
    // Pegar a autenticação do usuário
    var authorization = request.getHeader("Authorization");
    
   var authEncoded =  authorization.substring("Basic".length()).trim();

    byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
    
    var authString = new String(authDecoded);
    
    String[] authParts = authString.split(":");
    String userName = authParts[0];
    String password = authParts[1];
    
    // Validar usuário
    var user = this.userRepository.findByUsername(userName);
    
    if(user == null) {
      response.sendError(401, "Usuário não encontrado");
      return;
    }

    // Validar senha
    var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
    if(!passwordVerify.verified) {
      response.sendError(401, "Senha inválida");
      return;
    }
    
    // Segue o fluxo
    request.setAttribute("idUser", user.getId());
    filterChain.doFilter(request, response);
  }
}