package paquete;

import java.io.IOException;
import java.util.Scanner;

public class ALU {
	
	private static boolean parametrob = false;
	private static boolean parametroc = false;
	private static boolean parametrod = false;
	
	private final int mask0 = 0xFFFFFFFF;
	private final int maskf = 0x0000000F;
	
	

	public static boolean getParametrob() {
		return parametrob;
	}

	public static boolean getParametroc() {
		return parametroc;
	}

	public static boolean getParametrod() {
		return parametrod;
	}

	public void setParametroB(boolean paramB) {
        parametrob=paramB;
    }
	
    public void setParametroC(boolean paramC) {
        parametroc=paramC;
    }
    
    public void setParametroD(boolean paramD) {
        parametrod=paramD;
    }
		
	public void mov(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){ //parametros en decimal

		int b = valor2(memoria,reg,topB,vopB), copia = vopA, accesoA = copia>>4;
		int maskAccRegistro = accesoRegistro(accesoA);
		int nuevovalor,numreg;
		if (topA == 1) { //Registro
			numreg=vopA & maskf;
			if (accesoA == 2) //modifico valor registro (caso 3byte)		
				nuevovalor=(reg.getReg(numreg)&~maskAccRegistro)| (b<<8 & maskAccRegistro);
			else 
				nuevovalor=((reg.getReg(vopA & maskf)& ~maskAccRegistro))| (b & maskAccRegistro);
			reg.modificaReg(numreg, nuevovalor);
		}
		else{
			memoria.modificaRAM(vopA + reg.getDS(),b);
		}
		
	}

