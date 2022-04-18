package paquete;

import java.util.HashMap;

public class Registros {


	private HashMap <String, Integer> registros = new HashMap <String, Integer> ();
	
	public Registros() {
		registros.put("DS",0);
		registros.put("IP",0);
		registros.put("CC",0);
		registros.put("AC",0);
		registros.put("EAX",0);
		registros.put("EBX",0);
		registros.put("ECX",0);
		registros.put("EDX",0);
		registros.put("EEX",0);
		registros.put("EFX",0);
	}
	
	
	public int getRegistro(String reg) { //Unico metodo (con setRegistro) que conoce la implementacion de registros
		return this.registros.get(reg);
	}
	
	public void setRegistro(String reg,int valor) {
		this.registros.replace(reg, valor);
	}
	
	
	public int getReg(int numregistro) { //Accedo al valor del registro con su numero
        switch (numregistro){

            case 0:    {
                return getRegistro("DS");
            }
            case 5:{
                return getRegistro("IP");
            }
            case 8:    {
                return getRegistro("CC");
            }
            case 9:{
                return getRegistro("AC");
            }
            case 10:    {
                return getRegistro("EAX");
            }
            case 11:{
                return getRegistro("EBX");
            }
            case 12:    {
                return getRegistro("ECX");
            }
            case 13:{
                return getRegistro("EDX");
            }
            case 14:{
                return getRegistro("EEX");
            }
            case 15:{
                return getRegistro("EFX");
            }

            default:{return 1;}
        }

    }
	
	public void modificaReg(int numregistro,int nuevovalor) { //actualizo el registro completo -> La ALU ve que parte modificar
		switch (numregistro){
		
			case 0:	{
				setRegistro("DS",nuevovalor);break;
			}
			case 5:{
				setRegistro("IP",nuevovalor);break;
			}
			case 8:{
				setRegistro("CC",nuevovalor);break;
			}
			case 9:{
				setRegistro("AC",nuevovalor);break;
			}
			case 10:{
				setRegistro("EAX",nuevovalor);break;
			}
			case 11:{
				setRegistro("EBX",nuevovalor);break;
			}
			case 12:{
				setRegistro("ECX",nuevovalor);break;
			}
			case 13:{
				setRegistro("EDX",nuevovalor);break;
			}
			case 14:{
				setRegistro("EEX",nuevovalor);break;
			}
			case 15:{
				setRegistro("EFX",nuevovalor);break;
			}
		
		}	

	}
	
	//FUNCIONES ESPECIFICAS, LLAMO A UN REGISTRO EN PARTICULAR
	
	//getters
	public int getDS() {
		return this.registros.get("DS");
	}
	
	public int getIP() {
		return this.registros.get("IP");
	}
	
	public int getAC () {
		return getReg(9); 
	}
	
	public int signoCC () {
		return (getRegistro("CC")>>31) & 0X00000001;
	}
	public int ceroCC() {
		return getRegistro("CC") & 0x00000001;
	}
	
	public int getAX () {
		return getRegistro("EAX") & 0X0000FFFF;
	}
	public int getEDX () {
		return getRegistro("EDX");
	}
	public int getCX () {
		return getRegistro("ECX") & 0X0000FFFF;
	}
	
	//setters y modificadores
	public void incrementaIP() {
		int aux= this.registros.get("IP")+1;
		this.registros.replace("IP",aux);
	}
	
	public void setMenosSignificativoCC (int aux) {
		modificaReg(8,aux);
	}
	public void setMasSignificativoCC (int aux) {
		if (getReg(8)>>31 == 1)
			modificaReg(8,getReg(8) & (aux<<31));
		else
			modificaReg(8,getReg(8) | (aux<<31));
	}
	
	public void setAC (int aux) {
		modificaReg(9,aux); 
	}

	public void setIP (int aux) {
		modificaReg(5,aux);
	}
	
	public void setDS(int aux) {
		modificaReg(0,aux);
	}

	
}
