package paquete;

public class Memoria {


	private int[] memoria = new int[4096]; //cada columna tendrá 8 bits (el int puede almacenarlos)
	
	public Memoria() {
	}
	
	public void cargainstruccion(int instruccion,int i) {
		
		memoria[i]=instruccion;
	}
	
	public int getInstruccion(int IP) { //obtengo la instruccion de la celda de memoria q apunta IP	
		
		return memoria[IP];
	}

	public void modificaRAM(int celda,int nuevovalor) {
		this.memoria[celda]=nuevovalor;
	}
	
	public int getValorRAM(int celda) {
		return memoria[celda];
	}
}