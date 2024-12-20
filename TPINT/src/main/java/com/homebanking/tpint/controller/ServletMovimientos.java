package com.homebanking.tpint.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.homebanking.tpint.entidad.Movimientos;
import com.homebanking.tpint.negocio.MovimientoNeg;
import com.homebanking.tpint.negocioimpl.MovimientoNegImpl;

@WebServlet("/ServletMovimientos")
public class ServletMovimientos extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MovimientoNeg movimientoNeg;

    public void init() throws ServletException {
        super.init();
        movimientoNeg = new MovimientoNegImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("Ver") != null) {
        	
        	// GENERO LISTA
            ArrayList<Movimientos> listaMovimientos = new ArrayList<Movimientos>();
            
           // OBTENGO ID DE LA CUENTA DEL JSP
            int cuentaId = Integer.parseInt(request.getParameter("cuentaId"));
            System.out.println("CUENTA ID (GET): " + cuentaId);
            
            if(cuentaId != 0) {
            	
            
            // OBTENGO MOVIMIENTO POR CLIENTE POR CUENTA ID 
            listaMovimientos = movimientoNeg.ObtenerMovimientosPorCliente(cuentaId);

            if (listaMovimientos != null) {
                request.setAttribute("listaMovimientos", listaMovimientos);
                request.setAttribute("cuentaId", request.getParameter("cuentaId"));
                request.setAttribute("saldo", request.getParameter("saldo"));
                request.setAttribute("cbu", request.getParameter("cbu"));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/Movimientos.jsp");
                dispatcher.forward(request, response);
            } else {
                System.out.println("LISTA MOVIMIENTOS NULA");
            }
          }
       }
    }

    /*
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("parametro") != null) {
            System.out.println("ENTRA A btnFiltrar");

            ArrayList<Movimientos> listaMovimientos = new ArrayList<Movimientos>();

            String cuentaIdParam = request.getParameter("cuentaId");
            if (cuentaIdParam == null) {
                System.out.println("cuentaId es nulo en doPost");
            } else {
                int cuentaId = Integer.parseInt(cuentaIdParam);
                System.out.println("CUENTA ID (POST): " + cuentaId);
                
                
                String parametro = request.getParameter("parametro");
                System.out.println("btnFiltrar " + parametro);

                listaMovimientos = movimientoNeg.ObtenerMovimientosConFiltro(cuentaId, parametro);

                if (listaMovimientos != null) {
                    request.setAttribute("listaMovimientos", listaMovimientos);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/Movimientos.jsp");
                    dispatcher.forward(request, response);
                } else {
                    System.out.println("LISTA MOVIMIENTOS NULA");
                }
            }
        }
    }
    */
}
