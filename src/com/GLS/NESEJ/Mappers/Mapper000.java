package com.GLS.NESEJ.Mappers;

public class Mapper000 extends Mapper {

	public Mapper000(byte PRG, byte CHR) {
		super(PRG, CHR);
	}

	public RBI cpuMapRead(short saddr) {
		int addr = saddr&0xFFFF;
		if(addr>=0x8000&&addr<=0xFFFF) return new RBI(true,addr&(nPRGBanks>1?0x7FFF:0x3FFF));
		return new RBI(false,0);
	}

	public RBI cpuMapWrite(short saddr) {
		int addr = saddr&0xFFFF;
		if(addr>=0x8000&&addr<=0xFFFF) return new RBI(true,addr&(nPRGBanks>1?0x7FFF:0x3FFF));
		return new RBI(false,0);
	}

	public RBI ppuMapRead(short saddr) {
		int addr = saddr&0xFFFF;
		if(addr>=0x0000&&addr<=0x1FFF) return new RBI(true,addr);
		return new RBI(false,0);
	}

	public RBI ppuMapWrite(short saddr) {
		int addr = saddr&0xFFFF;
		if(addr>=0x0000&&addr<=0x1FFF&&nCHRBanks==0) return new RBI(true,addr);
		return new RBI(false,0);
	}

}
