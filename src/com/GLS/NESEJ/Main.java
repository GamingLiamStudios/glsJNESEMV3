//Code By David Barr, aka javidx9, ©OneLoneCoder 2019
//Ported By GamingLiamStudios, ©GLS 2020
package com.GLS.NESEJ;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.HashMap;

import javax.swing.JFrame;

public class Main extends Canvas implements KeyListener{

	private static final long serialVersionUID = 3861941072675527890L;
	private static final int W = 800, H = 520, PW = 1, PH = 1;
	
	boolean running = false;
	boolean bEmulationRun = false;
	
	Sprite fontSprite;
	
	byte nSelectedPalette = 0x00;
	
	double fElapsedTime = 0, fResidualTime = 0;
	
	public Bus nes;
	public Cartridge cart;
	public HashMap<Integer, String> mapAsm;

	public static void main(String[] args) {
		new Main();
	}
	
	public void init() {
		JFrame frame = new JFrame();
		frame.setTitle("NES Emulator By javidx9, ©OLC 2019. Ported By ©GLS 2020");
		frame.setSize(W,H);
		frame.setResizable(false);
		frame.add(this);
		frame.addKeyListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		nes = new Bus();
		ConstructFontSheet();
	}
	
	public void OnUserCreate() {
		cart = new Cartridge("nestest.nes");
		if(cart.imageValid) nes.insertCartridge(cart);
		else System.err.println("Invalid Cartridge File!");
	}
	
	public Main() {
		init();
		OnUserCreate();
		mapAsm = nes.cpu.disassemble(0x0000, 0xFFFF);
		nes.reset();
		System.out.println("Starting!");
		long last = System.nanoTime();
		while(true) {
			OnUserUpdate();
			fElapsedTime = (System.nanoTime()-last)/1000000000d;
		}
	}
	
