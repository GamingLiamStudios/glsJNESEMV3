package com.GLS.NESEJ;

import java.awt.Color;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

public class GLS2C02 {
	
	private Random rand;
	
	private Cartridge cart;
	
	private byte[][] tblName;
	private byte[] tblPalette;
	private byte[][] tblPattern;
	
	private Color[] palScreen;
	private Sprite sprScreen;
	private Sprite[] sprNameTable, sprPatternTable;
	
	private byte ppu_data_buffer = 0x00, address_latch = 0x00;
	private short ppu_address = 0x0000;
	
	private int scanline = 0, cycle = 0;
	
	public boolean frame_complete = false;
	
	byte status; //unused:1-5 sprite_overflow:6 sprite_zero_hit:7 vertical blank:8
	byte mask; //gs rbl rsl rb rs er eg eb
	byte control; //ntx nty im ps pb ss sm en
	
	public GLS2C02() {
		rand = new Random();
		tblName = new byte[2][1024];
		tblPalette = new byte[32];
		tblPattern = new byte[2][4096]; //Javid Future
		palScreen = new Color[0x40];
		sprScreen = new Sprite(256,240);
		sprNameTable = new Sprite[]{new Sprite(256,240),new Sprite(256,240)};
		sprPatternTable = new Sprite[]{new Sprite(128,128),new Sprite(128,128)};
		palScreen[0x00] = new Color(84, 84, 84);
		palScreen[0x01] = new Color(0, 30, 116);
		palScreen[0x02] = new Color(8, 16, 144);
		palScreen[0x03] = new Color(48, 0, 136);
		palScreen[0x04] = new Color(68, 0, 100);
		palScreen[0x05] = new Color(92, 0, 48);
		palScreen[0x06] = new Color(84, 4, 0);
		palScreen[0x07] = new Color(60, 24, 0);
		palScreen[0x08] = new Color(32, 42, 0);
		palScreen[0x09] = new Color(8, 58, 0);
		palScreen[0x0A] = new Color(0, 64, 0);
		palScreen[0x0B] = new Color(0, 60, 0);
		palScreen[0x0C] = new Color(0, 50, 60);
		palScreen[0x0D] = new Color(0, 0, 0);
		palScreen[0x0E] = new Color(0, 0, 0);
		palScreen[0x0F] = new Color(0, 0, 0);

		palScreen[0x10] = new Color(152, 150, 152);
		palScreen[0x11] = new Color(8, 76, 196);
		palScreen[0x12] = new Color(48, 50, 236);
		palScreen[0x13] = new Color(92, 30, 228);
		palScreen[0x14] = new Color(136, 20, 176);
		palScreen[0x15] = new Color(160, 20, 100);
		palScreen[0x16] = new Color(152, 34, 32);
		palScreen[0x17] = new Color(120, 60, 0);
		palScreen[0x18] = new Color(84, 90, 0);
		palScreen[0x19] = new Color(40, 114, 0);
		palScreen[0x1A] = new Color(8, 124, 0);
		palScreen[0x1B] = new Color(0, 118, 40);
		palScreen[0x1C] = new Color(0, 102, 120);
		palScreen[0x1D] = new Color(0, 0, 0);
		palScreen[0x1E] = new Color(0, 0, 0);
		palScreen[0x1F] = new Color(0, 0, 0);

		palScreen[0x20] = new Color(236, 238, 236);
		palScreen[0x21] = new Color(76, 154, 236);
		palScreen[0x22] = new Color(120, 124, 236);
		palScreen[0x23] = new Color(176, 98, 236);
		palScreen[0x24] = new Color(228, 84, 236);
		palScreen[0x25] = new Color(236, 88, 180);
		palScreen[0x26] = new Color(236, 106, 100);
		palScreen[0x27] = new Color(212, 136, 32);
		palScreen[0x28] = new Color(160, 170, 0);
		palScreen[0x29] = new Color(116, 196, 0);
		palScreen[0x2A] = new Color(76, 208, 32);
		palScreen[0x2B] = new Color(56, 204, 108);
		palScreen[0x2C] = new Color(56, 180, 204);
		palScreen[0x2D] = new Color(60, 60, 60);
		palScreen[0x2E] = new Color(0, 0, 0);
		palScreen[0x2F] = new Color(0, 0, 0);

		palScreen[0x30] = new Color(236, 238, 236);
		palScreen[0x31] = new Color(168, 204, 236);
		palScreen[0x32] = new Color(188, 188, 236);
		palScreen[0x33] = new Color(212, 178, 236);
		palScreen[0x34] = new Color(236, 174, 236);
		palScreen[0x35] = new Color(236, 174, 212);
		palScreen[0x36] = new Color(236, 180, 176);
		palScreen[0x37] = new Color(228, 196, 144);
		palScreen[0x38] = new Color(204, 210, 120);
		palScreen[0x39] = new Color(180, 222, 120);
		palScreen[0x3A] = new Color(168, 226, 144);
		palScreen[0x3B] = new Color(152, 226, 180);
		palScreen[0x3C] = new Color(160, 214, 228);
		palScreen[0x3D] = new Color(160, 162, 160);
		palScreen[0x3E] = new Color(0, 0, 0);
		palScreen[0x3F] = new Color(0, 0, 0);
	}
	
	public void connectCartridge(Cartridge cart) {
		this.cart = cart;
	}
	
