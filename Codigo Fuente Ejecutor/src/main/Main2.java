package main;

import java.io.IOException;

import paquete.ALU;
import paquete.Memoria;
import paquete.Registros;

public class Main2 {

	public static void main(String[] args) throws InterruptedException, IOException {
	
		Memoria m = Memoria.getInstancia();
		Registros r = Registros.getInstancia();
		ALU alu = new ALU();
		int a = 0xFC40000A; //push
		int b = 0xFD40000B; //pop
		int c = 0xC600A000; //slen
		int d = 0xDA00A000; //smov
		int e = 0xEF00000A; //scmp
		int f = 0xF0000003; //sys%3
		int g = 0xF0000004; //sys%4

		r.setDS(0x000A0019);
		r.setES(0x0BB80023);
		r.setSS(0x13880BDB);
		r.setCS(0x00190000);
		r.setSP(0x00011388);
		r.registros.replace("EDX",0x00021000);
		r.registros.replace("ECX", 40);
		//r.registros.replace("EAX", 87);
		
		m.memoria[25] = 'm';
		m.memoria[26] = 'a';
		m.memoria[27] = 't';
		m.memoria[28] = 'i';
		m.memoria[29] = 'a';
		m.memoria[30] = 's';
		m.memoria[31] = '\0';
		
		alu.ejecutaInstruccion(c);  
		System.out.println(r.getEAX());
		//alu.ejecutaInstruccion(f);
		r.registros.replace("EDX",0x00021000);
		r.registros.replace("ECX", 7);
		r.registros.replace("EAX", 0x00000000);
		//alu.ejecutaInstruccion(g);
	
		
	}

}
