package com.GLS.NESEJ;

import java.util.AbstractMap;

public class Bus {
	
	private int nSystemClockCounter = 0;
	
	public GLS6502 cpu;
	public GLS2C02 ppu;
	public byte[] ram;
	public Cartridge cart;
	
	public Bus() {
		ram = new byte[2048];
		cpu = new GLS6502();
		ppu = new GLS2C02();
		cpu.connectBus(this);
	}
	
	public void reset() {
		cpu.reset();
		nSystemClockCounter = 0;
	}
	
	public void clock() {
		ppu.clock();
		if(nSystemClockCounter%3==0) {
			cpu.clock();
		}
		nSystemClockCounter++;
	}
	
	public void cpuWrite(short saddr, byte data) {
		int addr = saddr&0xFFFF;
		if(cart.cpuWrite(saddr, data)) {
			
		}
		else if(addr>=0x0000&&addr<=0x1FFF) ram[addr&0x07FF] = data;
		else if(addr>=0x2000&&addr<=0x3FFF) ppu.cpuWrite((short)(addr&0x0007), data);
	}
	
	public byte cpuRead(short saddr, boolean rOnly) {
		int addr = saddr&0xFFFF;
		byte data = 0x00;
		AbstractMap.SimpleEntry<Boolean, Byte> cartr = cart.cpuRead(saddr);
		if(cartr.getKey()) data = cartr.getValue();
		else if(addr>=0x0000&&addr<=0x1FFF) data = ram[addr&0x07FF];
		else if(addr>=0x2000&&addr<=0x3FFF) data = ppu.cpuRead((short)(addr&0x0007),rOnly);
		return data;
	}
	
	public byte cpuRead(short addr) {
		return cpuRead(addr, false);
	}

	public void insertCartridge(Cartridge cart) {
		this.cart = cart;
		ppu.connectCartridge(cart);
	}
	
	String hex(int n, int d) {
	    String s = new String(new char[d]);
	    for (int i = d - 1; i >= 0; i--, n >>= 4)
	        s = changeCharInString(i,"0123456789ABCDEF".charAt(n & 0xF),s);
	    return s;
	}

	String changeCharInString(int pos, char c, String s) {
	    return s.substring(0,pos) + c + s.substring(pos+1);
	}

}