	public void clock() {
		if (scanline == 0 && cycle == 0)
			cycle = 1;
		if (scanline == -1 && cycle == 1)
			status = setRegister(status,(byte)0x80,false);
		if (scanline >= 241 && scanline < 261)
			if (scanline == 241 && cycle == 1)
				status = setRegister(status,(byte)0x80,true);
		sprScreen.setPixel(cycle-1, scanline, palScreen[rand.nextBoolean()?0x3F:0x30]);
		cycle++;
		if(cycle>=341) {
			cycle = 0;
			scanline++;
			if(scanline>=261) {
				scanline = -1;
				frame_complete = true;
			}
		}
	}
	
	public Sprite getScreen() {
		return sprScreen;
	}
	
	public Sprite getNameTable(int i) {
		return sprNameTable[i];
	}
	
	public Sprite getPatternTable(int i, byte palette) {
		for(int nTileY = 0; nTileY < 16; nTileY++) {
			for(int nTileX = 0; nTileX < 16; nTileX++) {
				int nOffset = nTileY*256+nTileX*16;
				for(int row = 0; row < 8; row++) {
					byte tile_lsb = ppuRead((short)(i*0x1000+nOffset+row));
					byte tile_msb = ppuRead((short)(i*0x1000+nOffset+row+0x0008));
					for(int col = 0; col < 8; col++) {
						byte pixel = (byte)((tile_lsb&0x01)+(tile_msb&0x01));
						tile_lsb>>>=1;
						tile_msb>>>=1;
						sprPatternTable[i].setPixel(nTileX*8+(7-col), nTileY*8+row,
								getColourFromPaletteRam(palette, pixel));
					}	
				}		
			}
		}		
		return sprPatternTable[i];
	}
	
	public Color getColourFromPaletteRam(byte palette, byte pixel) {
		return palScreen[ppuRead((short) (0x3F00 + (palette << 2) + pixel)) & 0x3F];
	}

	public void cpuWrite(short saddr, byte data) {
		int addr = saddr&0xFFFF;
		switch(addr) {
		case 0: // Control
			break;
		case 1: // Mask
			break;
		case 2: // Status
			break;
		case 3: // OAM Address
			break;
		case 4: // OAM Data
			break;
		case 5: // Scroll
			break;
		case 6: // PPU Address
			if(address_latch==0) {
				ppu_address = (short) ((ppu_address&0x00FF)|((data&0xFF)<<8));
				address_latch = 1;
			} else {
				ppu_address = (short) ((ppu_address&0xFF00)|data&0xFF);
				address_latch = 0;
			}
			break;
		case 7: // PPU Data
			ppuWrite(ppu_address,data);
			break;
		}
	}
	
	public byte cpuRead(short saddr, boolean rOnly) {
		int addr = saddr&0xFFFF;
		byte data = 0x00;
		if(rOnly) {
			switch(addr) {
			case 0: // Control
				data = control;
				break;
			case 1: // Mask
				data = mask;
				break;
			case 2: // Status
				data = status;
				break;
			case 3: // OAM Address
				break;
			case 4: // OAM Data
				break;
			case 5: // Scroll
				break;
			case 6: // PPU Address
				break;
			case 7: // PPU Data
				break;
			}
		} else {
			switch(addr) {
			case 0: // Control
				break;
			case 1: // Mask
				break;
			case 2: // Status
				data = (byte) (((status&0xFF)&0xE0)|((ppu_data_buffer&0xFF)&0x1F));
				status = setRegister(status,(byte)0x80,false);
				address_latch = 0;
				break;
			case 3: // OAM Address
				break;
			case 4: // OAM Data
				break;
			case 5: // Scroll
				break;
			case 6: // PPU Address
				break;
			case 7: // PPU Data
				data = ppu_data_buffer;
				ppu_data_buffer = ppuRead(ppu_address);
				if(ppu_address>0x3F00) data = ppu_data_buffer;
				break;
			}
		}
		return data;
	}
	
	public void ppuWrite(short saddr, byte data) {
		saddr&=0x3FFF;
		int addr = saddr&0xFFFF;
		if(cart.ppuWrite(saddr, data)) {
			
		} else if(addr>=0x0000&&addr<=0x1FFF) {
			tblPattern[(addr&0x1000)>>12][addr&0x0FFF] = data;
		} else if(addr>=0x2000&&addr<=0x3EFF) {
			
		} else if(addr>=0x3F00&&addr<=0x3FFF) {
			addr&=0x001F;
			if(addr==0x10) addr = 0x0;
			if(addr==0x14) addr = 0x4;
			if(addr==0x18) addr = 0x8;
			if(addr==0x1C) addr = 0xC;
			tblPalette[addr] = data;
		}
	}
	
	public byte ppuRead(short saddr) {
		saddr&=0x3FFF;
		int addr = saddr&0xFFFF;
		byte data = 0x00;
		SimpleEntry<Boolean, Byte> cartr = cart.ppuRead(saddr);
		if(cartr.getKey()) {
			data = cartr.getValue();
		} else if(addr>=0x0000&&addr<=0x1FFF) {
			data = tblPattern[(addr&0x1000)>>12][addr&0x0FFF];
		} else if(addr>=0x2000&&addr<=0x3EFF) {
			
		} else if(addr>=0x3F00&&addr<=0x3FFF) {
			addr&=0x001F;
			if(addr==0x0010) addr = 0x0000;
			if(addr==0x0014) addr = 0x0004;
			if(addr==0x0018) addr = 0x0008;
			if(addr==0x001C) addr = 0x000C;
			data = tblPalette[addr];
		}
		return data;
	}

	private byte getRegister(byte reg, byte bit) {
		return (byte) (((reg&bit)!=0)?1:0);
	}
	
	private byte setRegister(byte creg, byte bit, boolean val) {
		byte reg = creg;
		reg&=~bit;
		if(val) reg|=bit;
		return reg;
	}

}
