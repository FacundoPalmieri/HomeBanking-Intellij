package com.homebanking.tpint.datosimpl;
import com.homebanking.tpint.datos.UsuarioDao;
import com.homebanking.tpint.entidad.Direccion;
import com.homebanking.tpint.entidad.Localidad;
import com.homebanking.tpint.entidad.Pais;
import com.homebanking.tpint.entidad.Persona;
import com.homebanking.tpint.entidad.Provincia;
import com.homebanking.tpint.entidad.Usuario;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class UsuarioDaoImpl implements UsuarioDao{
	private Conexion cn;
	
	@Override
	public int validarLogin(String usuario, String contrasenia) {
	    int esCliente = 0;
	    Conexion cn = new Conexion();
	    String query = "SELECT usuario, pass, tipo_usuario_id FROM Usuarios WHERE usuario=? and pass=?";
	    try {
	    	cn.Open();
	        PreparedStatement preparedStatement = cn.prepareStatement(query);
	        preparedStatement.setString(1, usuario);
	        preparedStatement.setString(2, contrasenia);

	        // Usar executeQuery para obtener un ResultSet
	        ResultSet rs = preparedStatement.executeQuery();

	        // Si se encuentra al menos un registro, el usuario existe sin se devuelve un 0
	        if (rs.next()) {
	        	//Si el usuario es de tipo Cliente(2) se devuelve un 2 si es de tipo Admin sera un 1
	        	esCliente = 2;
	        	if(rs.getInt("tipo_usuario_id")==1) {
	            	esCliente=1;
	            }
	            System.out.println(" DAO IMPL - Usuario encontrado: " + rs.getString("usuario") + " " + rs.getString("pass"));
	        } else {
	            System.out.println("DAO IMPL - No se encontro usuario: " );
	        }

	        rs.close();
	        preparedStatement.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        cn.close();
	    }

	    System.out.println("Estado retorno ValidadLogin DAO: " + esCliente);
	    return esCliente;
	}

	
	
	@Override
	public boolean validarDNI(String DNI) {
	    Conexion cn = new Conexion();
	    

	    boolean estado = true;
	    String query = "SELECT persona_dni FROM Usuarios WHERE persona_dni=?";

	    try {
	    	cn.Open();
	    	System.out.println("CONEXION ABIERTA VALIDAR USUARIO");
	    	
	        PreparedStatement preparedStatement = cn.prepareStatement(query);
	        preparedStatement.setString(1, DNI);
	

	        // Usar executeQuery para obtener un ResultSet
	        ResultSet rs = preparedStatement.executeQuery();

	        // Si se encuentra al menos un registro, el usuario existe
	        if (rs.next()) {
	            estado = false;
	     
	        } else {
	       
	        }

	        rs.close();
	        preparedStatement.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        cn.close();
	    }

	    System.out.println("Estado retorno USUARIODAO: " + estado);
	    return estado;
	}

	public boolean agregarCliente(Usuario usuario, Persona persona, Direccion direccion) {
	    boolean estado = false;
	    Conexion cn = null;    
	    String query1 = "INSERT INTO direcciones (calle,numero,piso, departamento, localidad_id) " +
	                    " VALUES (?,?,?,?,?)";

	    String query2 = "INSERT INTO personas (dni, cuil, nombre, apellido, sexo, celular, telefono, Direccion_id, nacionalidad, fecha_nacimiento, email) " +
	                    "VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)";

	    String query3 = "INSERT INTO usuarios (usuario,pass,persona_dni) " +
	                    " VALUES (?,?,?)";

	    try {
	        cn = new Conexion();
	        cn.Open();
	        System.out.println("Estado inicial de Auto-commit: " + cn.getConexion().getAutoCommit());
	        System.out.println("Auto-commit despu�s de setAutoCommit(false): " + cn.getConexion().getAutoCommit());

	        // Query 1: direcci�n
	        PreparedStatement preparedStatement1 = cn.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
	        preparedStatement1.setString(1, direccion.getCalle());
	        preparedStatement1.setInt(2, direccion.getAltura());
	        preparedStatement1.setString(3, direccion.getPiso());
	        preparedStatement1.setString(4, direccion.getDepartamento());
	        preparedStatement1.setInt(5, direccion.getLocalidad_id());

	        try {
	        	cn.setAutoCommit(false);  // Inicia la transacci�n
	            preparedStatement1.executeUpdate();
	            ResultSet generatedKeys = preparedStatement1.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                int direccionId = generatedKeys.getInt(1);
	                persona.setDireccion_id(direccionId);
	                System.out.println("ID de direcci�n generado: " + direccionId);
	            } else {
	                throw new Exception("No se gener� el ID de direcci�n.");
	            }
	        } catch (Exception e) {
	            System.out.println("Error en la consulta 1: " + e.getMessage());
	            e.printStackTrace();
	            if (!cn.getConexion().getAutoCommit()) {
	                cn.rollback();
	                System.out.println("Rollback realizado en consulta 1");
	            }
	            return false;
	        }

	        // Query 2: persona
	        PreparedStatement preparedStatement2 = cn.prepareStatement(query2);
	        preparedStatement2.setString(1, persona.getDni());
	        preparedStatement2.setString(2, persona.getCuil());
	        preparedStatement2.setString(3, persona.getNombre());
	        preparedStatement2.setString(4, persona.getApellido());
	        preparedStatement2.setString(5, persona.getSexo());
	        preparedStatement2.setString(6, persona.getCelular());
	        preparedStatement2.setString(7, persona.getTelefono());
	        preparedStatement2.setInt(8, persona.getDireccion_id());
	        preparedStatement2.setString(9, persona.getNacionalidad());
	        preparedStatement2.setDate(10, java.sql.Date.valueOf(persona.getFechaNacimiento()));
	        preparedStatement2.setString(11, persona.getEmail());

	        try {
	        	cn.setAutoCommit(false);  // Inicia la transacci�n
	            preparedStatement2.executeUpdate();
	            System.out.println("Consulta 2 ejecutada correctamente");
	        } catch (Exception e) {
	            System.out.println("Error en la consulta 2: " + e.getMessage());
	            e.printStackTrace();
	            if (!cn.getConexion().getAutoCommit()) {
	                cn.rollback();
	                System.out.println("Rollback realizado en consulta 2");
	            }
	            return false;
	        }

	        // Query 3: usuario
	        PreparedStatement preparedStatement3 = cn.prepareStatement(query3);
	        preparedStatement3.setString(1, usuario.getUsuario());
	        preparedStatement3.setString(2, usuario.getPass());
	        preparedStatement3.setString(3, usuario.getPersona_dni());

	        try {
	        	cn.setAutoCommit(false);  // Inicia la transacci�n
	            preparedStatement3.executeUpdate();
	            System.out.println("Consulta 3 ejecutada correctamente");
	        } catch (Exception e) {
	            System.out.println("Error en la consulta 3: " + e.getMessage());
	            e.printStackTrace();
	            if (!cn.getConexion().getAutoCommit()) {
	                cn.rollback();
	                System.out.println("Rollback realizado en consulta 3");
	            }
	            return false;
	        }

	        cn.commit();  // Confirma la transacci�n
	        System.out.println("Commit realizado");
	        estado = true;

	    } catch (Exception e) {
	        try {
	            if (cn != null && !cn.getConexion().getAutoCommit()) {
	                cn.rollback();
	                System.out.println("Rollback realizado en catch principal");
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        e.printStackTrace();
	    } finally {
	        if (cn != null) {
	            try {
	                if (!cn.getConexion().getAutoCommit()) {
	                    cn.setAutoCommit(true); 
	                    System.out.println("Auto-commit en finally restablecido a: " + cn.getConexion().getAutoCommit());
	                }
	                cn.close();
	                System.out.println("Conexi�n cerrada en finally");
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    System.out.println("ESTADO DAO AGREGAR USUARIO: " + estado);
	    return estado;
	}



	@Override
	public Usuario ObtenerUsuario(String usuario) {
	    cn = new Conexion();
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;

	    Usuario u = new Usuario();
	    
	    try {
	    	cn.Open();
	    	System.out.println("CONEXION ABIERTA OBTENER USUARIO");
	    	String query = "    SELECT usuarios.usuario, usuarios.persona_dni, usuarios.tipo_usuario_id "
	    		        	+ "	FROM usuarios"
	    		        	+ " INNER JOIN  Personas  ON personas.dni = usuarios.persona_dni"
	    		        	+ " WHERE usuarios.habilitado = 1" 
	    		        	+ " AND usuarios.usuario = ? ";
	    	
	    	
	    	 preparedStatement = cn.prepareStatement(query);
		     preparedStatement.setString(1, usuario);
	    	
		     rs = preparedStatement.executeQuery();
		     System.out.println("QUERY OBTENER CLIENTE DAO: " + preparedStatement);

	      
	        if (rs.next()) {
	            u.setUsuario(rs.getString("usuarios.usuario"));
	            u.setPersona_dni(rs.getString("usuarios.Persona_dni"));
	            u.setTipoUsuarioId(Integer.parseInt(rs.getString("usuarios.tipo_usuario_id")));

	            
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (preparedStatement != null) {
	                preparedStatement.close();
	            }
	            if (rs != null) {
	                rs.close();
	            }
	            if (cn != null) {
	                cn.close();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return u;
	}
	
	

	@Override
	public Persona ObtenerCliente(String usuario) { 
	    cn = new Conexion();
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	
	    Persona p = new Persona(); 
	    try {
	        cn.Open();
	        System.out.println("CONEXION ABIERTA OBTENER CLIENTE");
	    	String query = "SELECT personas.nombre, personas.apellido,personas.dni,personas.cuil,personas.sexo, personas.celular,personas.telefono,personas.direccion_id, personas.nacionalidad,personas.fecha_nacimiento,personas.email"
	    			    + " FROM  personas"
	    			    + " INNER JOIN usuarios ON personas.dni = usuarios.persona_dni"
	    			    + " WHERE usuarios.usuario = ? "
	    			    + " AND tipo_usuario_id=2" ;
	    			
	    			


	        preparedStatement = cn.prepareStatement(query);
	        preparedStatement.setString(1, usuario);
	        
	        rs = preparedStatement.executeQuery();
	        System.out.println("QUERY OBTENER CLIENTE DAO: " + preparedStatement);

	      
	        if (rs.next()) {      
	            p.setNombre(rs.getString("personas.nombre"));
	            p.setApellido(rs.getString("personas.apellido"));
	            p.setDni(rs.getString("personas.dni"));
	            p.setCuil(rs.getString("personas.cuil"));
	            p.setSexo(rs.getString("personas.sexo"));
	            p.setCelular(rs.getString("personas.celular"));
	            p.setTelefono(rs.getString("personas.telefono"));
	            p.setDireccion_id(rs.getInt("personas.direccion_id"));
	            p.setNacionalidad(rs.getString("personas.nacionalidad"));
	            
	            Date sqlDate = rs.getDate("personas.fecha_nacimiento");
	            if (sqlDate != null) {
	                LocalDate localDate = sqlDate.toLocalDate();
	                p.setFechaNacimiento(localDate);
	            }
	            
	            p.setEmail(rs.getString("personas.email"));
	            
	        }
	    } catch (Exception e) {
	    	  System.out.println("Error en la consulta obtener cliente");
	        e.printStackTrace();
	        
	    } finally {
	        cn.close();
	    }
	    return p;
	}
	
	
	@Override
	public Usuario ObtenerUsuarioPorDni(String DNI)  {
	    cn = new Conexion();
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    Usuario u = new Usuario();

	    try {
	        cn.Open();
	        System.out.println("CONEXION ABIERTA OBTENER USUARIO X DNI");
	       
	        String query = "SELECT usuarios.usuario, usuarios.persona_dni, usuarios.Habilitado FROM usuarios WHERE tipo_usuario_id=2 AND usuarios.persona_dni = ? ";
	        System.out.println("Query: " + query);
	        preparedStatement = cn.prepareStatement(query);
	        preparedStatement.setString(1, DNI);
	   
	        
	        rs = preparedStatement.executeQuery();

	        if (rs.next()) {
	            System.out.println("Usuario encontrado: " + rs.getString("usuarios.usuario"));
	            u.setUsuario(rs.getString("usuarios.usuario"));
	            u.setPersona_dni(rs.getString("usuarios.persona_dni"));
	            u.setHabilitado(rs.getInt("usuarios.habilitado"));
	        } else {
	            System.out.println("No se encontr� usuario con DNI: " + DNI);
	        }

	    } catch (Exception e) {
	        System.out.println("ERROR USUARIO X DNI DAO");
	        e.printStackTrace();
	    
	    } finally {
	        try {
	            if (preparedStatement != null) {
	                preparedStatement.close();
	            }
	            if (rs != null) {
	                rs.close();
	            }
	            if (cn != null) {
	                cn.close();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    System.out.println("RETURN U: " + u.getUsuario());
	    return u;
	}


	@Override
	public boolean editarUsuario(Usuario usuario) {
		boolean estado=true;

		cn = new Conexion();
		cn.Open();	

		String query = "UPDATE  usuarios SET pass='"+ usuario.getPass()+"', persona_dni='"+ usuario.getPersona_dni()+"', habilitado='"+ usuario.getHabilitado()+"' WHERE dni='"+usuario.getPersona_dni()+"'";
		try
		 {
			estado=cn.execute(query);
		 }
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cn.close();
		}
		
		return estado;
	}
	
	
	@Override
	public boolean editarContrasena(Usuario usuario) {
		boolean estado=true;

		cn = new Conexion();
		cn.Open();	

		String query = "UPDATE usuarios SET pass = ? WHERE usuario= ?";
		 System.out.println("query EDITAR CONTRASE�A" + usuario.getPass());
		 try {
		        PreparedStatement preparedStatement = cn.prepareStatement(query);
		        preparedStatement.setString(1, usuario.getPass());
		        preparedStatement.setString(2, usuario.getUsuario());
		        
		        estado = preparedStatement.executeUpdate() > 0;
		        
		        preparedStatement.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		        estado = false;
		    } finally {
		        cn.close();
		    }
		    
		    System.out.println("estado : " + estado);
		    return estado;
		
	}
	

	public ArrayList<Usuario> listarUsuarios(){
		ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
		String query="select * from usuarios where tipo_usuario_id=2 "; 
		
		try {
			cn = new Conexion();
		    cn.Open();
		    System.out.println("CONEXION ABIERTA LISTAR USUARIO");
		    ResultSet rs = cn.query(query);
		    
		    while(rs.next()){
		    	Usuario u = new Usuario(); 
		    	u.setId(rs.getInt("usuarios.id"));
		    	u.setPersona_dni(rs.getString("usuarios.persona_dni"));
		    	u.setUsuario(rs.getString("usuarios.usuario"));
		    	u.setHabilitado(rs.getInt("usuarios.habilitado"));
		    	listaUsuarios.add(u);
		    }
		
		}catch (Exception e){
			System.out.println("ERROR EN LISTAR USUARIO DAO");
			e.printStackTrace();
        
		}finally{
			cn.close();
		}
		return listaUsuarios;
	}
	
	
	@Override
	public ArrayList<Persona> listarPersonas() {
		ArrayList<Persona> listaPersonas = new ArrayList<Persona>();
		String query="SELECT personas.id, personas.dni,  personas.nombre, personas.apellido, personas.email "
			    	+ "FROM personas "
			    	+ "INNER JOIN usuarios  ON usuarios.persona_dni = personas.dni "
			    	+ "WHERE usuarios.tipo_usuario_id = 2"; 
		
		 System.out.println("QUERY" + query);
		
		try {
			cn = new Conexion();
		    cn.Open();
		    System.out.println("CONEXION ABIERTA LISTAR PERSONAS");
		    ResultSet rs = cn.query(query);
		    
		    while(rs.next()){
		    	Persona p = new Persona(); 
	      	    p.setId(rs.getInt("personas.id"));
	      		System.out.println("id" + p.getId());
		    	p.setDni(rs.getString("personas.dni"));
		    	p.setNombre(rs.getString("personas.nombre"));
		    	p.setApellido(rs.getString("personas.apellido"));
		    	p.setEmail(rs.getString("personas.email"));
		    	listaPersonas.add(p);
		    }
		
		}catch (Exception e){
			System.out.println("ERROR EN LISTAR PERSONA DAO");
			e.printStackTrace();
        
		}finally{
			cn.close();
		}
		return listaPersonas;
	}
		



	@Override
	public ArrayList<Direccion> listarDirecciones() {
		ArrayList<Direccion> listaDirecciones = new ArrayList<Direccion>();
		String query="SELECT direcciones.id, direcciones.calle, direcciones.numero, direcciones.piso, direcciones.departamento "
				+ "FROM direcciones "
				+ "INNER JOIN personas  ON Personas.Direccion_id = direcciones.id "
				+ "INNER JOIN usuarios  ON usuarios.persona_dni = personas.dni "
				+ "WHERE usuarios.tipo_usuario_id = 2"; 
		
		
		try {
			cn = new Conexion();
		    cn.Open();
		    System.out.println("CONEXION ABIERTA LISTAR DIRECCIONES");
		    ResultSet rs = cn.query(query);
		    
		    while(rs.next()){
		    	Direccion d = new Direccion(); 
	      	    d.setId(rs.getInt("direcciones.id"));
		    	d.setCalle(rs.getString("direcciones.calle"));
		    	d.setAltura(rs.getInt("direcciones.numero"));
		    	d.setPiso(rs.getString("direcciones.piso"));
		    	d.setDepartamento(rs.getString("direcciones.departamento"));
		    	listaDirecciones.add(d);
		    }
		
		}catch (Exception e){
			System.out.println("ERROR EN LISTAR DIRECCIONES DAO");
			e.printStackTrace();
        
		}finally{
			cn.close();
		}
		return listaDirecciones;
	}


	@Override
	public boolean eliminarUsuario(Usuario usuario) {
		boolean estado=false;

	
		String query = "UPDATE  usuarios SET habilitado = ? WHERE persona_DNI = ? ";
	
		try
		 {
				cn = new Conexion();
				cn.Open();	

			    PreparedStatement preparedStatement = cn.prepareStatement(query);
		        preparedStatement.setInt(1, usuario.getHabilitado());
		        preparedStatement.setString(2, usuario.getPersona_dni());
		        
		        int filasAfectadas = preparedStatement.executeUpdate();
		        
		        if (filasAfectadas > 0) {
		            estado = true;
		        }
		 }
		catch(Exception e)
		{
			System.out.println("Error en Eliminar usuario DAO");
			e.printStackTrace();
			
		}
		finally
		{
			cn.close();
		}
		
		return estado;
		
	}



	@Override
	public Direccion ObtenerDireccionCliente(int IDdireccion) {
		cn = new Conexion();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		Direccion d = new Direccion();
	  
		String query = "SELECT direcciones.id, direcciones.calle, direcciones.numero, direcciones.piso, direcciones.departamento, direcciones.localidad_id "
		        	 + "FROM direcciones "
		        	 + "WHERE direcciones.id = ? ";
		       
		  try {
		        cn.Open();
		        System.out.println("CONEXION ABIERTA OBTENER DIRECCION X CLIENTE");
			       
		        System.out.println("Query: " + query);
		        preparedStatement = cn.prepareStatement(query);
		        preparedStatement.setInt(1, IDdireccion);
		   
		        
		        rs = preparedStatement.executeQuery();

		        if (rs.next()) {
		            d.setId(rs.getInt("direcciones.id"));
		            d.setCalle(rs.getString("direcciones.calle"));
		            d.setAltura(rs.getInt("direcciones.numero"));
		            d.setPiso(rs.getString("direcciones.piso"));
		            d.setDepartamento(rs.getString("direcciones.departamento"));
		            d.setLocalidad_id(rs.getInt("direcciones.localidad_id"));
		            
		        
		        } 

		    } catch (Exception e) {
		        System.out.println("ERROR ObtenerDireccionCliente DAO");
		        e.printStackTrace();
		    
		    } finally {
		        try {
		            if (preparedStatement != null) {
		                preparedStatement.close();
		            }
		            if (rs != null) {
		                rs.close();
		            }
		            if (cn != null) {
		                cn.close();
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		   
		    return d;
		}



	@Override
	public Provincia ObtenerProvinciaCliente(int IDprovincia) {
		cn = new Conexion();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		Provincia p = new Provincia();
	  
		String query = "SELECT provincias.id, provincias.nombre "
		        	 + "FROM provincias "
		        	 + "WHERE provincias.id = ? ";
		       
		  try {
		        cn.Open();
		        System.out.println("CONEXION ABIERTA OBTENER ProvinciaCliente");
			       
		        System.out.println("Query: " + query);
		        preparedStatement = cn.prepareStatement(query);
		        preparedStatement.setInt(1, IDprovincia);
		   
		        
		        rs = preparedStatement.executeQuery();

		        if (rs.next()) {
		            p.setId(rs.getInt("provincias.id"));
		            p.setNombre(rs.getString("provincias.nombre"));
		     
		            
		        
		        } 

		    } catch (Exception e) {
		        System.out.println("ERROR OBTENER ProvinciaCliente DAO");
		        e.printStackTrace();
		    
		    } finally {
		        try {
		            if (preparedStatement != null) {
		                preparedStatement.close();
		            }
		            if (rs != null) {
		                rs.close();
		            }
		            if (cn != null) {
		                cn.close();
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		   
		    return p;
		}




	@Override
	public Localidad ObtenerLocalidadCliente(int IDlocalidad) {
		cn = new Conexion();
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		Localidad l = new Localidad();
	  
		String query = "SELECT localidades.id, localidades.nombre "
		        	 + "FROM localidades "
		        	 + "WHERE localidades.id = ? ";
		       
		  try {
		        cn.Open();
		        System.out.println("CONEXION ABIERTA OBTENER LocalidadCliente");
			       
		        System.out.println("Query: " + query);
		        preparedStatement = cn.prepareStatement(query);
		        preparedStatement.setInt(1, IDlocalidad);
		   
		        
		        rs = preparedStatement.executeQuery();

		        if (rs.next()) {
		            l.setId(rs.getInt("localidades.id"));
		            l.setNombre(rs.getString("localidades.nombre"));
    
		        } 

		    } catch (Exception e) {
		        System.out.println("ERROR ObtenerLocalidadesCliente DAO");
		        e.printStackTrace();
		    
		    } finally {
		        try {
		            if (preparedStatement != null) {
		                preparedStatement.close();
		            }
		            if (rs != null) {
		                rs.close();
		            }
		            if (cn != null) {
		                cn.close();
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		   
		    return l;
	}




	public ArrayList<Persona> listarPersonasComposicion(){
		ArrayList<Persona> listaPersonas = new ArrayList<Persona>();
		String query= "SELECT personas.id,personas.dni,personas.nombre,personas.apellido,personas.email,usuarios.id,usuarios.usuario,usuarios.habilitado," 
				+"direcciones.id,direcciones.Calle,direcciones.numero,direcciones.piso,direcciones.departamento"
		    	+" FROM personas"
		    	+" INNER JOIN usuarios ON usuarios.persona_dni=personas.dni"
		    	+" INNER JOIN direcciones ON Personas.Direccion_id=direcciones.id"
		    	+" WHERE usuarios.tipo_usuario_id = 2";
		
		try {
			cn = new Conexion();
		    cn.Open();
		    System.out.println("CONEXION ABIERTA LISTAR PERSONAS");
		    System.out.println("QUERY" + query);
		    ResultSet rs = cn.query(query);
		    while(rs.next()){
		    	Persona p = new Persona(); 
	      	    p.setId(rs.getInt("personas.id"));
	      		System.out.println("id" + p.getId());
		    	p.setDni(rs.getString("personas.dni"));
		    	p.setNombre(rs.getString("personas.nombre"));
		    	p.setApellido(rs.getString("personas.apellido"));
		    	p.setEmail(rs.getString("personas.email"));
		    	Usuario u = new Usuario(); 
		    	u.setId(rs.getInt("usuarios.id"));
		    	u.setUsuario(rs.getString("usuarios.usuario"));
		    	u.setHabilitado(rs.getInt("usuarios.habilitado"));
		    	Direccion d = new Direccion(); 
	      	    d.setId(rs.getInt("direcciones.id"));
		    	d.setCalle(rs.getString("direcciones.calle"));
		    	d.setAltura(rs.getInt("direcciones.numero"));
		    	d.setPiso(rs.getString("direcciones.piso"));
		    	d.setDepartamento(rs.getString("direcciones.departamento"));
		    	
		    	//Se agregan los objetos Usuario y Direccion a Persona
		    	p.setUsuario(u);
		    	p.setDireccion(d);
		    	listaPersonas.add(p);
		    }
		}
		catch(Exception e){
			System.out.println("ERROR EN LISTAR PERSONA DAO");
			e.printStackTrace();
		}
		finally {
			cn.close();
		}
		return listaPersonas;
	}
	

	public Persona ObtenerPersonaCompleta(String usuario){
		cn = new Conexion();
		ResultSet rs = null;
		PreparedStatement preparedStatement = null;
		Persona p = new Persona();
		String query= "SELECT personas.id,personas.dni,personas.cuil,personas.nombre,personas.apellido,personas.sexo, personas.celular, personas.telefono, personas.direccion_id, personas.nacionalidad, personas.fecha_nacimiento, personas.email, "
				     +"direcciones.id, direcciones.calle, direcciones.numero, direcciones.piso, direcciones.departamento, direcciones.localidad_id,usuarios.id, usuarios.usuario, usuarios.habilitado, localidades.id, localidades.Nombre,provincias.id, provincias.nombre,paises.nombre "
		    	     +" FROM personas"
		    	     +" INNER JOIN usuarios ON usuarios.persona_dni=personas.dni"
		         	 +" INNER JOIN direcciones ON Personas.Direccion_id=direcciones.id"
		    	     +" INNER JOIN localidades ON direcciones.Localidad_id=localidades.id"
		         	 +" INNER JOIN provincias ON localidades.Provincia_id=provincias.id"
		    	     +" INNER JOIN paises ON provincias.Pais_id=paises.id"
		    	     +" WHERE usuarios.usuario = ?";
		
		try {
			cn.Open();
		    System.out.println("CONEXION ABIERTA OBTENER PERSONA COMPLETA");
		    System.out.println("QUERY" + query);
		    preparedStatement = cn.prepareStatement(query);
		    preparedStatement.setString(1, usuario);
      	    rs = preparedStatement.executeQuery();
		    while(rs.next()){
	      	    p.setId(rs.getInt("personas.id"));
		    	p.setDni(rs.getString("personas.dni"));
		    	p.setCuil(rs.getString("personas.cuil"));
		    	p.setNombre(rs.getString("personas.nombre"));
		    	p.setApellido(rs.getString("personas.apellido"));
		    	p.setCelular(rs.getString("celular"));
	            p.setTelefono(rs.getString("telefono"));
		    	p.setEmail(rs.getString("personas.email"));
		    	p.setSexo(rs.getString("personas.sexo"));
		    	Date sqlDate = rs.getDate("personas.fecha_nacimiento");
	            if (sqlDate != null) {
	                LocalDate localDate = sqlDate.toLocalDate();
	                p.setFechaNacimiento(localDate);
	            }
	            p.setNacionalidad(rs.getString("personas.nacionalidad"));
		    	Usuario u = new Usuario(); 
		    	u.setId(rs.getInt("usuarios.id"));
		    	//u.setPersona_dni(rs.getString("usuarios.persona_dni"));
		    	u.setUsuario(rs.getString("usuarios.usuario"));
		    	u.setHabilitado(rs.getInt("usuarios.habilitado"));
		    	
		    	Pais ps = new Pais();
		    	ps.setNombre(rs.getString("paises.nombre"));
		    	
		    	//Se agrega el objeto Pais a Provincia
		    	Provincia pv = new Provincia();
		    	pv.setId(rs.getInt("provincias.id"));
		    	pv.setNombre(rs.getString("provincias.nombre"));
		    	pv.setPais(ps);
		    	
		    	//Se agrega el objeto Provincia a Localidad
		    	Localidad l = new Localidad();
		    	l.setId(rs.getInt("localidades.id"));
		    	l.setNombre(rs.getString("localidades.nombre"));
		    	l.setProvincia(pv);
		    	
		    	//Se agrega el objeto Localidad a Direccion
		    	Direccion d = new Direccion(); 
	      	    d.setId(rs.getInt("direcciones.id"));
		    	d.setCalle(rs.getString("direcciones.calle"));
		    	d.setAltura(rs.getInt("direcciones.numero"));
		    	d.setPiso(rs.getString("direcciones.piso"));
		    	d.setDepartamento(rs.getString("direcciones.departamento"));
		    	d.setLocalidad(l);
		    	
		    	//Se agregan los objetos Usuario y Direccion a Persona
		    	p.setUsuario(u);
		    	p.setDireccion(d);
		    }
		}
		catch(Exception e){
			System.out.println("ERROR EN OBTENER PERSONA COMPLETA");
			e.printStackTrace();
		}
		finally {
			cn.close();
		}
		return p;
	}
	
	
	//Este metodo devuelve todos los usuarios, esten habilitados o inhabilitados
	public Usuario obtenerUsuarioEstado1o2(String usuario) {
	    cn = new Conexion();
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    Usuario u = new Usuario();
	    try {
	    	cn.Open();
	    	System.out.println("CONEXION ABIERTA OBTENER USUARIO");
	    	String query = "    SELECT usuarios.usuario, usuarios.persona_dni, usuarios.tipo_usuario_id, usuarios.habilitado "
	    		        	+ "	FROM usuarios"
	    		        	+ " INNER JOIN  Personas  ON personas.dni = usuarios.persona_dni"
	    		        	+ " WHERE usuarios.usuario = ? ";
	    		        	
	    	preparedStatement = cn.prepareStatement(query);
		    preparedStatement.setString(1, usuario);
		    rs = preparedStatement.executeQuery();
		    System.out.println("QUERY OBTENER CLIENTE DAO: " + preparedStatement);
	        
		    if (rs.next()){
	            u.setUsuario(rs.getString("usuarios.usuario"));
	            u.setPersona_dni(rs.getString("usuarios.Persona_dni"));
	            u.setHabilitado(rs.getInt("usuarios.habilitado"));
	            u.setTipoUsuarioId(Integer.parseInt(rs.getString("usuarios.tipo_usuario_id"))); 
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (preparedStatement != null) {
	                preparedStatement.close();
	            }
	            if (rs != null) {
	                rs.close();
	            }
	            if (cn != null) {
	                cn.close();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return u;
	}



	@Override
	public Persona obtenerClientePorDNI(int dni) {
		Conexion cn = new Conexion ();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Persona persona = new Persona();
		
		String query = "SELECT nombre, apellido, dni "
				     + "FROM personas "
				     + "WHERE dni = ? ";
		
		try {
			cn.Open();
			System.out.println("CONEXION ABIRTA obtenerClientePorDNI");
			
			ps = cn.prepareStatement(query);
			ps.setInt(1, dni);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				persona.setNombre(rs.getString("nombre"));
				persona.setApellido(rs.getString("apellido"));
				persona.setDni(rs.getString("dni"));
			}
			
			
		} catch (Exception e) {
			System.out.println("ERROR obtenerClientePorDNI");
			e.printStackTrace();
		}
		finally {
			try {
				cn.close();
			} catch (Exception e2) {
				System.out.println("ERROR CERRAR CONEXION obtenerClientePorDNI");
				e2.printStackTrace();
			}
			try {
				ps.close();
			} catch (Exception e2) {
				System.out.println("ERROR CERRAR ps obtenerClientePorDNI");
				e2.printStackTrace();
			}
			try {
				rs.close();
			} catch (Exception e2) {
				System.out.println("ERROR CERRAR rs obtenerClientePorDNI");
				e2.printStackTrace();
			}
			
		}
		
		return persona;
	}



	@Override
	public Persona obtenerPersonaCompleta(String dni) {
		Conexion cn = new Conexion();
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    Persona p = null;
	    
	    try {
	        cn.Open();
	        System.out.println(" CONEXION ABIERTA obtenerPersonaCompleta");

	        // Primero, obtenemos la informaci�n de la persona bas�ndonos en el DNI
	        String querySelect = "SELECT personas.id,personas.dni,personas.cuil,personas.nombre,personas.apellido,personas.sexo, personas.celular, personas.telefono, personas.direccion_id, personas.nacionalidad, personas.fecha_nacimiento, personas.email, "
				     +"direcciones.id, direcciones.calle, direcciones.numero, direcciones.piso, direcciones.departamento, direcciones.localidad_id,usuarios.id, usuarios.usuario, usuarios.pass,localidades.id, localidades.Nombre,provincias.nombre,paises.id, paises.nombre, "
				     + "provincias.id, provincias.nombre "
		    	     +" FROM personas"
		    	     +" INNER JOIN usuarios ON usuarios.persona_dni=personas.dni"
		         	 +" INNER JOIN direcciones ON Personas.Direccion_id=direcciones.id"
		    	     +" INNER JOIN localidades ON direcciones.Localidad_id=localidades.id"
		         	 +" INNER JOIN provincias ON localidades.Provincia_id=provincias.id"
		    	     +" INNER JOIN paises ON provincias.Pais_id=paises.id"
		    	     +" WHERE personas.dni = ? AND usuarios.habilitado = 1 ";

	        preparedStatement = cn.prepareStatement(querySelect);
	        preparedStatement.setString(1, dni);
	        rs = preparedStatement.executeQuery();

	        if (rs.next()) {
	            // Crear la instancia de Persona
	            p = new Persona();
	            p.setId(rs.getInt("personas.id"));
	            p.setDni(rs.getString("dni"));
	            p.setCuil(rs.getString("cuil"));
	            p.setNombre(rs.getString("nombre"));
	            p.setApellido(rs.getString("apellido"));
	            p.setCelular(rs.getString("celular"));
	            p.setTelefono(rs.getString("telefono"));
	            p.setSexo(rs.getString("sexo"));
	            p.setEmail(rs.getString("email"));
	            Date sqlDate = rs.getDate("fecha_nacimiento");
	            if (sqlDate != null) {
	                p.setFechaNacimiento(sqlDate.toLocalDate());
	            }
	            p.setNacionalidad(rs.getString("nacionalidad"));
	            
	            //Usuario
	            Usuario u = new Usuario(); 
		    	u.setUsuario(rs.getString("usuarios.usuario"));
		    	u.setPass(rs.getString("usuarios.pass"));

	            
	            // Crear la instancia de Pais
	            Pais ps = new Pais();
	            ps.setId(rs.getInt("paises.id"));
	            ps.setNombre(rs.getString("paises.nombre"));
	            
	            // Crear la instancia de Provincia
	            Provincia pv = new Provincia();
	            pv.setId(rs.getInt("provincias.id"));
	            pv.setNombre(rs.getString("provincias.nombre"));
	            pv.setPais(ps);
	            
	            // Crear la instancia de Localidad
	            Localidad l = new Localidad();
	            l.setId(rs.getInt("localidades.id"));
	            l.setNombre(rs.getString("localidades.Nombre"));
	            l.setProvincia(pv);
	            
	            // Crear la instancia de Direccion
	            Direccion d = new Direccion();
	            d.setId(rs.getInt("direcciones.id"));
	            d.setCalle(rs.getString("calle"));
	            d.setAltura(rs.getInt("numero"));
	            d.setPiso(rs.getString("piso"));
	            d.setDepartamento(rs.getString("departamento"));
	            d.setLocalidad(l);

		    	p.setUsuario(u);
		    	p.setDireccion(d);

	        }
	    } catch (Exception e) {
	        System.out.println("ERROR AL OBTENER PERSONA COMPLETA");
	        e.printStackTrace();
	    } finally {
	        // Cerrar recursos
	        try {
	            if (rs != null) rs.close();
	            if (preparedStatement != null) preparedStatement.close();
	            cn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return p;
	}



	@Override
	public boolean actualizarPersonaCompleta(Persona persona) {
		Conexion cn = new Conexion();
	    PreparedStatement preparedStatement = null;
	    boolean actualizado = false;

	    try {
	        cn.Open();
	        System.out.println("ACTUALIZAR CLIENTE COMPLETO");

	        // Actualizar la informaci�n en la tabla 'personas'
	        String queryUpdatePersona = "UPDATE personas SET cuil = ?, nombre = ?, apellido = ?, sexo = ?, celular = ?, telefono = ?, nacionalidad = ?, fecha_nacimiento = ?, email = ? WHERE dni = ?";
	        preparedStatement = cn.prepareStatement(queryUpdatePersona);
	        preparedStatement.setString(1, persona.getCuil());
	        preparedStatement.setString(2, persona.getNombre());
	        preparedStatement.setString(3, persona.getApellido());
	        preparedStatement.setString(4, persona.getSexo());
	        preparedStatement.setString(5, persona.getCelular());
	        preparedStatement.setString(6, persona.getTelefono());
	        preparedStatement.setString(7, persona.getNacionalidad());
	        preparedStatement.setDate(8, java.sql.Date.valueOf(persona.getFechaNacimiento()));
	        preparedStatement.setString(9, persona.getEmail());
	        preparedStatement.setString(10, persona.getDni());
	        preparedStatement.executeUpdate();
	        preparedStatement.close();

	        // Actualizar la informaci�n en la tabla 'usuarios'
	        String queryUpdateUsuario = "UPDATE usuarios SET pass = ?  WHERE persona_dni = ?";
	        preparedStatement = cn.prepareStatement(queryUpdateUsuario);
	        preparedStatement.setString(1, persona.getUsuario().getPass());
	        preparedStatement.setString(2, persona.getDni());
	        preparedStatement.executeUpdate();
	        preparedStatement.close();

	        // Actualizar la informaci�n en la tabla 'direcciones'
	        String queryUpdateDireccion = "UPDATE direcciones SET calle = ?, numero = ?, piso = ?, departamento = ?, localidad_id = ? WHERE id = ?";
	        preparedStatement = cn.prepareStatement(queryUpdateDireccion);
	        preparedStatement.setString(1, persona.getDireccion().getCalle());
	        preparedStatement.setInt(2, persona.getDireccion().getAltura());
	        preparedStatement.setString(3, persona.getDireccion().getPiso());
	        preparedStatement.setString(4, persona.getDireccion().getDepartamento());
	        preparedStatement.setInt(5, persona.getDireccion().getLocalidad().getId());
	        preparedStatement.setInt(6, persona.getDireccion().getId());
	        preparedStatement.executeUpdate();
	        preparedStatement.close();

	        actualizado = true;
	    } catch (Exception e) {
	        System.out.println("ERROR AL ACTUALIZAR PERSONA COMPLETA");
	        e.printStackTrace();
	    } finally {
	        // Cerrar recursos
	        try {
	            if (preparedStatement != null) preparedStatement.close();
	            cn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    return actualizado;
	}



	@Override
	public int validarMail(String email) {
		Conexion cn = new Conexion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int estado = 1;
		
		String query = "Select email FROM personas where email = ? ";
		
		try {
			cn.Open();
			System.out.println("CONEXION ABIERTA validarMail ");
			
			ps = cn.prepareStatement(query);
			ps.setString(1, email);
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				estado = 0;
				
			}

		} catch (Exception e) {
			System.out.println("ERROR EN validarMail ");
			e.printStackTrace();
		}finally {
			
			 try {
				cn.close();
			} catch (Exception e2) {
				System.out.println("ERROR al cerrar CN  validarMail ");
				e2.printStackTrace();
			}
			 try {
				ps.close();
			} catch (Exception e2) {
				System.out.println("ERROR al cerrar PS  validarMail ");
				e2.printStackTrace();
			}
			 try {
				rs.close();
			} catch (Exception e2) {
				System.out.println("ERROR al cerrar PS  validarMail ");
				e2.printStackTrace();
			}
		 
		}
		
		return estado;
		
	}
	
	
	@Override
	public boolean validarUsuario(String usuario) {
	    Conexion cn = new Conexion();
	    

	    boolean estado = true;
	    String query = "SELECT usuario FROM Usuarios WHERE usuario=?";

	    try {
	    	cn.Open();
	    	System.out.println("CONEXION ABIERTA VALIDAR USUARIO");
	    	
	        PreparedStatement preparedStatement = cn.prepareStatement(query);
	        preparedStatement.setString(1, usuario);
	

	        // Usar executeQuery para obtener un ResultSet
	        ResultSet rs = preparedStatement.executeQuery();

	        // Si se encuentra al menos un registro, el usuario existe
	        if (rs.next()) {
	            estado = false;
	     
	        } else {
	       
	        }

	        rs.close();
	        preparedStatement.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        cn.close();
	    }

	    System.out.println("Estado retorno USUARIODAO: " + estado);
	    return estado;
	}

}