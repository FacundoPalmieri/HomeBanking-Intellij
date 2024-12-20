package com.homebanking.tpint.negocioimpl;

import java.util.ArrayList;
import com.homebanking.tpint.datos.CuentaDao;
import com.homebanking.tpint.datosimpl.CuentaDaoImpl;
import com.homebanking.tpint.entidad.Cuenta;
import com.homebanking.tpint.negocio.CuentaNeg;

public class CuentaNegImpl implements CuentaNeg {
	
	 private CuentaDao cuentaDao;
	
	 public CuentaNegImpl() {
	        cuentaDao = new CuentaDaoImpl();
	    }

	@Override
	public int ValidarCantidad(String DNI) {
		return  cuentaDao.ValidarCantidad(DNI);
	}

	@Override
	public int CrearCuenta(String DNI, int TipoCuenta) {
		return cuentaDao.CrearCuenta(DNI, TipoCuenta);
		
	}


	public int buscarNCuenta(String DNI) {
		return cuentaDao.ultimaCuentaCreada(DNI);
	};

	@Override
	public ArrayList<Cuenta> obtenerCuentasPorDNI(String DNI) {
	
		return cuentaDao.obtenerCuentasPorDNI(DNI);
	}

	@Override
	public int modificarSaldo(int nCuenta, float monto) {
		return cuentaDao.modificarSaldo(nCuenta, monto);
	}

	@Override
	public Cuenta obtenerSaldo(int nCuenta) {
		return cuentaDao.obtenerSaldo(nCuenta);
	}

	@Override
	public Cuenta obtenerCuentaporCBU(String cbu) {
		return cuentaDao.obtenerCuentaporCBU(cbu);
	}

	@Override
	public int setearEstadoCuenta(String dni, int estado) {
		return cuentaDao.setearEstadoCuenta(dni, estado);
	}

	@Override
	public int setearEstadoPorCuenta(String dni, int estado, int nCuenta) {
		return cuentaDao.setearEstadoPorCuenta(dni, estado, nCuenta);
	}

	@Override
	public ArrayList<Cuenta> listarTodasLAsCuentas() {
		ArrayList<Cuenta> lista = null;
		lista = cuentaDao.listarTodasLAsCuentas();
		return lista;
	}

	@Override
	public boolean modificarCuenta(int nCuenta, String Dni, int nuevoTipoCuenta) {
		return cuentaDao.modificarCuenta(nCuenta, Dni, nuevoTipoCuenta);
	}
	
}

			
			