	void OnUserUpdate() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.setColor(new Color(0,0,139));
		g.fillRect(0,0,W,H);
		if (bEmulationRun) {
			if (fResidualTime > 0.0f)
				fResidualTime -= fElapsedTime;
			else {
				fResidualTime += (1.0f / 60.0f) - fElapsedTime;
				do { nes.clock(); } while (!nes.ppu.frame_complete);
				nes.ppu.frame_complete = false;
			}
		}
		drawCpu(516, 2,g);
		drawCode(516, 72, 26,g);
		final int nSwatchSize = 6;
		for(int p = 0; p < 8; p++)
			for(int s = 0; s < 4; s++) {
				g.setColor(nes.ppu.getColourFromPaletteRam((byte)p, (byte)s));
				g.fillRect(516+p*(nSwatchSize*5)+s*nSwatchSize, 340, nSwatchSize, nSwatchSize);
			}
		g.setColor(Color.WHITE);
		g.drawRect(516+nSelectedPalette*(nSwatchSize*5)-1, 339, nSwatchSize*4, nSwatchSize);
		drawSprite(516,348,nes.ppu.getPatternTable(0, nSelectedPalette),1,g);
		drawSprite(648,348,nes.ppu.getPatternTable(1, nSelectedPalette),1,g);
		drawSprite(0, 0, nes.ppu.getScreen(), 2, g);
		g.dispose();
		bs.show();
	}
	
	void drawRam(int x, int y, int nAddr, int nRows, int nColumns, Graphics g) {
		int nRamX = x, nRamY = y;
		for (int row = 0; row < nRows; row++)
		{
			String sOffset = "$" + hex(nAddr, 4) + ":";
			for (int col = 0; col < nColumns; col++) {
				sOffset += " " + hex(nes.cpuRead((short)nAddr, true), 2);
				nAddr += 1;
			}
			drawString(nRamX, nRamY, sOffset,Color.WHITE,1,g);
			nRamY += 10;
		}
	}

	void drawCpu(int x, int y, Graphics g) {
		String status = "STATUS: ";
		drawString(x , y , "STATUS:", Color.WHITE,1,g);
		drawString(x  + 64, y, "N", (nes.cpu.status & nes.cpu.N)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x  + 80, y , "V", (nes.cpu.status & nes.cpu.V)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x  + 96, y , "-", (nes.cpu.status & nes.cpu.U)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x  + 112, y , "B", (nes.cpu.status & nes.cpu.B)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x  + 128, y , "D", (nes.cpu.status & nes.cpu.D)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x  + 144, y , "I", (nes.cpu.status & nes.cpu.I)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x  + 160, y , "Z", (nes.cpu.status & nes.cpu.Z)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x  + 178, y , "C", (nes.cpu.status & nes.cpu.C)!=0 ? Color.GREEN : Color.RED,1,g);
		drawString(x , y + 10, "PC: $" + hex(nes.cpu.pc&0xFFFF, 4),Color.WHITE,1,g);
		drawString(x , y + 20, "A: $" +  hex(nes.cpu.a&0xFF, 2) + "  [" + (nes.cpu.a&0xFF) + "]",Color.WHITE,1,g);
		drawString(x , y + 30, "X: $" +  hex(nes.cpu.x&0xFF, 2) + "  [" + (nes.cpu.x&0xFF) + "]",Color.WHITE,1,g);
		drawString(x , y + 40, "Y: $" +  hex(nes.cpu.y&0xFF, 2) + "  [" + (nes.cpu.y&0xFF) + "]",Color.WHITE,1,g);
		drawString(x , y + 50, "Stack P: $" + hex(nes.cpu.stkp&0xFF, 4),Color.WHITE,1,g);
	}

	void drawCode(int x, int y, int nLines,Graphics g) {
		int pc = nes.cpu.pc&0xFFFF;
		String it_a = mapAsm.get(pc);
		int nLineY = (nLines >> 1) * 10 + y;
		if(it_a != null) {
			drawString(x,nLineY,it_a+" OP:"+hex(nes.cpuRead((short) (pc), true)&0xFF,2),Color.CYAN,1,g);
		}
		pc++;
		while(nLineY < (nLines*10) + y) {
			nLineY+=10;
			it_a = mapAsm.get(pc++);
			while(it_a == null) it_a = mapAsm.get(pc++);
			drawString(x,nLineY,it_a+" OP:"+hex(nes.cpuRead((short) (pc-1), true)&0xFF,2),Color.WHITE,1,g);
		}
		pc = nes.cpu.pc&0xFFFF;
		it_a = mapAsm.get(pc);
		nLineY = (nLines >> 1) * 10 + y;
		pc--;
		while(nLineY > y) {
			nLineY-=10;
			it_a = mapAsm.get(pc--);
			while(it_a == null) it_a = mapAsm.get(pc--);
			drawString(x,nLineY,it_a+" OP:"+hex(nes.cpuRead((short) (pc+1), true)&0xFF,2),Color.WHITE,1,g);
		}
	}
	
	public void drawSprite(int x, int y, Sprite sprite, int scale, Graphics g) {
		if (sprite == null)
			return;

		if (scale > 1)
		{
			for (int i = 0; i < sprite.getWidth(); i++)
				for (int j = 0; j < sprite.getHeight(); j++)
					for (int is = 0; is < scale; is++)
						for (int js = 0; js < scale; js++)
							draw(x + (i*scale) + is, y + (j*scale) + js, sprite.getPixel(i, j), g);
		}
		else
		{
			for (int i = 0; i < sprite.getWidth(); i++)
				for (int j = 0; j < sprite.getHeight(); j++)
					draw(x + i, y + j, sprite.getPixel(i, j),g);
		}
	}
	
	public void drawString(int x, int y, String sText, Color col, int scale, Graphics g) {
		int sx = 0;
		int sy = 0;
		for (char c : sText.toCharArray()) {
			if (c == '\n') {
				sx = 0; sy += 8 * scale;
			} else {
				int ox = (c - 32) % 16;
				int oy = (c - 32) / 16;

					for (int i = 0; i < 8; i++)
						for (int j = 0; j < 8; j++)
							if (fontSprite.getPixel(i + ox * 8, j + oy * 8).getRed() > 0)
								for (int is = 0; is < scale; is++)
									for (int js = 0; js < scale; js++)
										draw(x + sx + (i*scale) + is, y + sy + (j*scale) + js, col, g);
				sx += 8 * scale;
			}
		}
	}
	
	void draw(int x, int y, Color c, Graphics g) {
		g.setColor(c);
		g.fillRect(x*PW, y*PH, PW, PH);
	}
	
	void ConstructFontSheet() {
		String data = "";
		data += "?Q`0001oOch0o01o@F40o0<AGD4090LAGD<090@A7ch0?00O7Q`0600>00000000";
		data += "O000000nOT0063Qo4d8>?7a14Gno94AA4gno94AaOT0>o3`oO400o7QN00000400";
		data += "Of80001oOg<7O7moBGT7O7lABET024@aBEd714AiOdl717a_=TH013Q>00000000";
		data += "720D000V?V5oB3Q_HdUoE7a9@DdDE4A9@DmoE4A;Hg]oM4Aj8S4D84@`00000000";
		data += "OaPT1000Oa`^13P1@AI[?g`1@A=[OdAoHgljA4Ao?WlBA7l1710007l100000000";
		data += "ObM6000oOfMV?3QoBDD`O7a0BDDH@5A0BDD<@5A0BGeVO5ao@CQR?5Po00000000";
		data += "Oc``000?Ogij70PO2D]??0Ph2DUM@7i`2DTg@7lh2GUj?0TO0C1870T?00000000";
		data += "70<4001o?P<7?1QoHg43O;`h@GT0@:@LB@d0>:@hN@L0@?aoN@<0O7ao0000?000";
		data += "OcH0001SOglLA7mg24TnK7ln24US>0PL24U140PnOgl0>7QgOcH0K71S0000A000";
		data += "00H00000@Dm1S007@DUSg00?OdTnH7YhOfTL<7Yh@Cl0700?@Ah0300700000000";
		data += "<008001QL00ZA41a@6HnI<1i@FHLM81M@@0LG81?O`0nC?Y7?`0ZA7Y300080000";
		data += "O`082000Oh0827mo6>Hn?Wmo?6HnMb11MP08@C11H`08@FP0@@0004@000000000";
		data += "00P00001Oab00003OcKP0006@6=PMgl<@440MglH@000000`@000001P00000000";
		data += "Ob@8@@00Ob@8@Ga13R@8Mga172@8?PAo3R@827QoOb@820@0O`0007`0000007P0";
		data += "O`000P08Od400g`<3V=P0G`673IP0`@3>1`00P@6O`P00g`<O`000GP800000000";
		data += "?P9PL020O`<`N3R0@E4HC7b0@ET<ATB0@@l6C4B0O`H3N7b0?P01L3R000000020";

		char[] cData = data.toCharArray();
		fontSprite = new Sprite(128, 48);
		int px = 0, py = 0;
		for (int b = 0; b < 1024; b += 4) {
			int sym1 = (int)cData[b + 0] - 48;
			int sym2 = (int)cData[b + 1] - 48;
			int sym3 = (int)cData[b + 2] - 48;
			int sym4 = (int)cData[b + 3] - 48;
			int r = sym1 << 18 | sym2 << 12 | sym3 << 6 | sym4;

			for (int i = 0; i < 24; i++)
			{
				int k = (r & (1 << i))!=0 ? 255 : 0;
				fontSprite.setPixel(px, py, new Color(k, k, k, k));
				if (++py == 48) { px++; py = 0; }
			}
		}
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

	public void keyTyped(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		
		if(!bEmulationRun) {
			if (e.getKeyCode()==KeyEvent.VK_C) {
				do { nes.clock(); } while (!nes.cpu.complete());
				do { nes.clock(); } while (nes.cpu.complete());
			}
			if (e.getKeyCode()==KeyEvent.VK_F) {
				do { nes.clock(); } while (!nes.ppu.frame_complete);
				do { nes.clock(); } while (!nes.cpu.complete());
				nes.ppu.frame_complete = false;
			}
		}


		if (e.getKeyCode()==KeyEvent.VK_SPACE) bEmulationRun = !bEmulationRun;
		if (e.getKeyCode()==KeyEvent.VK_R) nes.reset();
		if (e.getKeyCode()==KeyEvent.VK_P) {
			nSelectedPalette++;
			nSelectedPalette&=0x07;
		}
	}

	public void keyReleased(KeyEvent e) {
		
	}

}
