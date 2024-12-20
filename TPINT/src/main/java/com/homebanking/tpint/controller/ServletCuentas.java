package com.homebanking.tpint.controller;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.homebanking.tpint.entidad.Cuenta;
import com.homebanking.tpint.entidad.Persona;
import com.homebanking.tpint.entidad.Usuario;
import com.homebanking.tpint.negocio.CuentaNeg;
import com.homebanking.tpint.negocio.MovimientoNeg;
import com.homebanking.tpint.negocio.UsuarioNeg;
import com.homebanking.tpint.negocioimpl.CuentaNegImpl;
import com.homebanking.tpint.negocioimpl.MovimientoNegImpl;
import com.homebanking.tpint.negocioimpl.UsuarioNegImpl;


@WebServlet("/ServletCuentas")
public class ServletCuentas extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	CuentaNeg cuentaNeg = new CuentaNegImpl();
	UsuarioNeg usuarioNeg = new UsuarioNegImpl();
	
	MovimientoNeg movimientoNeg = new MovimientoNegImpl();
	
       
   
    public ServletCuentas() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 //Listar cuentas
		 if (request.getParameter("Param") != null) {
		    HttpSession session = request.getSession();
		    String Usuario = (String) session.getAttribute("usuario");
		    System.out.println("SESION USUARIO: " + Usuario);
		    String DNI = new String();
		    Usuario usuario = new Usuario();
		    
		    usuario = usuarioNeg.obtenerUsuario(Usuario);
		    DNI = usuario.getPersona_dni();
		    System.out.println("DNI  " + DNI);
		    ArrayList<Cuenta> listaCuentas = cuentaNeg.obtenerCuentasPorDNI(DNI);
		    System.out.println();
		    
		    
		    request.setAttribute("listaCuentas", listaCuentas);
		    
    		RequestDispatcher dispatcher = request.getRequestDispatcher("/CuentasCliente.jsp");
    		dispatcher.forward(request, response);
    
   
		 }
		 
		 
		 if(request.getParameter("btnBuscarCuentas")!= null) {
		  String dni = null;
		  Persona persona = new Persona();
		  ArrayList <Cuenta> listaCuentas = new   ArrayList <Cuenta>();
		  
		  dni = request.getParameter("DNICliente");
		  listaCuentas = cuentaNeg.obtenerCuentasPorDNI(dni);
		  
		  
		  if(listaCuentas != null) {
			  
			  int dniInt = Integer.parseInt(dni);
			  persona = usuarioNeg.obtenerClientePorDNI(dniInt); 
			  
			  request.setAttribute("listaCuentas", listaCuentas);
			  request.setAttribute("persona", persona);
			 
			  RequestDispatcher dispatcher = request.getRequestDispatcher("/EliminarCuenta.jsp");
	          dispatcher.forward(request, response); 
			    
			  
		  }
			 
	   }
		 
		 


	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// BUSCAR CLIENTE
		
		if (request.getParameter("btnBuscarClienteCrearCuenta") != null) {
			
        	Usuario usuario = new Usuario();
        	Persona persona = new Persona();
        	
        	String DNI = new String();
        	
        	DNI = request.getParameter("dniCliente"); 
        	usuario = usuarioNeg.obtenerUsuarioPorDNI(DNI);
        	 
        	if(usuario.getHabilitado() == 1) {
        		
        	
	        	System.out.println("USUARIO HABILITADO" + usuario.getHabilitado());
	        	persona = usuarioNeg.ObtenerCliente(usuario.getUsuario());
	        	
	        	request.setAttribute("usuario", usuario.getUsuario());
	            request.setAttribute("nombre", persona.getNombre());
	            request.setAttribute("apellido", persona.getApellido());
	           
	            RequestDispatcher dispatcher = request.getRequestDispatcher("/CrearCuenta.jsp");
	            dispatcher.forward(request, response);
        	}else {
        		request.setAttribute("Mensaje","Cliente inexistente");  
	            RequestDispatcher dispatcher = request.getRequestDispatcher("/CrearCuenta.jsp");
	            dispatcher.forward(request, response);
        		
        	}
 
        } 
		
		
		//VALIDAR CLIENTE Y CREAR CUENTA
		if(request.getParameter("btnCrearCuenta") != null){
			String DNI = new String();
	
			DNI = request.getParameter("dniCliente");
	
			//ValidarCliente
			Usuario usuario = new Usuario();
			usuario = usuarioNeg.obtenerUsuarioPorDNI(DNI);
			
		
			
			if(usuario.getHabilitado() == 1) {
							
				int CantidadCuenta = cuentaNeg.ValidarCantidad(DNI);
			
			
				if (CantidadCuenta < 3) {
					int tipoCuenta = 0;
					int nCuenta = 0;
					int estadoCrearCuenta = 0;
					int estadoCrearMovimiento = 0;
					
					String tipoCuentaStr = request.getParameter("tipoCuenta");
					tipoCuenta = Integer.parseInt(tipoCuentaStr);
					
					estadoCrearCuenta = cuentaNeg.CrearCuenta(DNI, tipoCuenta);
					System.out.println("Estado Crear Cuenta " + estadoCrearCuenta);
					
					nCuenta = cuentaNeg.buscarNCuenta(DNI);
					System.out.println("Numero cuenta" + nCuenta);
					estadoCrearMovimiento = movimientoNeg.CrearMovimiento(nCuenta,"Saldo Inicial",10000.00,1);
					
				
						
					if(estadoCrearCuenta == 1 && estadoCrearMovimiento == 1) {
						 request.setAttribute("Mensaje", "La cuenta ha sido creada");
					     RequestDispatcher dispatcher = request.getRequestDispatcher("/CrearCuenta.jsp");
						 dispatcher.forward(request, response);
						
					}else {
						 request.setAttribute("Mensaje", "La cuenta NO pudo ser creada");
					     RequestDispatcher dispatcher = request.getRequestDispatcher("/CrearCuenta.jsp");
						 dispatcher.forward(request, response);
					}
				
		   
				} else { // No permitir agregar m�s cuentas
					request.setAttribute("Mensaje", "El cliente ha alcanzado el limite de cuentas.");
				    RequestDispatcher dispatcher = request.getRequestDispatcher("/CrearCuenta.jsp");
				    dispatcher.forward(request, response);
				}
				
		 }else { // NO existe cliente 
				request.setAttribute("Mensaje", "Cliente inexistente.");
			    RequestDispatcher dispatcher = request.getRequestDispatcher("/CrearCuenta.jsp");
			    dispatcher.forward(request, response);
		 }
			
	}
		
		
		 
		
		
		
		
		if(request.getParameter("confirmacion")!= null){
		  String cuentaYdni = request.getParameter("cuenta");
		  String[] partes = cuentaYdni.split("-");
		  System.out.println("cuentaYdni " + cuentaYdni);
		  
		  int nCuenta = Integer.parseInt(partes[0]);
		  String dni = partes[1];
		  
		  if(nCuenta != 0 && dni != null) {
			int estado = 0;
			Cuenta cuenta = cuentaNeg.obtenerSaldo(nCuenta);
			
				if(cuenta.getSaldo()!= 0) {
				
			
				estado = cuentaNeg.setearEstadoPorCuenta(dni, 0, nCuenta );
			 
			 	if(estado != 0) {
				    request.setAttribute("Mensaje", "La cuenta ha sido eliminada");
				    RequestDispatcher dispatcher = request.getRequestDispatcher("/EliminarCuenta.jsp");
				    dispatcher.forward(request, response);
				 
			 	}else {
				    request.setAttribute("Mensaje", "No se ha podido eliminar la cuenta");
				    RequestDispatcher dispatcher = request.getRequestDispatcher("/EliminarCuenta.jsp");
				    dispatcher.forward(request, response);
				 }
			 
			}else {
				request.setAttribute("Mensaje", "No se puede eliminar una cuenta con saldo");
			    RequestDispatcher dispatcher = request.getRequestDispatcher("/EliminarCuenta.jsp");
			    dispatcher.forward(request, response);
			}
			  
			  
		  }else {
			    request.setAttribute("Mensaje", "Ups! Ha ocurrido un error inesperado");
			    RequestDispatcher dispatcher = request.getRequestDispatcher("/EliminarCuenta.jsp");
			    dispatcher.forward(request, response);
		  }
	
		}
	
	}

}