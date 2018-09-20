import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class MultiPegToH{
	static int max=100+1;
	static int maxrow=10+1;
	static int maxcol=20+1;
	static int frameH=600;
	static int frameW=900;
	static int timeDelay=20;
	
	int xDiskAll[]=new int[max];
	int xDiskCurrent[]=new int [max];
	int yDiskAll[]= new int[max];
	int yDiskCurrent[]=new int[max];
	
	int diskWidth[]=new int[max];
	static int diskHeight= 15;
	
	
	int pegx[] = new int[max];
	int pegy = 150;
	int pegHeight= 350;
	int pegWidth=10;
	
	ArrayList <Integer> states [] = new ArrayList [max];

	
	int pegstatus[]=new int[max];
	int disks;
	int pegs;
	int kmax;
	int na;
	
	int N[][]=new int[maxrow][maxcol];
	
	static int movescnt=0;
	
	class MovesList{
		public int disknumber;
		public int topegnumber;
		public int toheight;
		
		MovesList(int a, int b, int c){
			disknumber=a;
			topegnumber=b;
			toheight=c;
		}
	}
	static int maxmoves = 5000;
	static MovesList [] mvlst= new MovesList[maxmoves];
	
	
	void read(){
		Scanner read=new Scanner(System.in);
		
		System.out.println("Enter the number of disks: ");
		disks=read.nextInt();
		
		
		do{
			System.out.println("Enter the number of pegs(>=3): ");
			pegs=read.nextInt();
		}while(pegs<3);
		
		read.close();
		
	}
	void setTowers(){
		pegstatus[1]=disks;
		for(int i=0; i< max; i++){
			states[i]=new ArrayList <Integer> ();
		}
		for(int i=0; i < disks; i++){
			states[0].add(i);
		}
	}
	
	
	void makeN(){
		//base conditions
		for(int i=0;i<maxrow;i++){
			N[i][3]=1;
		}
		for(int i=0;i<maxcol;i++){
			N[0][i]=1;
		}
		//calculations
		for(int i=1;i<maxrow;i++){
			for(int j=4;j<maxcol;j++){
				N[i][j]=N[i-1][j]+N[i][j-1];
			}
		}
	}
	
	void printN(){
		for(int i=0;i<maxrow;i++){
			for(int j=0;j<maxcol;j++){
				System.out.print(N[i][j]+" ");
			}
			System.out.println();
		}
		
	}
	
	int optimalPartition(int n, int p){
//		System.out.printf("%d %d\n", n,p);
		for(int i=0;i<maxrow;i++){
			if(N[i][p+1]>n){
				kmax=i;
				break;
			}
		}
		System.out.println(kmax+"");
		na=n-N[kmax-1][p+1];
		int a=(kmax<2)?0:N[kmax-2][p+1];
		int c=(na<N[kmax][p-1])? na:N[kmax][p-1];
		int b=n-(N[kmax-1][p]+c);
		int n1=(a>b)? a:b;
		
		System.out.println(a + " "+ b+" done "+n1);
//		System.exit(0);
		return n1;
	}
	
	

	void moves(int n,int p,int src, int dest){
		if(n>0){
			
			if(n==1){
				int d = (int) states[src-1].get(states[src-1].size()-1);
				states[src-1].remove(states[src-1].size()-1);
				states[dest-1].add(d);
				System.out.printf("move %d from %d to %d\n", d, src, dest);
				
				mvlst[movescnt]=new MovesList(d, dest-1, states[dest-1].size()-1);
				movescnt++;
				
				return;
			}
			
			
			
			int n1=optimalPartition(n,p);
			System.out.println(n+ " "+ p + " " + n1);
			
			int optdisk = (int) states[src-1].get(n-n1);
			
			for(int i=0; i< pegs; i++){
				if(i+1==dest) continue;
				
				int top;
				if(states[i].isEmpty()){
					top = -1;
				}
				else{
					 top = (int) states[i].get(states[i].size()-1);
				}
				
				if(top < optdisk){
					moves(n1,p,src,i+1);
					
					
					if(p>3) moves(n-n1,p-1,src,dest);
					else{
						int d = (int) states[src-1].get(states[src-1].size()-1);
						states[src-1].remove(states[src-1].size()-1);
						states[dest-1].add(d);
						mvlst[movescnt]=new MovesList(d, dest-1, states[dest-1].size()-1);
						movescnt++;
						System.out.printf("move %d from %d to %d\n", d, src, dest);
					}
					moves(n1,p,i+1,dest);
					return;
				}
			}
			
			
		}
		
	}
	
	
	
	//////////////////////////////////////////////////////////////
	///////////////// GRAPHICS
	
	JFrame frame= new JFrame("Multi Peg TOH");
	Rectangle [] diskrec= new Rectangle[max];
	JComponent board;
	
	public void initial(int n, int p){
		
		pegx[0] = 90;
		int difpeg= (frameW-90)/p;
		for(int i=1; i< p; i++){
			pegx[i]= pegx[i-1]+difpeg;
		}
		xDiskAll[0]=20;
		xDiskCurrent[0]=xDiskAll[0];
		yDiskAll[0]= 460;
		yDiskCurrent[0]=yDiskAll[0];
		

		
		for(int i=1;i<n;i++){
			xDiskAll[i]= xDiskAll[i-1]+difpeg;
			xDiskCurrent[i]= xDiskCurrent[i-1]+8;
		}

		
		
		
		
		for(int i=1;i<n;i++){
			yDiskAll[i]=yDiskAll[i-1]-diskHeight-15;
			yDiskCurrent[i]=yDiskAll[i];
		}
		
		for(int i=0; i < n; i++){
			diskWidth[i]= (pegx[0]-xDiskCurrent[i])*2+pegWidth;
		}	
		
		////////////////////////////
		Rectangle [] pegrec = new Rectangle[max];
		
		for(int i=0;i<p;i++){
			pegrec[i]=new Rectangle(pegx[i],pegy, pegWidth, pegHeight);
		}
		
		for(int i=0; i <n;i++){
			diskrec[i]= new Rectangle(xDiskCurrent[i], yDiskCurrent[i], diskWidth[i], diskHeight);
		}

		
		board = new JComponent(){
			public void paint(Graphics g){
				Graphics2D graphSettings= (Graphics2D) g;
				graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
 
 
				graphSettings.setColor(Color.black);
 
				graphSettings.fill(new Rectangle2D.Float(0,0,1600,1600));
 
				///////////////////////////////////////////////////////////////////
				//////////// COLORS
				Color[] cl= new Color[3];
				cl[0]= Color.orange;
				cl[1]= Color.gray;
				cl[2]=Color.blue;
				//////////////////////////////////////////////////////////////////////
				graphSettings.setColor(cl[0]);
 
				graphSettings.fill(new Rectangle2D.Float(0,0,1600,1600));
 
				graphSettings.setColor(cl[1]);
				for(int i=0; i < p; i++){
					graphSettings.fill(pegrec[i]);
				} 
				graphSettings.fillRect(0, 480, 900, 80);

				graphSettings.setColor(cl[2]);
				
				for(int i=0;i<n;i++){
					graphSettings.fill(diskrec[i]);
				}
			}
		};
 
		frame.setSize(frameW, frameH);
		frame.add(board, BorderLayout.CENTER);
		frame.setVisible(true);
 
//		int x= 50;
//		while(x<90){
//			x++;
//			board.repaint();
//			rec.setLocation(x, x);
//			try{
//				Thread.sleep(100);
// 
//			}catch(Exception e){
// 
//			}
//		}				

		
		for(int i=0;i<movescnt;i++){

			animate(mvlst[i].disknumber, mvlst[i].topegnumber, mvlst[i].toheight);
			
		}
	}
	
	////////////////////////////////////////////////////
	//////////////////////// ANIMATE
	

	void moveUp(int d){
		while(yDiskCurrent[d] > 50){
			yDiskCurrent[d] -= 5;
			for(int j=0; j <disks;j++){
				diskrec[j]= new Rectangle(xDiskCurrent[j], yDiskCurrent[j], diskWidth[j], diskHeight);
			}
			board.repaint();
			try{
				Thread.currentThread().sleep(timeDelay);
				
			}catch(Exception e){
				
			}
		}
	}
	void moveHorizontal(int d, int xdest){
		while(Math.abs(xDiskCurrent[d]- xdest) > 5){
			xDiskCurrent[d] = (xDiskCurrent[d] > xdest)? (xDiskCurrent[d]-5):(xDiskCurrent[d]+5);
			for(int j=0; j <disks;j++){
				diskrec[j]= new Rectangle(xDiskCurrent[j], yDiskCurrent[j], diskWidth[j], diskHeight);
			}
			board.repaint();
			try{
				Thread.currentThread().sleep(timeDelay);
				
			}catch(Exception e){
				
			}
		}
	}
	
	void moveVertical(int d, int ydest){
		while(Math.abs(yDiskCurrent[d]- ydest) > 5){
			yDiskCurrent[d] = yDiskCurrent[d]+5;
			for(int j=0; j <disks;j++){
				diskrec[j]= new Rectangle(xDiskCurrent[j], yDiskCurrent[j], diskWidth[j], diskHeight);
			}
			board.repaint();
			try{
				Thread.currentThread().sleep(timeDelay);
				
			}catch(Exception e){
				
			}			
		}
	}
	public void animate(int disknumber, int topegnumber, int toheight){
		int xdest = xDiskAll[topegnumber]+8*disknumber;
		int ydest =yDiskAll[toheight];
		
		moveUp(disknumber);
		moveHorizontal(disknumber, xdest);
		moveVertical(disknumber, ydest);
	}
	
	public void studentId(){
		System.out.println("*******************************");
		System.out.println("Name: A. S. M. Ahsan-Ul-Haque");
		System.out.println("Student ID: 1205021");
		System.out.println("*******************************");
	}
	
	

	public static void main(String [] args){
		MultiPegToH towers=new MultiPegToH();
		towers.makeN();
		//towers.printN();
		
		towers.studentId();
		towers.read();
		towers.setTowers();
		towers.moves(towers.disks,towers.pegs,1,towers.pegs);
		System.out.println("Total number of moves: "+towers.movescnt);
		
		towers.initial(towers.disks, towers.pegs);
		
		for(int i=0; i< movescnt; i++){
			System.out.printf("\n%d %d %d\n", mvlst[i].disknumber, mvlst[i].topegnumber, mvlst[i].toheight);
		}
		
	}
	
}
