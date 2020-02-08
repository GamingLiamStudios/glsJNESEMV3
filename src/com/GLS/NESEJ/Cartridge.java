package com.GLS.NESEJ;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

import com.GLS.NESEJ.Mappers.*;
import com.GLS.NESEJ.Mappers.Mapper.RBI;

public class Cartridge {
	
	private byte[] vPRGMemory, vCHRMemory;
	private byte nMapperID = 0, nPRGBanks = 0, nCHRBanks = 0;
	private Mapper mapper;
	
	public boolean imageValid = false, mirror;
	
	/* HEADER
	char name[4]; 0-3
	uint8_t prg_rom_chunks; 4
	uint8_t chr_rom_chunks; 5
	uint8_t mapper1; 6
	uint8_t mapper2; 7
	uint8_t prg_ram_size; 8
	uint8_t tv_system1; 9
	uint8_t tv_system2; 10
	char unused[5]; 11-15
	 */
	
	public Cartridge(String fileName) {
		try {
			InputStream i = new FileInputStream(fileName);
			byte[] header = new byte[16];
			i.read(header);
			if((header[6]&0x04)!=0) i.skip(512);
			nMapperID = (byte) ((((header[7]&0xFF)>>4)<<4)|((header[6]&0xFF)>>4));
			mirror = (header[6]&0x01)!=0;
			int nFileType = 1;
			if(nFileType==0) {
				
			}
			if(nFileType==1) {
				nPRGBanks = header[4];
				vPRGMemory = new byte[nPRGBanks*16384];
				i.read(vPRGMemory);
				nCHRBanks = header[5];
				vCHRMemory = new byte[nCHRBanks>0?nCHRBanks*8192:8192];
				i.read(vCHRMemory);
			}
			if(nFileType==2) {
				
			}
			switch(nMapperID&0xFF) {
			case 0: mapper = new Mapper000(nPRGBanks,nCHRBanks);break;
			}
			i.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		imageValid = true;
	}
	
	public SimpleEntry<Boolean, Byte> cpuRead(short saddr) {
		RBI r = mapper.cpuMapRead(saddr);
		byte data = 0;
		if(r.b) data = vPRGMemory[r.i];
		return new AbstractMap.SimpleEntry<>(r.b,data);
	}
	
	public boolean cpuWrite(short saddr, byte data) {
		RBI r = mapper.cpuMapWrite(saddr);
		if(r.b) {
			vPRGMemory[r.i] = data;
			return true;
		}
		else return false;
	}
	
	public SimpleEntry<Boolean, Byte> ppuRead(short saddr) {
		RBI r = mapper.ppuMapRead(saddr);
		byte data = 0;
		if(r.b) data = vCHRMemory[r.i];
		return new AbstractMap.SimpleEntry<>(r.b,data);
	}
	
	public boolean ppuWrite(short saddr, byte data) {
		RBI r = mapper.ppuMapWrite(saddr);
		if(r.b) {
			vCHRMemory[r.i] = data;
			return true;
		}
		else return false;
	}

}
