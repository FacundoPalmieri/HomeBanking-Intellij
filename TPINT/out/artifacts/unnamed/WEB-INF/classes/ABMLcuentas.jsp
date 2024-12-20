<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
	<jsp:include page="css"></jsp:include>
</style>
</head>
<body>

<% if(session.getAttribute("tipoUsuario")!=null){%>
	
<div id="General">
	<div class="banner">
		<div class="logo_encabezado_izquierda">
		 	  <img src="img/Grupo 13_encabezado.png" alt="Logo" class="logo_encabezado">
		  	  <h3>ABML Cuentas</h3>
		</div>
	<div class="logo_encabezado_derecha">
	     <%= (String) session.getAttribute("usuario") %>
	    <a href="ServletCerrarSesion" class="logout">
	        <img src="img/logout.png" alt="Logout" class="logo_encabezado">
	    </a>
	</div>

	</div>

 	<div class="button-container">
		 <input type="submit" value="Alta de Cuenta" name="btnCrearCuenta" onclick="window.location.href='CrearCuenta.jsp';" class="botonera">
         <input type="submit" value="Eliminar Cuenta" name="btnEliminarCuenta" onclick="window.location.href='EliminarCuenta.jsp';" class="botonera">
         <input type="submit" value="Modificar Cuenta" name="btnEliminarCuenta" onclick="window.location.href='ModificarCuenta.jsp';" class="botonera">
         <a href="ServletListarCuentas" class="botonera">Listar Cuentas</a>
         
  
	</div>
		 <div style = "display: flex; justify-content: center;" >
 			 <input type="button" value="Volver" name="btnVolver" onclick="window.location.href='UsuarioAdministrador.jsp';">
		</div>
    
 <%}else{%>
 	<div class="fullscreen-gif">
    	<img src="img/No tiene permiso.gif" id="gift-ingreso-prohibido">
    </div>
 <%}%>
 
</div> 
<script src="js/scripts.js"></script>
</body>
</html>