	public void add(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf)| ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			if (accesoA == 2) b = b<<8;
			resultado = valor + b;
			reg.modificaReg(vopA & maskf,(((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0));
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS()) + b;
			memoria.modificaRAM(vopA + reg.getDS(),resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void sub(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA,accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			if (accesoA == 2) b = b<<8;
			resultado = valor - b;
			reg.modificaReg(vopA & maskf, (((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0));
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS())- b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void mul(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA,accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			if (accesoA == 2) b = b<<8;
			resultado = valor * b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);

		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS()) * b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void div(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA,accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (b!=0) {	
			if (topA == 1) {  	//Registro
				if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
					valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
				else 
					valor = reg.getReg(vopA & maskf) & maskAccRegistro;
				if (accesoA == 2) b = b<<8;
				reg.setRegistro("AC",valor % b);
				resultado = valor / b;
				reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
			}
			else{
				reg.setRegistro("AC",memoria.getValorRAM(vopA + reg.getDS()) % b);
				resultado = memoria.getValorRAM(vopA + reg.getDS()) / b;
				memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
			}
			this.seteaCC(reg, resultado);			
		}
	}
	

	public void swap(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int copiaA = vopA,copiaB = vopB,accesoA = copiaA>>4, maskAccRegistroA = accesoRegistro(accesoA),aux,accesoB = copiaB>>4,maskAccRegistroB = accesoRegistro(accesoB);
		if (topA == 1) {  	//Registro
				if (topB == 1){
					aux= (reg.getReg(vopA & maskf) & maskAccRegistroA);
					if (accesoB == 2)
						reg.modificaReg(vopA & maskf, ((reg.getReg(vopB & maskf) & maskAccRegistroB)>>8 & maskAccRegistroA));
					else {
						reg.modificaReg(vopA & maskf, (reg.getReg(vopB & maskf) & maskAccRegistroB) & maskAccRegistroA);
						reg.modificaReg(vopB & maskf, aux & maskAccRegistroB);
					}

				}
				else {
					aux= memoria.getValorRAM(vopB + reg.getDS());
					memoria.modificaRAM(vopB + reg.getDS(), reg.getReg(vopA & maskf) & maskAccRegistroA);
					reg.modificaReg(vopA & maskf, aux);
				}
			}
			else{
				if (topB == 1){	
					aux=reg.getReg(vopB & maskf) & maskAccRegistroB;
					reg.modificaReg(vopB & maskf, memoria.getValorRAM(vopA + reg.getDS()));
					memoria.modificaRAM( vopA + reg.getDS(),aux);
				}
				else {
					aux=memoria.getValorRAM(vopB + reg.getDS());
					memoria.modificaRAM(vopB+ reg.getDS(), memoria.getValorRAM(vopA+ reg.getDS())); 
					memoria.modificaRAM(vopA+ reg.getDS(), aux);

				}
			}
	}
	
	public void cmp(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor - b;
		}
		else
			if (topA == 2)	// directo
				resultado = memoria.getValorRAM(vopA + reg.getDS()) - b;
			else
				resultado = vopA - b;
		this.seteaCC(reg, resultado);
	}
	
	public void and(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor & b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			resultado =memoria.getValorRAM(vopA + reg.getDS()) & b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void or(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor | b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			resultado=memoria.getValorRAM(vopA + reg.getDS())|b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void xor(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor ^ b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS())^ b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void shl(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor << b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS())<< b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void shr(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor >> b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro) & mask0));
		}
		else{
			resultado=memoria.getValorRAM(vopA + reg.getDS()) >> b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void sys (Memoria memoria,Registros reg,int vopA) throws InterruptedException, IOException {
		String cad=null;
		char w;
		boolean prompt = true, saltoLinea = true, caracter = false;
		int a,comienzo, cantidad,salida,cont,cod,auxInt;
		String aux;
		Scanner leer = new Scanner(System.in);
		comienzo = reg.getEDX();
		cantidad = reg.getCX();
		if ((vopA & 0xF) == 0x1) {  //lectura
			if (reg.getAX()>>11 == 1) 
				prompt = false;
			if (((reg.getAX()>>8) & 0X00000003) == 1) {
				saltoLinea = false;	
			}
			switch (reg.getAX() &  maskf ) {
					case 8 : cad = "%X "; break;
					case 4 : cad = "%o "; break;
					case 1 : cad = "%d "; break;
			}
			if (!saltoLinea) {
				if (prompt)
					System.out.format("[%04d]",comienzo);
				aux = leer.nextLine();
				cont = 0;
				for (a = comienzo;a<(comienzo + cantidad);a++) {    //pasar octal o hexa
					memoria.modificaRAM(a+reg.getDS(),aux.charAt(cont));
					cont++;
				}
			}	
			else {
				for (a = comienzo;a<(comienzo+cantidad);a++) {		
					if (prompt)
						System.out.format("[%04d]",a);
					auxInt = leer.nextInt();
					memoria.modificaRAM(a+reg.getDS(), auxInt);
					
				}
			}
		}
		else { 			//escritura
			if ((vopA & 0xF) == 0x2) {
				if ((reg.getAX() & 0x00000800) == 0x00000800)
					prompt = false;
				if (((reg.getAX()>>8) & 0X00000100) == 0X00000100)
					saltoLinea = false;
				for (a = comienzo;a<(comienzo+cantidad);a++) {
					if (prompt)
						System.out.format("[%04d] ",a);		
					if (((reg.getAX() & 0x0000010) ==  0X00000010)){
						salida = (char)memoria.getValorRAM(a +reg.getDS())& 0X000000FF;
						if (((salida>=0) && (salida<=31)) || (salida ==127)) 
							salida =(char)46;
						else
							salida=(char)memoria.getValorRAM(a + reg.getDS());
						System.out.format("'%c ",salida);
					}
					salida=memoria.getValorRAM(a + reg.getDS());
					if (((reg.getAX()& 0x0000008) ==  0X00000008))
						System.out.format("H%X ",salida);
					if (((reg.getAX()& 0x0000004) ==  0X00000004))  
						System.out.format("@%o ",salida);
					if (((reg.getAX()& 0x0000001) ==  0X00000001)) 
						System.out.format("#%d ",salida);;		
					if (saltoLinea)
						System.out.format("\n");
				
				}
				
			}
			else {    					//breakpoint
				if ((vopA & 0xF) == 0XF) {
					if (getParametrob()) {
						System.out.format("[%04d] cmd: ",reg.getReg(5));
						aux = leer.nextLine();
						cod = codEntradaCmd(aux);
						if ( cod == 1)
							MaquinaVirtual.setP(false);
						else
							if (cod == 2)
								MaquinaVirtual.setP(true);
							else {
								if (cod == 3) {
									int i = Integer.parseInt(aux);
									
									System.out.format("[%04d] %08X  %d \n",i,memoria.getValorRAM(i),memoria.getValorRAM(i));
								}
								else {  
									String op1 = primeraDireccion(aux),op2 = segundaDireccion(aux);
									int i = Integer.parseInt(op1),j = Integer.parseInt(op2);
									for (int t=i;t<=j;t++) 
										System.out.format("[%04d] %08X  %d \n",t,memoria.getValorRAM(t),memoria.getValorRAM(t));
								}
							}
						}
					}
					if (getParametroc() && (vopA >> 28) != 1) {    
						try{
							new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
						}
						catch (Exception e) {
							
						}
					}
					
					if (getParametrod()) {
						int auxiliar,start,end,ip = reg.getReg(5);
						if (ip>=5) {
							start = ip - 5;
							end = ip + 5;
						}
						else {
							start = 0;
							end = 9;
						}
						System.out.print("\nCODIGO: \n");
						for (auxiliar = start;auxiliar<end;auxiliar++) {
							if (auxiliar == ip)
								System.out.print(">");
							else
								System.out.print(" ");
							System.out.format ("[%04d]: %08X  %d: ",auxiliar,memoria.getValorRAM(auxiliar),(auxiliar-start+1));
							muestraInstruccion(memoria.getValorRAM(auxiliar));
							System.out.print("     ");
							muestraOperandos(memoria.getValorRAM(auxiliar));
							System.out.print("\n");
						}
						System.out.print("\nREGISTROS: \n");
						reg.getReg(0);
						System.out.format("DS  = %8X |",reg.getReg(0));
						//System.out.format("    = %8X |",reg.getReg(1));
						//System.out.format("    = %8X |",reg.getReg(2));
						//System.out.format("    = %8X |\n",reg.getReg(3));
						//System.out.format("    = %8X |",reg.getReg(4));
						System.out.format("IP  = %8X |",reg.getReg(5));
						//System.out.format("    = %8X |",reg.getReg(6));
						//System.out.format("    = %8X |\n",reg.getReg(7));
						System.out.format("CC  = %8X |",reg.getReg(8));
						System.out.format("AC  = %8X |",reg.getReg(9));
						System.out.format("EAX = %8X |",reg.getReg(10));
						System.out.format("EBX = %8X |\n",reg.getReg(11));
						System.out.format("ECX = %8X |",reg.getReg(12));
						System.out.format("EDX = %8X |",reg.getReg(13));
						System.out.format("EEX = %8X |",reg.getReg(14));
						System.out.format("EFX = %8X |\n",reg.getReg(15));
					}
				}
			}
		}
		
	
	
	public void jmp (Memoria memoria,Registros reg,int topA,int vopA) {
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (topA == 1 ) {
			if ( aRegistroA == 2) 
				valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
			else
				valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
		}
		else {
			if (topA == 2 )    //directo 
				valor = memoria.getValorRAM(vopA);
			else
				valor = vopA;
		}
		reg.setIP(valor);
	}
	
	public void jz (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.ceroCC() == 1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jp (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 0) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jn (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jnn (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 0 || reg.ceroCC()==1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jnz (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.ceroCC() == 0) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}	
	
	public void jnp (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 1 || reg.ceroCC() == 1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}

	public void ldh (Memoria memoria,Registros reg,int topA,int vopA) {
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo
					valor = memoria.getValorRAM(vopA);         //SOLO VOPA ?????
				else
					valor = vopA;
			}
			reg.setAC(((valor & 0X0000FFFF)<<16)|((reg.getAC() & 0X0000FFFF)));
	}

	public void ldl (Memoria memoria,Registros reg,int topA,int vopA) {
		int valor;
		int aux = vopA;
		int aRegistroA = (aux>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor =memoria.getValorRAM(vopA + reg.getDS());

				else
					valor = vopA;
			}
			reg.setAC((valor & 0X0000FFFF)|(reg.getAC() & 0XFFFF0000));
	}
	
	public void not (Memoria memoria,Registros reg,int topA,int vopA) {
		int aux = vopA;
		int aRegistroA = (aux>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
			if (topA == 1 ) {
				if ( aRegistroA == 2)
					reg.modificaReg(vopA & maskf, ~((reg.getReg(vopA & maskf) & maskAccRegistro)>>8));
				else
					reg.modificaReg(vopA & maskf, ~(reg.getReg(vopA & maskf) & maskAccRegistro));
			}
			else  
				memoria.modificaRAM(vopA + reg.getDS(),~ memoria.getValorRAM(vopA + reg.getDS()));
			
	}
	
	public void stop() {
		System.exit(0);
	}
	
	
public void ejecutaInstruccion(int a, Memoria memoria, Registros registros) throws InterruptedException, IOException {
	int vopA,vopB,topA,topB;
	if ((a>>28 & maskf)!= maskf){
		vopA = (a>>12) & 0x00000fff;
		vopB = a & 0x00000fff;
		topA = (a & 0X0C000000)>>26;
		topB = (a & 0X03000000)>>24;  
        switch (a>>28 & maskf){
            case 0: mov(memoria,registros,topA, topB, vopA, vopB); break;
            case 1: add(memoria,registros,topA, topB, vopA, vopB);break;
            case 2: sub(memoria,registros,topA, topB, vopA, vopB);break;
            case 3: swap(memoria,registros,topA, topB, vopA, vopB);break;
            case 4: mul(memoria,registros,topA, topB, vopA, vopB);break;
            case 5: div(memoria,registros,topA, topB, vopA, vopB);break;
            case 6: cmp(memoria,registros,topA, topB, vopA, vopB);break;
            case 7: shl(memoria,registros,topA, topB, vopA, vopB);break;
            case 8: shr(memoria,registros,topA, topB, vopA, vopB);break;
            case 9:and(memoria,registros,topA, topB, vopA, vopB);break;
            case 10:or(memoria,registros,topA, topB, vopA, vopB);break;
            case 11:xor(memoria,registros,topA, topB, vopA, vopB);break;
        }
    }
    else{
        if (((a>>24 & maskf) == maskf) && ((a>>28 & maskf) == maskf)) {
        	stop();
        }
        else{
      	   topA = (a & 0X00C00000)>>22;
      	   vopA = a & 0X0000ffff;
            switch (a>>24 & maskf){
                case 0:sys(memoria,registros,vopA);break;
                case 1:jmp(memoria,registros,topA,vopA);break;
                case 2:jz(memoria,registros,topA,vopA);break;
                case 3:jp(memoria,registros,topA,vopA);break;
                case 4:jn(memoria,registros,topA,vopA);break;
                case 5:jnz(memoria,registros,topA,vopA);break;
                case 6:jnp(memoria,registros,topA,vopA);break;
                case 7:jnn(memoria,registros,topA,vopA);break;
                case 8:ldl(memoria,registros,topA,vopA);break;
                case 9:ldh(memoria,registros,topA,vopA);break;
                case 11:not(memoria,registros,topA,vopA);break;
            }   
        }
    }
}

	public int valor2 (Memoria memoria,Registros reg, int topB,int vopB) {
		int valor;
		int copiaB = vopB;
		if (topB == 1){      //Registro
			int aRegistroB = (copiaB>>4);
			int mask1 = accesoRegistro(aRegistroB);
			if (aRegistroB == 2) {
				valor = (reg.getReg(vopB & maskf) & mask1)>>8;
			}
			else
				valor = reg.getReg(vopB & maskf) & mask1;
			
			if (esNegativo(valor,mask1)) {
				valor = valor | ~mask1;
			}
		}
		else
			if (topB == 2){ // Directo
				valor=memoria.getValorRAM(vopB + reg.getDS()); //obtengo direccion de memoria
			}
			else{
				if (copiaB>>11 == 1) {
					valor = vopB | 0xFFFFF000;
				}
				else
				valor = vopB;
			}
		return valor;
	}

	
	int  accesoRegistro (int a) {
		int mask=0;
		switch (a) {
		case 0: mask = 0xFFFFFFFF;break;
		case 1: mask = 0x000000FF;break;
		case 2: mask = 0x0000FF00;break;
		case 3: mask = 0x0000FFFF;break;
		}
		return mask; 
	}
	

	public int bitMasSignificativo(int mask) {
		int aux=0;
		switch (mask) {
		case 0xFFFFFFFF: aux = 31;break;
		case 0x000000FF: aux = 7;break;
		case 0x0000FF00: aux = 7;break;
		case 0x0000FFFF: aux = 15;break;
		}
		return aux; 
	}
	
	public boolean esNegativo(int valor, int mask) {
		return (((valor >>(bitMasSignificativo(mask)))& 0x1) == 1);
	}
	
	public void seteaCC (Registros reg,int resultado) {
		if (resultado == 0) 
			reg.setMenosSignificativoCC(1);
		else 
			reg.setMenosSignificativoCC(0);
		if (resultado < 0) 
			reg.setMasSignificativoCC(1);
		else
			reg.setMasSignificativoCC(0);
	}
	
	public void escribeRegistro(int a ) {
		switch (a & 0Xf) {
		case 10:{switch ((a & 0X00000030)>>4) {
				case 0 : System.out.print("EAX");break;
				case 1 : System.out.print("AL");break;
				case 2 : System.out.print("AH");break;
				case 3 : System.out.print("AX");break;
			}
		break;
		}
		case 11:{
			switch ((a & 0X00000030)>>4) {
			case 0 : System.out.print("EBX");break;
			case 1 : System.out.print("BL");break;
			case 2 : System.out.print("BH");break;
			case 3 : System.out.print("BX");break;
		}
		break;	
		}
		case 12:{
			switch ((a & 0X00000030)>>4) {
			case 0 : System.out.print("ECX");break;
			case 1 : System.out.print("CL");break;
			case 2 : System.out.print("CH");break;
			case 3 : System.out.print("CX");break;
		}
		break;	
		}
		case 13:{
			switch ((a & 0X00000030)>>4) {
			case 0 : System.out.print("EDX");break;
			case 1 : System.out.print("DL");break;
			case 2 : System.out.print("DH");break;
			case 3 : System.out.print("DX");break;
		}
		break;
		}
		case 14:{
			switch ((a & 0X00000030)>>4) {
			case 0 : System.out.print("EEX");break;
			case 1 : System.out.print("EL");break;
			case 2 : System.out.print("EH");break;
			case 3 : System.out.print("EX");break;
		}
		break;
		}
		case 15:{
			switch ((a & 0X00000030)>>4) {
			case 0 : System.out.print("EFX");break;
			case 1 : System.out.print("FL");break;
			case 2 : System.out.print("FH");break;
			case 3 : System.out.print("FX");break;
		}
		break;
		}
		}
	}

	public void muestraOperandos(int a) {
		int vopA,vopB,topA,topB;
		vopA = (a>>12) & 0x00000fff;
		topA = (a & 0X0C000000)>>26;
		topB = (a & 0X03000000)>>24;
		vopB = a & 0x00000fff;
		if ((a>>28 & maskf)!= maskf){
			if (topA == 1){
				escribeRegistro(vopA);
			}
			else {
				if (topA == 2) {
					System.out.print("["+vopA+"]");
				}
				else
					System.out.print(vopA);
			}
			System.out.print(",");
			if (topB == 1){
				escribeRegistro(vopB);
			}
			else {
				if (topB == 2) {
					System.out.print("["+vopB+"]");
				}
				else
					System.out.print(vopA);
			}	
	    }
	    else{
	        if (((a>>24 & maskf) == maskf) && ((a>>28 & maskf) == maskf)) {
	        }
	        else{
	      	   topA = (a & 0X00C00000)>>22;
	      	   vopA = a & 0X0000ffff;
	      	 if (topA == 1){
	 			escribeRegistro(vopA);
	 		}
	 		else {
	 			if (topA == 2) {
	 				System.out.print("["+vopA+"]");
	 			}
	 			else
	 				System.out.print(vopA);
	 		}
	       }
	    }
	}


	public void muestraInstruccion(int a) {
		if ((a>>28 & maskf)!= maskf){   
	        switch (a>>28 & maskf){
	            case 0: System.out.print("MOV");break;
	            case 1: System.out.print("ADD");break;
	            case 2: System.out.print("SUB");break;
	            case 3: System.out.print("SWAP");break;
	            case 4: System.out.print("MUL");break;
	            case 5: System.out.print("DIV");break;
	            case 6: System.out.print("CMP");break;
	            case 7: System.out.print("SHL");break;
	            case 8: System.out.print("SHR");break;
	            case 9: System.out.print("AND");break;
	            case 10: System.out.print("OR");break;
	            case 11: System.out.print("XOR");break;
	        }
	    }
	    else{
	        if (((a>>24 & maskf) == maskf) && ((a>>28 & maskf) == maskf)) 
	        	System.out.print("STOP\n");
	        else{
	      	   
	            switch (a>>24 & maskf){
	                case 0:System.out.print("SYS");break;
	                case 1:System.out.print("JMP");break;
	                case 2:System.out.print("JZ");break;
	                case 3:System.out.print("JP");break;
	                case 4:System.out.print("JN");break;
	                case 5:System.out.print("JNZ");break;
	                case 6:System.out.print("JNP");break;
	                case 7:System.out.print("JNN");break;
	                case 8:System.out.print("LDL");break;
	                case 9:System.out.print("LDH");break;
	                case 11:System.out.print("NOT");break;
	            }   
	        }
	    }
	}
	
	
	public int codEntradaCmd (String cadena) {
		int cod,w;
		boolean espacio= false;
		if (cadena.compareTo("r")==0)
			cod = 1;
		else
			if(cadena.compareTo("p")==0) 
				cod = 2;
			else{
				w=0;
				while (!espacio && w< cadena.length()) {
					if (cadena.charAt(w)==' ') {
						espacio = true;
					}
					w++;	
				}
			if (espacio)
				cod = 4;
			else
				cod = 3;
		}
	return cod;
	}
	
	public String primeraDireccion (String cadena) {
		int w=0;
			while (cadena.charAt(w)!=' ')
				w++;
		return cadena.substring(0,w);
	}
	
	public String segundaDireccion (String cadena) {
		int w=0;
			while (cadena.charAt(w)!=' ') 
				w++;
		return cadena.substring(w+1,cadena.length());
	}
	
}

