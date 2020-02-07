package com.GLS.NESEJ;

import java.lang.reflect.Method;
import java.util.HashMap;

public class GLS6502 {
	
	private INSTRUCTION[] lookup;
	private Bus bus;
	
	public final int C = 1<<0;
	public final int Z = 1<<1;
	public final int I = 1<<2;
	public final int D = 1<<3;
	public final int B = 1<<4;
	public final int U = 1<<5;
	public final int V = 1<<6;
	public final int N = 1<<7;
	
	public byte a = 0x00, x = 0x00, y = 0x00, stkp = 0x00, status = 0x00;
	public short pc = 0x0000, addr_abs = 0x0000, addr_rel = 0x0000, temp = 0x0000;
	public byte fetched = 0x00, opcode = 0x00;
	public int cycles;
	
	public class INSTRUCTION {
		String name, operate, addrmode;
		int cycles;
		public INSTRUCTION(String name, String operate, String addrMode, int cycles) {
			this.name = name;
			this.operate = operate;
			this.addrmode = addrMode;
			this.cycles = cycles;
		}
	}
	
	public GLS6502() {
		INSTRUCTION[] lookup2 = {
			new INSTRUCTION( "BRK", "BRK", "IMM", 7 ),new INSTRUCTION( "ORA", "ORA", "IZX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 3 ),new INSTRUCTION( "ORA", "ORA", "ZP0", 3 ),new INSTRUCTION( "ASL", "ASL", "ZP0", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "PHP", "PHP", "IMP", 3 ),new INSTRUCTION( "ORA", "ORA", "IMM", 2 ),new INSTRUCTION( "ASL", "ASL", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "ORA", "ORA", "ABS", 4 ),new INSTRUCTION( "ASL", "ASL", "ABS", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),
			new INSTRUCTION( "BPL", "BPL", "REL", 2 ),new INSTRUCTION( "ORA", "ORA", "IZY", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "ORA", "ORA", "ZPX", 4 ),new INSTRUCTION( "ASL", "ASL", "ZPX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "CLC", "CLC", "IMP", 2 ),new INSTRUCTION( "ORA", "ORA", "ABY", 4 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "ORA", "ORA", "ABX", 4 ),new INSTRUCTION( "ASL", "ASL", "ABX", 7 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),
			new INSTRUCTION( "JSR", "JSR", "ABS", 6 ),new INSTRUCTION( "AND", "AND", "IZX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "BIT", "BIT", "ZP0", 3 ),new INSTRUCTION( "AND", "AND", "ZP0", 3 ),new INSTRUCTION( "ROL", "ROL", "ZP0", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "PLP", "PLP", "IMP", 4 ),new INSTRUCTION( "AND", "AND", "IMM", 2 ),new INSTRUCTION( "ROL", "ROL", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "BIT", "BIT", "ABS", 4 ),new INSTRUCTION( "AND", "AND", "ABS", 4 ),new INSTRUCTION( "ROL", "ROL", "ABS", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),
			new INSTRUCTION( "BMI", "BMI", "REL", 2 ),new INSTRUCTION( "AND", "AND", "IZY", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "AND", "AND", "ZPX", 4 ),new INSTRUCTION( "ROL", "ROL", "ZPX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "SEC", "SEC", "IMP", 2 ),new INSTRUCTION( "AND", "AND", "ABY", 4 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "AND", "AND", "ABX", 4 ),new INSTRUCTION( "ROL", "ROL", "ABX", 7 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),
			new INSTRUCTION( "RTI", "RTI", "IMP", 6 ),new INSTRUCTION( "EOR", "EOR", "IZX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 3 ),new INSTRUCTION( "EOR", "EOR", "ZP0", 3 ),new INSTRUCTION( "LSR", "LSR", "ZP0", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "PHA", "PHA", "IMP", 3 ),new INSTRUCTION( "EOR", "EOR", "IMM", 2 ),new INSTRUCTION( "LSR", "LSR", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "JMP", "JMP", "ABS", 3 ),new INSTRUCTION( "EOR", "EOR", "ABS", 4 ),new INSTRUCTION( "LSR", "LSR", "ABS", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),
			new INSTRUCTION( "BVC", "BVC", "REL", 2 ),new INSTRUCTION( "EOR", "EOR", "IZY", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "EOR", "EOR", "ZPX", 4 ),new INSTRUCTION( "LSR", "LSR", "ZPX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "CLI", "CLI", "IMP", 2 ),new INSTRUCTION( "EOR", "EOR", "ABY", 4 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "EOR", "EOR", "ABX", 4 ),new INSTRUCTION( "LSR", "LSR", "ABX", 7 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),
			new INSTRUCTION( "RTS", "RTS", "IMP", 6 ),new INSTRUCTION( "ADC", "ADC", "IZX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 3 ),new INSTRUCTION( "ADC", "ADC", "ZP0", 3 ),new INSTRUCTION( "ROR", "ROR", "ZP0", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "PLA", "PLA", "IMP", 4 ),new INSTRUCTION( "ADC", "ADC", "IMM", 2 ),new INSTRUCTION( "ROR", "ROR", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "JMP", "JMP", "IND", 5 ),new INSTRUCTION( "ADC", "ADC", "ABS", 4 ),new INSTRUCTION( "ROR", "ROR", "ABS", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),
			new INSTRUCTION( "BVS", "BVS", "REL", 2 ),new INSTRUCTION( "ADC", "ADC", "IZY", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "ADC", "ADC", "ZPX", 4 ),new INSTRUCTION( "ROR", "ROR", "ZPX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "SEI", "SEI", "IMP", 2 ),new INSTRUCTION( "ADC", "ADC", "ABY", 4 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "ADC", "ADC", "ABX", 4 ),new INSTRUCTION( "ROR", "ROR", "ABX", 7 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),
			new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "STA", "STA", "IZX", 6 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "STY", "STY", "ZP0", 3 ),new INSTRUCTION( "STA", "STA", "ZP0", 3 ),new INSTRUCTION( "STX", "STX", "ZP0", 3 ),new INSTRUCTION( "???", "XXX", "IMP", 3 ),new INSTRUCTION( "DEY", "DEY", "IMP", 2 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "TXA", "TXA", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "STY", "STY", "ABS", 4 ),new INSTRUCTION( "STA", "STA", "ABS", 4 ),new INSTRUCTION( "STX", "STX", "ABS", 4 ),new INSTRUCTION( "???", "XXX", "IMP", 4 ),
			new INSTRUCTION( "BCC", "BCC", "REL", 2 ),new INSTRUCTION( "STA", "STA", "IZY", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "STY", "STY", "ZPX", 4 ),new INSTRUCTION( "STA", "STA", "ZPX", 4 ),new INSTRUCTION( "STX", "STX", "ZPY", 4 ),new INSTRUCTION( "???", "XXX", "IMP", 4 ),new INSTRUCTION( "TYA", "TYA", "IMP", 2 ),new INSTRUCTION( "STA", "STA", "ABY", 5 ),new INSTRUCTION( "TXS", "TXS", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "???", "NOP", "IMP", 5 ),new INSTRUCTION( "STA", "STA", "ABX", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),
			new INSTRUCTION( "LDY", "LDY", "IMM", 2 ),new INSTRUCTION( "LDA", "LDA", "IZX", 6 ),new INSTRUCTION( "LDX", "LDX", "IMM", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "LDY", "LDY", "ZP0", 3 ),new INSTRUCTION( "LDA", "LDA", "ZP0", 3 ),new INSTRUCTION( "LDX", "LDX", "ZP0", 3 ),new INSTRUCTION( "???", "XXX", "IMP", 3 ),new INSTRUCTION( "TAY", "TAY", "IMP", 2 ),new INSTRUCTION( "LDA", "LDA", "IMM", 2 ),new INSTRUCTION( "TAX", "TAX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "LDY", "LDY", "ABS", 4 ),new INSTRUCTION( "LDA", "LDA", "ABS", 4 ),new INSTRUCTION( "LDX", "LDX", "ABS", 4 ),new INSTRUCTION( "???", "XXX", "IMP", 4 ),
			new INSTRUCTION( "BCS", "BCS", "REL", 2 ),new INSTRUCTION( "LDA", "LDA", "IZY", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "LDY", "LDY", "ZPX", 4 ),new INSTRUCTION( "LDA", "LDA", "ZPX", 4 ),new INSTRUCTION( "LDX", "LDX", "ZPY", 4 ),new INSTRUCTION( "???", "XXX", "IMP", 4 ),new INSTRUCTION( "CLV", "CLV", "IMP", 2 ),new INSTRUCTION( "LDA", "LDA", "ABY", 4 ),new INSTRUCTION( "TSX", "TSX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 4 ),new INSTRUCTION( "LDY", "LDY", "ABX", 4 ),new INSTRUCTION( "LDA", "LDA", "ABX", 4 ),new INSTRUCTION( "LDX", "LDX", "ABY", 4 ),new INSTRUCTION( "???", "XXX", "IMP", 4 ),
			new INSTRUCTION( "CPY", "CPY", "IMM", 2 ),new INSTRUCTION( "CMP", "CMP", "IZX", 6 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "CPY", "CPY", "ZP0", 3 ),new INSTRUCTION( "CMP", "CMP", "ZP0", 3 ),new INSTRUCTION( "DEC", "DEC", "ZP0", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "INY", "INY", "IMP", 2 ),new INSTRUCTION( "CMP", "CMP", "IMM", 2 ),new INSTRUCTION( "DEX", "DEX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "CPY", "CPY", "ABS", 4 ),new INSTRUCTION( "CMP", "CMP", "ABS", 4 ),new INSTRUCTION( "DEC", "DEC", "ABS", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),
			new INSTRUCTION( "BNE", "BNE", "REL", 2 ),new INSTRUCTION( "CMP", "CMP", "IZY", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "CMP", "CMP", "ZPX", 4 ),new INSTRUCTION( "DEC", "DEC", "ZPX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "CLD", "CLD", "IMP", 2 ),new INSTRUCTION( "CMP", "CMP", "ABY", 4 ),new INSTRUCTION( "NOP", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "CMP", "CMP", "ABX", 4 ),new INSTRUCTION( "DEC", "DEC", "ABX", 7 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),
			new INSTRUCTION( "CPX", "CPX", "IMM", 2 ),new INSTRUCTION( "SBC", "SBC", "IZX", 6 ),new INSTRUCTION( "???", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "CPX", "CPX", "ZP0", 3 ),new INSTRUCTION( "SBC", "SBC", "ZP0", 3 ),new INSTRUCTION( "INC", "INC", "ZP0", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 5 ),new INSTRUCTION( "INX", "INX", "IMP", 2 ),new INSTRUCTION( "SBC", "SBC", "IMM", 2 ),new INSTRUCTION( "NOP", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "SBC", "IMP", 2 ),new INSTRUCTION( "CPX", "CPX", "ABS", 4 ),new INSTRUCTION( "SBC", "SBC", "ABS", 4 ),new INSTRUCTION( "INC", "INC", "ABS", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),
			new INSTRUCTION( "BEQ", "BEQ", "REL", 2 ),new INSTRUCTION( "SBC", "SBC", "IZY", 5 ),new INSTRUCTION( "???", "XXX", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 8 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "SBC", "SBC", "ZPX", 4 ),new INSTRUCTION( "INC", "INC", "ZPX", 6 ),new INSTRUCTION( "???", "XXX", "IMP", 6 ),new INSTRUCTION( "SED", "SED", "IMP", 2 ),new INSTRUCTION( "SBC", "SBC", "ABY", 4 ),new INSTRUCTION( "NOP", "NOP", "IMP", 2 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),new INSTRUCTION( "???", "NOP", "IMP", 4 ),new INSTRUCTION( "SBC", "SBC", "ABX", 4 ),new INSTRUCTION( "INC", "INC", "ABX", 7 ),new INSTRUCTION( "???", "XXX", "IMP", 7 ),
		};
		lookup = lookup2.clone();
	}
	
	public byte callMethod(String method) {
		byte data = 0x00;
		try {
			Method m = this.getClass().getMethod(method);
			data = (byte)m.invoke(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void connectBus(Bus bus) {
		this.bus = bus;
	}
	
	private byte read(int addr) {
		return bus.cpuRead((short)addr);
	}
	
	private byte read(int addr, boolean rOnly) {
		return bus.cpuRead((short)addr, rOnly);
	}
	
	private void write(short addr, byte data) {
		bus.cpuWrite(addr, data);
	}
	
	private byte getFlag(int f) {
		return (byte) (((status&f)!=0)?1:0);
	}
	
	private void setFlag(int f, boolean v) {
		if(v) status|=f;
		else status&=~f;
	}
	
	private void setFlag(int f, int v) {
		if(v==1) status|=f;
		else status&=~f;
	}
	
	public void clock() {
		if(cycles==0) {
			opcode = read(pc);
			pc++;
			cycles = lookup[opcode&0xFF].cycles;
			byte c1 = callMethod(lookup[opcode&0xFF].addrmode);
			byte c2 = callMethod(lookup[opcode&0xFF].operate);
			cycles+=(c1&c2);
		}
		cycles--;
	}
	
	public void reset() {
		a = 0;
		x = 0;
		y = 0;
		stkp = (byte) 0xFD;
		status = 0x00 | U;
		addr_abs = (short) 0xFFFC;
		short lo = (short) (read(addr_abs)&0xFF);
		short hi = (short) (read(addr_abs+1)&0xFF);
		pc = (short) ((hi<<8)|lo);
		addr_rel = 0x0000;
		addr_abs = 0x0000;
		fetched = 0x00;
		cycles = 8;
	}
	
	public void irq() {
		if(getFlag(I)==0) {
			write((short) (0x0100+(stkp&0xFF)),(byte)((pc>>8)&0x00FF));
			stkp--;
			write((short) (0x0100+(stkp&0xFF)),(byte)(pc&0x00FF));
			stkp--;
			setFlag(B, 0);
			setFlag(U, 1);
			setFlag(I, 1);
			write((short) (0x0100+(stkp&0xFF)), status);
			stkp--;
			addr_abs = (short) 0xFFFE;
			short lo = (short) (read(addr_abs)&0xFF);
			short hi = (short) (read(addr_abs+1)&0xFF);
			pc = (short) ((hi<<8)|lo);
			cycles = 7;
		}
	}
	
	public void nmi() {
		write((short) (0x0100+(stkp&0xFF)),(byte)((pc>>8)&0x00FF));
		stkp--;
		write((short) (0x0100+(stkp&0xFF)),(byte)(pc&0x00FF));
		stkp--;
		setFlag(B, 0);
		setFlag(U, 1);
		setFlag(I, 1);
		write((short) (0x0100+(stkp&0xFF)), status);
		stkp--;
		addr_abs = (short) 0xFFFA;
		short lo = (short) (read(addr_abs)&0xFF);
		short hi = (short) (read(addr_abs+1)&0xFF);
		pc = (short) ((hi<<8)|lo);
		cycles = 8;
	}
	
	public byte IMP() {
		fetched = a;
		return 0;
	}

	public byte IMM() {
		addr_abs = pc++;	
		return 0;
	}

	public byte ZP0() {
		addr_abs = (short)(read(pc)&0xFF);	
		pc++;
		addr_abs &= 0x00FF;
		return 0;
	}

	public byte ZPX() {
		addr_abs = (short)((read(pc)&0xFF) + (x&0xFF));
		pc++;
		addr_abs &= 0x00FF;
		return 0;
	}

	public byte ZPY() {
		addr_abs = (short)((read(pc)&0xFF) + (y&0xFF));
		pc++;
		addr_abs &= 0x00FF;
		return 0;
	}

	public byte REL() {
		addr_rel = (short)(read(pc)&0xFF);
		pc++;
		if ((addr_rel & 0x80)!=0) addr_rel |= 0xFF00;
		return 0;
	}

	public byte ABS() {
		short lo = (short)(read(pc)&0xFF);
		pc++;
		short hi = (short)(read(pc)&0xFF);
		pc++;
		addr_abs = (short)(((hi&0xFF) << 8) | (lo&0xFF));
		return 0;
	}

	public byte ABX() {
		short lo = (short)(read(pc)&0xFF);
		pc++;
		short hi = (short)(read(pc)&0xFF);
		pc++;
		addr_abs = (short)((hi << 8) | lo);
		addr_abs = (short)((addr_abs&0xFF)+(x&0xFF));
		if ((addr_abs & 0xFF00) != (short)(hi << 8))
			return 0x01;
		else
			return 0x00;	
	}

	public byte ABY() {
		short lo = (short)(read(pc)&0xFF);
		pc++;
		short hi = (short)(read(pc)&0xFF);
		pc++;
		addr_abs = (short)((hi << 8) | lo);
		addr_abs = (short)((addr_abs&0xFF)+(y&0xFF));
		if ((addr_abs & 0xFF00) != (short)(hi << 8))
			return 0x01;
		else
			return 0x00;	
	}

	public byte IND() {
		short ptr_lo = (short)(read(pc)&0xFF);
		pc++;
		short ptr_hi = (short)(read(pc)&0xFF);
		pc++;
		short ptr = (short)((ptr_hi << 8) | ptr_lo);
		if (ptr_lo == 0x00FF) 
			addr_abs = (short)(((read(ptr & 0xFF00)&0xFF) << 8) | (read(ptr + 0)&0xFF));
		else 
			addr_abs = (short)(((read(ptr + 1)&0xFF) << 8) | (read(ptr + 0)&0xFF));
		
		return 0;
	}

	public byte IZX() {
		byte t = (byte)(read(pc)&0xFF);
		pc++;
		short lo = (short) (read((short)((t&0xFF) + (x&0xFF)) & 0x00FF)&0xFF);
		short hi = (short) (read((short)((t&0xFF) + (x&0xFF) + 1) & 0x00FF)&0xFF);
		addr_abs = (short)((hi << 8) | lo);
		return 0;
	}

	public byte IZY() {
		short t = read(pc);
		pc++;
		short lo = (short)(read(t & 0x00FF)&0xFF);
		short hi = (short)(read((t + 1) & 0x00FF)&0xFF);
		addr_abs = (short)((hi << 8) | lo);
		addr_abs = (short)((addr_abs&0xFF)+(y&0xFF));
		if ((addr_abs & 0xFF00) != (hi << 8))
			return 1;
		else
			return 0;
	}

	public byte fetch() {
		if (!(lookup[opcode&0xFF].addrmode == "IMP"))
			fetched = read(addr_abs);
		return fetched;
	}

	public byte ADC() {
		fetch();
		temp = (short) ((short)(a&0xFF) + (short)(fetched&0xFF) + (short)(getFlag(C)&0xFF));
		setFlag(C, temp > 255);
		setFlag(Z, (temp & 0x00FF) == 0);
		setFlag(V, ((~((short)a ^ (short)fetched) & ((short)a ^ (short)temp)) & 0x0080)!=0);
		setFlag(N, (temp & 0x80)!=0);
		a = (byte) (temp & 0x00FF);
		return 1;
	}

	public byte SBC() {
		fetch();
		short value = (short) ((short)(fetched&0xFF) ^ 0x00FF);
		temp = (short) ((short)(a&0xFF) + value + (short)(getFlag(C)&0xFF));
		setFlag(C, (temp & 0xFF00)!=0);
		setFlag(Z, ((temp & 0x00FF) == 0));
		setFlag(V, ((temp ^ (short)a) & (temp ^ value) & 0x0080)!=0);
		setFlag(N, (temp & 0x0080)!=0);
		a = (byte) (temp & 0x00FF);
		return 1;
	}

	public byte AND() {
		fetch();
		a = (byte) (a & fetched);
		setFlag(Z, a == 0x00);
		setFlag(N, (a & 0x80)!=0);
		return 1;
	}

	public byte ASL() {
		fetch();
		temp = (short) ((short)(fetched&0xFF) << 1);
		setFlag(C, (temp & 0xFF00) > 0);
		setFlag(Z, (temp & 0x00FF) == 0x00);
		setFlag(N, (temp & 0x80)!=0);
		if (lookup[opcode&0xFF].addrmode == "IMP")
			a = (byte) (temp & 0x00FF);
		else
			write(addr_abs, (byte)(temp & 0x00FF));
		return 0;
	}

	public byte BCC() {
		if (getFlag(C) == 0) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte BCS() {
		if (getFlag(C) == 1) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte BEQ() {
		if (getFlag(Z) == 1) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte BIT() {
		fetch();
		temp = (short) (a & fetched);
		setFlag(Z, (temp & 0x00FF) == 0x00);
		setFlag(N, fetched & (1 << 7));
		setFlag(V, fetched & (1 << 6));
		return 0;
	}

	public byte BMI() {
		if (getFlag(N) == 1) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte BNE() {
		if (getFlag(Z) == 0) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte BPL() {
		if (getFlag(N) == 0) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte BRK() {
		pc++;
		setFlag(I, 1);
		write((short) (0x0100 + (stkp&0xFF)), (byte)((pc >> 8) & 0x00FF));
		stkp--;
		write((short) (0x0100 + (stkp&0xFF)), (byte)(pc & 0x00FF));
		stkp--;
		setFlag(B, 1);
		write((short) (0x0100 + (stkp&0xFF)), status);
		stkp--;
		setFlag(B, 0);
		pc = (short) ((short)(read(0xFFFE)&0xFF) | ((short)(read(0xFFFF)&0xFF) << 8));
		return 0;
	}

	public byte BVC() {
		if (getFlag(V) == 0) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte BVS() {
		if (getFlag(V) == 1) {
			cycles++;
			addr_abs = (short)((pc&0xFFFF) + (addr_rel&0xFFFF));
			if((addr_abs & 0xFF00) != (pc & 0xFF00))
				cycles++;
			pc = addr_abs;
		}
		return 0;
	}

	public byte CLC() {
		setFlag(C, false);
		return 0;
	}

	public byte CLD() {
		setFlag(D, false);
		return 0;
	}

	public byte CLI() {
		setFlag(I, false);
		return 0;
	}

	public byte CLV() {
		setFlag(V, false);
		return 0;
	}

	public byte CMP() {
		fetch();
		temp = (short) ((short)(a&0xFF) - (short)(fetched&0xFF));
		setFlag(C, a >= fetched);
		setFlag(Z, (temp & 0x00FF) == 0x0000);
		setFlag(N, (temp & 0x0080)!=0);
		return 1;
	}

	public byte CPX() {
		fetch();
		temp = (short) ((short)(x&0xFF) - (short)(fetched&0xFF));
		setFlag(C, x >= fetched);
		setFlag(Z, (temp & 0x00FF) == 0x0000);
		setFlag(N, (temp & 0x0080)!=0);
		return 0;
	}

	public byte CPY() {
		fetch();
		temp = (short) ((short)(y&0xFF) - (short)(fetched&0xFF));
		setFlag(C, y >= fetched);
		setFlag(Z, (temp & 0x00FF) == 0x0000);
		setFlag(N, (temp & 0x0080)!=0);
		return 0;
	}

	public byte DEC() {
		fetch();
		temp = (short) ((fetched - 1)&0xFF);
		write(addr_abs, (byte) (temp & 0x00FF));
		setFlag(Z, (temp & 0x00FF) == 0x0000);
		setFlag(N, (temp & 0x0080)!=0);
		return 0;
	}

	public byte DEX() {
		x--;
		setFlag(Z, x == 0x00);
		setFlag(N, (x & 0x80)!=0);
		return 0;
	}

	public byte DEY() {
		y--;
		setFlag(Z, y == 0x00);
		setFlag(N, (y & 0x80)!=0);
		return 0;
	}

	public byte EOR() {
		fetch();
		a = (byte) (a ^ fetched);	
		setFlag(Z, a == 0x00);
		setFlag(N, (a & 0x80)!=0);
		return 1;
	}

	public byte INC() {
		fetch();
		temp = (short) ((fetched + 1)&0xFF);
		write(addr_abs, (byte) (temp & 0x00FF));
		setFlag(Z, (temp & 0x00FF) == 0x0000);
		setFlag(N, (temp & 0x0080)!=0);
		return 0;
	}

	public byte INX() {
		x++;
		setFlag(Z, x == 0x00);
		setFlag(N, (x & 0x80)!=0);
		return 0;
	}

	public byte INY() {
		y++;
		setFlag(Z, y == 0x00);
		setFlag(N, (y & 0x80)!=0);
		return 0;
	}

	public byte JMP() {
		pc = addr_abs;
		return 0;
	}

	public byte JSR() {
		pc--;
		write((short) (0x0100 + (stkp&0xFF)), (byte)((pc >> 8) & 0x00FF));
		stkp--;
		write((short) (0x0100 + (stkp&0xFF)), (byte)(pc & 0x00FF));
		stkp--;
		pc = addr_abs;
		return 0;
	}

	public byte LDA() {
		fetch();
		a = fetched;
		setFlag(Z, a == 0x00);
		setFlag(N, (a & 0x80)!=0);
		return 1;
	}

	public byte LDX() {
		fetch();
		x = fetched;
		setFlag(Z, x == 0x00);
		setFlag(N, (x & 0x80)!=0);
		return 1;
	}

	public byte LDY() {
		fetch();
		y = fetched;
		setFlag(Z, y == 0x00);
		setFlag(N, (y & 0x80)!=0);
		return 1;
	}

	public byte LSR() {
		fetch();
		setFlag(C, (fetched & 0x0001)!=0);
		temp = (short) ((fetched >> 1)&0xFF);	
		setFlag(Z, (temp & 0x00FF) == 0x0000);
		setFlag(N, (temp & 0x0080)!=0);
		if (lookup[opcode&0xFF].addrmode == "IMP")
			a = (byte) (temp & 0x00FF);
		else
			write(addr_abs, (byte) (temp & 0x00FF));
		return 0;
	}

	public byte NOP() {
		switch (opcode) {
		case 0x1C:
		case 0x3C:
		case 0x5C:
		case 0x7C:
		case (byte) 0xDC:
		case (byte) 0xFC:
			return 1;
		}
		return 0;
	}

	public byte ORA() {
		fetch();
		a = (byte) (a | fetched);
		setFlag(Z, a == 0x00);
		setFlag(N, (a & 0x80)!=0);
		return 1;
	}

	public byte PHA() {
		write((short) (0x0100 + (stkp&0xFF)), a);
		stkp--;
		return 0;
	}

	public byte PHP() {
		write((short) (0x0100 + (stkp&0xFF)), (byte)(status | B | U));
		setFlag(B, 0);
		setFlag(U, 0);
		stkp--;
		return 0;
	}

	public byte PLA() {
		stkp++;
		a = read(0x0100 + (stkp&0xFF));
		setFlag(Z, a == 0x00);
		setFlag(N, (a & 0x80)!=0);
		return 0;
	}

	public byte PLP() {
		stkp++;
		status = read(0x0100 + (stkp&0xFF));
		setFlag(U, 1);
		return 0;
	}

	public byte ROL() {
		fetch();
		temp = (short) (((short)(fetched << 1) | getFlag(C))&0xFF);
		setFlag(C, (temp & 0xFF00)!=0);
		setFlag(Z, (temp & 0x00FF) == 0x0000);
		setFlag(N, (temp & 0x0080)!=0);
		if (lookup[opcode&0xFF].addrmode == "IMP")
			a = (byte) (temp & 0x00FF);
		else
			write(addr_abs, (byte) (temp & 0x00FF));
		return 0;
	}

	public byte ROR() {
		fetch();
		temp = (short) (((short)(getFlag(C) << 7) | (fetched >> 1))&0xFF);
		setFlag(C, (fetched & 0x01)!=0);
		setFlag(Z, (temp & 0x00FF) == 0x00);
		setFlag(N, (temp & 0x0080)!=0);
		if (lookup[opcode&0xFF].addrmode == "IMP")
			a = (byte) (temp & 0x00FF);
		else
			write(addr_abs, (byte) (temp & 0x00FF));
		return 0;
	}

	public byte RTI() {
		stkp++;
		status = read(0x0100 + (stkp&0xFF));
		status &= ~B;
		status &= ~U;
		stkp++;
		pc = (short)read(0x0100 + (stkp&0xFF));
		stkp++;
		pc |= ((short)read(0x0100 + (stkp&0xFF)) << 8)&0xFFFF;
		return 0;
	}

	public byte RTS()
	{
		stkp++;
		pc = (short) ((short)read(0x0100 + (stkp&0xFF))&0xFF);
		stkp++;
		pc |= ((short)read(0x0100 + (stkp&0xFF)) << 8)&0xFFFF;
		
		pc++;
		return 0;
	}

	public byte SEC() {
		setFlag(C, true);
		return 0;
	}

	public byte SED() {
		setFlag(D, true);
		return 0;
	}

	public byte SEI() {
		setFlag(I, true);
		return 0;
	}

	public byte STA() {
		write(addr_abs, a);
		return 0;
	}

	public byte STX() {
		write(addr_abs, x);
		return 0;
	}

	public byte STY() {
		write(addr_abs, y);
		return 0;
	}

	public byte TAX() {
		x = a;
		setFlag(Z, x == 0x00);
		setFlag(N, (x & 0x80)!=0);
		return 0;
	}

	public byte TAY() {
		y = a;
		setFlag(Z, y == 0x00);
		setFlag(N, (y & 0x80)!=0);
		return 0;
	}

	public byte TSX() {
		x = stkp;
		setFlag(Z, x == 0x00);
		setFlag(N, (x & 0x80)!=0);
		return 0;
	}

	public byte TXA() {
		a = x;
		setFlag(Z, a == 0x00);
		setFlag(N, (a & 0x80)!=0);
		return 0;
	}

	public byte TXS() {
		stkp = x;
		return 0;
	}

	public byte TYA() {
		a = y;
		setFlag(Z, a == 0x00);
		setFlag(N, (a & 0x80)!=0);
		return 0;
	}

	public byte XXX() {
		return 0;
	}
	
	public boolean complete() {
		return cycles == 0;
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

	public HashMap<Integer, String> disassemble(int nStart, int nStop) {
		int addr = nStart;
		short value = 0x00, lo = 0x00, hi = 0x00;
		HashMap<Integer, String> mapLines = new HashMap<>();
		int line_addr = 0;

		while (addr <= (int)nStop) {
			line_addr = addr;
			String sInst = "$" + hex(addr, 4) + ": ";
			byte opcode = read((short)addr, true);
			addr++;
			sInst += lookup[opcode&0xFF].name + " ";
			if (lookup[opcode&0xFF].addrmode == "IMP") {
				sInst += " {IMP}";
			}
			else if (lookup[opcode&0xFF].addrmode == "IMM") {
				value = bus.cpuRead((short) addr, true); addr++;
				sInst += "#$" + hex(value, 2) + " {IMM}";
			}
			else if (lookup[opcode&0xFF].addrmode == "ZP0") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = 0x00;												
				sInst += "$" + hex(lo, 2) + " {ZP0}";
			}
			else if (lookup[opcode&0xFF].addrmode == "ZPX") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = 0x00;														
				sInst += "$" + hex(lo, 2) + ", X {ZPX}";
			}
			else if (lookup[opcode&0xFF].addrmode == "ZPY") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = 0x00;														
				sInst += "$" + hex(lo, 2) + ", Y {ZPY}";
			}
			else if (lookup[opcode&0xFF].addrmode == "IZX") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = 0x00;								
				sInst += "($" + hex(lo, 2) + ", X) {IZX}";
			}
			else if (lookup[opcode&0xFF].addrmode == "IZY") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = 0x00;								
				sInst += "($" + hex(lo, 2) + "), Y {IZY}";
			}
			else if (lookup[opcode&0xFF].addrmode == "ABS") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = bus.cpuRead((short) addr, true); addr++;
				sInst += "$" + hex((int)(hi << 8) | lo, 4) + " {ABS}";
			}
			else if (lookup[opcode&0xFF].addrmode == "ABX") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = bus.cpuRead((short) addr, true); addr++;
				sInst += "$" + hex((int)(hi << 8) | lo, 4) + ", X {ABX}";
			}
			else if (lookup[opcode&0xFF].addrmode == "ABY") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = bus.cpuRead((short) addr, true); addr++;
				sInst += "$" + hex((int)(hi << 8) | lo, 4) + ", Y {ABY}";
			}
			else if (lookup[opcode&0xFF].addrmode == "IND") {
				lo = bus.cpuRead((short) addr, true); addr++;
				hi = bus.cpuRead((short) addr, true); addr++;
				sInst += "($" + hex((int)(hi << 8) | lo, 4) + ") {IND}";
			}
			else if (lookup[opcode&0xFF].addrmode == "REL") {
				value = bus.cpuRead((short) addr, true); addr++;
				sInst += "$" + hex(value, 2) + " [$" + hex(addr + value, 4) + "] {REL}";
			}
			mapLines.put(line_addr, sInst);
		}

		return mapLines;
	}

}
