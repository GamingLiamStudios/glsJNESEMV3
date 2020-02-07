package com.GLS.NESEJ.Mappers;

public abstract class Mapper {
	
	protected byte nPRGBanks = 0, nCHRBanks = 0;
	
	public Mapper(byte PRG, byte CHR) {
		this.nPRGBanks = PRG;
		this.nCHRBanks = CHR;
	}
	
	public abstract RBI cpuMapRead(short saddr);
	public abstract RBI cpuMapWrite(short saddr);
	public abstract RBI ppuMapRead(short saddr);
	public abstract RBI ppuMapWrite(short saddr);
	
	public class RBI {
		public boolean b;
		public int i;
		public RBI(boolean b, int i) {
			this.b = b;
			this.i = i;
		}
	}
	
}
