import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class roguelike extends PApplet {


space[][] level;

//////global counter for bresenham
int bresCounter=0;

//global for what part of game we on
int gamecounter=0;


//WARNING col=row, row=col
int row=40;
int col=40;
int texty=15;
int textxplus=13;
int textyplus=13;
int textx=15;
//so you dont spam the keys
boolean keyreset=true;

//if its true turn false;
boolean turn;
boolean keyHolder;


//setup
public void setup(){
size(800,800);

//creation();

}

//draw
public void draw(){
background(255);
//background(0);

if(gamecounter==1){
updateall();

}
menu();


}
//create level
public void createSpace(){


	for(int i =0;i<row;i++){
    	for(int r=0;r<col;r++){

    		level[i][r]=new space(i,r);        
    	}
	}




}



//class for grid container
class space{
//if in fov
boolean visible;
//if seen
boolean seen;
//other
int myX,myY;
boolean groundcheck;
String groundtype;
Object ground;
boolean solidcheck;
String solidtype;
Object solid;
///pathfinding help
boolean checked;


public space(int x, int y){
myX=x;
myY=y;


}


}


//class for everything on board
class exist{
	int myX, myY;
	String symbol;


	public void drawself(){
		fill(0);
		//fill(255);
		textSize(20);
		text(symbol,myX*textx+textxplus,myY*texty+textyplus);
	}
	public void update(){

		if(level[myX][myY].visible==true){
		this.drawself();
	}
	}
}



//parent for all ground objects
class ground extends exist{


	
}
//grass
	class grass extends ground{

		public grass(int x, int y){
			myX=x;
			myY=y;
			symbol=",";

		}
		public void drawself(){
			fill(0,153,0);
			textSize(20);
			text(symbol,myX*textx+textxplus,myY*texty+textyplus);
		}
	
	}

//for chars and walls
class solid extends exist{
boolean myTurn;



}
	class character extends solid{
		int newX, newY;
		boolean moving;
		int myFOV=20;
		//actual stats
		int hp=1;
		int attack=1;
		int defense=0;

		public void move(){

			if(moving==true){

				if(isValid(newX,newY)){
					//attacking others
					//make sure not self
					//if(newX!=myX&&newY!=myY){
						if(level[newX][newY].solidcheck==true){

							     
							if(level[newX][newY].solidtype=="player"){
								((player)level[newX][newY].solid).hp-=attack;

							} 
							if(level[newX][newY].solidtype=="anteater"){
								((anteater)level[newX][newY].solid).hp-=attack;

							} 
							


						}
				//	}



					if(level[newX][newY].solidcheck==false){

						String typeHolder=level[myX][myY].solidtype;
						level[myX][myY].solidcheck=false;     
						myX=newX;
						myY=newY;
						level[myX][myY].solidcheck=true;     
						level[myX][myY].solidtype=typeHolder; 
						level[myX][myY].solid=this;
						myTurn=false;


					}
					
				}	
				moving=false;
			}


		}
		public void update(){

			if(level[myX][myY].visible==true){
				this.drawself();
			}
			
			if(myTurn==true){
				this.ai();
				//System.out.println(newX+" "+newY);
				this.move();
				myTurn=false;
				
			}
			checkDeath();

		}
		public void ai(){


		}
		public void checkDeath(){

			if(hp<=0){
				level[myX][myY].solidcheck=false;


			}
		}


	}
		class racoon extends character{

			public racoon(int x, int y){
				myX=x;
				myY=y;
				symbol="r";
			}

			public void ai(){
				moving=true;
				int randNum=(int)(Math.random()*4);
				
					if(randNum==0){
						newX=myX-1;
						newY=myY;

					}
					if(randNum==1){
						newX=myX+1;
						newY=myY;
						
					}
					if(randNum==2){
						newX=myX;
						newY=myY-1;
						
					}
					if(randNum==3){
						newX=myX;
						newY=myY+1;
						
					}

				}


			


		}
		class dino extends character{
			int nextx=-1;
			int nexty=-1;
			public dino(int x, int y){
				myX=x;
				myY=y;
				symbol="d";
			}
			public void ai(){
				moving=true;
				int targetx=myX;
				int targety=myY;
				if( (Math.abs(targetx-myX)==1&&targety==myY)||
					(Math.abs(targety-myY)==1&&targetx==myX) ){
					newX=targetx;
					newY=targety;
					nextx=-1;

				}

				else if(nextx>=0){
					newX=nextx;
					newY=nexty;
					nextx=-1;
				}
				else{
					
						for(int i =0;i<row;i++){
		    				for(int r=0;r<col;r++){
		    					if(level[i][r].solidcheck==true){
		    						if(level[i][r].solidtype=="player"){
	    								targetx=((player)level[i][r].solid).myX;
	    								targety=((player)level[i][r].solid).myY;
	    				
	    							}
		    					}    
		    		  
		    				}
						}
					
					int[] currentpos={myX,myY};	
					node startoff=new node(currentpos);
					int[] pathfound=bfs(startoff,targetx,targety);
					if(pathfound.length==4){
						newX=pathfound[0];
						newY=pathfound[1];
						nextx=pathfound[2];
						nexty=pathfound[3];
					
					}
					//uh hopefully solves some pathfinding bugs
					if(targety<myY){
						int gen=(int)(Math.random()*2);
						if(gen==0){

							newX=myX;
							newY=myY-1;
							nextx=-1;
						}

					}
				}

			}




		}
		class anteater extends character{
			public anteater(int x, int y){
				myX=x;
				myY=y;
				symbol="a";
				hp=5;
				attack=1;
			}
			public void ai(){
				moving=true;
				int targetx=myX;
				int targety=myY;
				

				
				
					
				for(int i =0;i<row;i++){
		    		for(int r=0;r<col;r++){
		    			if(level[i][r].solidcheck==true){
		    				if(level[i][r].solidtype=="player"){
	    								targetx=((player)level[i][r].solid).myX;
	    								targety=((player)level[i][r].solid).myY;
	    				
	    					}
		    			}    
		    		  
		    		}
				}
					
					int[] currentpos={myX,myY};	
					node startoff=new node(currentpos);
					int[] pathfound=astar(startoff,targetx,targety);
					if(pathfound.length==4){
						newX=pathfound[0];
						newY=pathfound[1];
						
					
					}
					//soo attacks when next to
					if((abs(targetx-myX)==1&&targety==myY)||(abs(targety-myY)==1&&targetx==myX)){

						newX=targetx;
						newY=targety;
					}

			}
		}
		class player extends character{

				int myUp;
				int myDown;
				int myLeft;
				int myRight;

				//actual stats
				

			public player(int x, int y,int up,int down,int left,int right){
				myX=x;
				myY=y;
				symbol="@";
				myUp=up;
				myDown=down;
				myLeft=left;
				myRight=right;
				hp=10;
				attack=1;
			}
			public void update(){

				this.drawself();
				this.keyReader();
				if(myTurn==true){
					this.move();
					//regenerate health
					//this.hp++;
				}
				checkDeath();

			}

			public void drawself(){
				fill(0);
				textSize(20);
				text(symbol,myX*textx+textxplus,myY*texty+textyplus);


				//translate halth to flavor
				textSize(15);
				if(hp>9){
					text("you feel on top of the world",590,100);
				}
				else if(hp>3){
					text("you feel fine",590,100);

				}
				else if(hp>2){
					text("you are injured",590,100);

				}
				else if (hp>0){
					text("you are on death's doorstep",590,100);

				}
				textSize(20);
			}

			public void keyReader(){
				if(keyPressed&&keyreset){

					if(keyCode==myUp){

						this.newX=myX;
						this.newY=myY-1;
						
					}
					if(keyCode==myDown){
						this.newX=myX;
						this.newY=myY+1;
					}
					if(keyCode==myLeft){
						this.newX=myX-1;
						this.newY=myY;
					}
					if(keyCode==myRight){
						this.newX=myX+1;
						this.newY=myY;
					}
					moving=true;
						
					keyreset=false;

				}


			}



		}

	class wall extends solid{





	}
		class stonewall extends wall{
			public stonewall(int x, int y){

				myX=x;
				myY=y;
				symbol="#";
			}

		}



public void createGrass(){

	for(int i =0;i<row;i++){

    	for(int r=0;r<col;r++){
    		level[i][r].groundcheck=true;     
    		level[i][r].groundtype="grass"; 
    		level[i][r].ground= new grass(i,r);  
    		
    	}
	}
}

public void createStonewall(){
		for(int i =0;i<row;i++){
	    	for(int r=0;r<col;r++){
	    		int chance=(int)(Math.random()*7);
	    		if(chance==6){

	    		level[i][r].solidcheck=true;     
	    		level[i][r].solidtype="stonewall"; 
	    		level[i][r].solid= new stonewall(i,r);  
	    		}
	    	}
		}


}
public void createRacoon(){
			for(int i =0;i<row;i++){
		    	for(int r=0;r<col;r++){
		    		int chance=(int)(Math.random()*200);
		    		if(chance==6){

		    		level[i][r].solidcheck=true;     
		    		level[i][r].solidtype="racoon"; 
		    		level[i][r].solid= new racoon(i,r);  
		    		}
		    	}
			}
}
public void createDino(){
			for(int i =0;i<row;i++){
		    	for(int r=0;r<col;r++){
		    		int chance=(int)(Math.random()*200);
		    		if(chance==6){

		    		level[i][r].solidcheck=true;     
		    		level[i][r].solidtype="dino"; 
		    		level[i][r].solid= new dino(i,r);  
		    		}
		    	}
			}
			// int whev=15;
			// level[whev][whev].solidcheck=true;     
		 // 	level[whev][whev].solidtype="dino"; 
		 // 	level[whev][whev].solid= new dino(whev,whev);  
}
public void createAnteater(){
			for(int i =0;i<row;i++){
		    	for(int r=0;r<col;r++){
		    		int chance=(int)(Math.random()*200);
		    		if(chance==6){

		    		level[i][r].solidcheck=true;     
		    		level[i][r].solidtype="anteater"; 
		    		level[i][r].solid= new anteater(i,r);  
		    		}
		    	}
			}
			// int whev=15;
			// level[whev][whev].solidcheck=true;     
		 // 	level[whev][whev].solidtype="dino"; 
		 // 	level[whev][whev].solid= new dino(whev,whev);  
}
public void createPlayer(){
	int spawnx=col/2;
	int spawny=row/2;

	level[spawnx][spawny].solidcheck=true;     
	level[spawnx][spawny].solidtype="player"; 
	level[spawnx][spawny].solid= new player(spawnx,spawny,38,40,37,39);
	((player)level[spawnx][spawny].solid).newX=spawnx;
	((player)level[spawnx][spawny].solid).newY=spawny;
}
////////////////////////////////////////////////////////////////////////
public void updateall(){
	//obscure turn sorter with player
	if(keyHolder==false){


	
		if(keyPressed){
			turn=true;
			keyHolder=true;
		}
	}	
	//turn good
	if(turn==true){
		for(int i =0;i<row;i++){
	    	for(int r=0;r<col;r++){
	    		if(level[i][r].solidcheck==true){
	    			((solid)level[i][r].solid).myTurn=true;

	    		}    
	    		  
	    	}
		}
	}	
//////////////////////////////////VISION
///////////////////////////////////////
//first set all to unvisible
for(int i =0;i<row;i++){
    	for(int r=0;r<col;r++){
	    		level[i][r].visible=false;
	    }
}
//if player?
int yesorno=0;
for(int i =0;i<row;i++){
    	for(int r=0;r<col;r++){
	    		if(level[i][r].solidcheck==true){
	    			if(level[i][r].solidtype=="player"){
	    				yesorno=1;
	    			}
	    		}
	    	}
	    }
if(yesorno==0){

	gamecounter=2;
}

//then calculate LOS
for(int i =0;i<row;i++){
    	for(int r=0;r<col;r++){
	    		if(level[i][r].solidcheck==true){
	    			if(level[i][r].solidtype=="player"){
	    				
	    				int fov=((player)level[i][r].solid).myFOV/2;
	    				int x=((player)level[i][r].solid).myX;
	    				int y=((player)level[i][r].solid).myY;
	    					//getting fov
	    				for(int p =x-fov;p<x+fov;p++){
    						for(int q=y-fov;q<y+fov;q++){
    							if(isValid(p,q)){
    								//then checking los
    								
		    							int[][] shadow = bresenham(i,r,p,q);
		    							

		    							int clear=0;
										//if block is visible
		    							boolean front=false;

		    								for(int n =0;n<shadow.length;n++){
    												int shadowx=shadow[n][0];
    												int shadowy=shadow[n][1];
    												
    												int endarray=0;
    												if(shadowx==0&&shadowy==0){
    													endarray=n;

    												}

    													if(level[shadowx][shadowy].solidcheck==true){
		    												if(level[shadowx][shadowy].solidtype=="stonewall"){
		    														//so can see what cause shadow
		    														
		    														clear++;
		    														if(shadowx==p&&shadowy==q){

		    															front=true;

		    														}
		    														
		    														if(clear>2){

		    															break;
		    														}

		    												}
		    											}
		    										

		    								}
		    								if(front==true&&clear<2){
		    										level[p][q].visible=true;
		    									}
		    								if(clear<1){
		    									
		    									level[p][q].visible=true;
		    									
		    								}

		    					}
		    				}
		    			}



	    				
	    			}
	    		}
	    }
}

	//update
	for(int i =0;i<row;i++){
    	for(int r=0;r<col;r++){
    		
	    		if(level[i][r].solidcheck==true){
	    			if(level[i][r].solidtype=="stonewall"){
	    				((stonewall)level[i][r].solid).update();
	    				
	    				}
	    			if(level[i][r].solidtype=="player"){
	    				((player)level[i][r].solid).update();
	    				
	    				}
	    			if(level[i][r].solidtype=="racoon"){
	    				((racoon)level[i][r].solid).update();
	    				
	    				}
	    			if(level[i][r].solidtype=="dino"){
	    				((dino)level[i][r].solid).update();
	    				
	    				}
	    			if(level[i][r].solidtype=="anteater"){
	    				((anteater)level[i][r].solid).update();
	    				
	    				}
    			





    			}
    		


	    		else if(level[i][r].groundcheck==true){
	    				if(level[i][r].groundtype=="grass"){
	    				((grass)level[i][r].ground).update();
	    				
	    				}

	    		}   
    		   
    		  
    	}
	}
//turn flipper
if(turn==true){

	turn =false;
}



}

public boolean isValid(int x,int y){

	if(x<col&&x>-1&&y>-1&&y<row){
		return true;
	}

	return false;
}


public void keyReleased(){

	keyreset=true;
	keyHolder=false;
	if(gamecounter==0||gamecounter==2){
		if(keyCode==82){
	gamecounter=1;
	creation();
}
}
}


class node{
	int[] position={0,0};
	Object myParent;
	boolean hasparent;
	float f,g,h;
	node(int[] coords, Object parent){
		position=coords;
		myParent=parent;
		hasparent=true;
	}
	node(int[] coords){
		position=coords;
		hasparent=false;
		f=0;
		}
}

public int[] bfs(Object start,int goalx,int goaly){
	///bredth first search
	//problem:depends on order of nodes because he may just cahnge his course once he goes 
	//to the next square in a sequence, sending him back to start rip
	int x;
	int y;

	boolean goalcheck=false;
	
	Object goalnode=start;
	Object nextstepnode=start;
	Object secondstep=start;
	for(int i =0;i<row;i++){

    	for(int r=0;r<col;r++){
    		level[i][r].checked=false;
    	}
    }     
	int[] returner = {
		((node)start).position[0], 
		((node)start).position[1],
		((node)start).position[0],
		((node)start).position[1]};
	ArrayList <node> theList= new ArrayList <node>();
	ArrayList <node> thePath= new ArrayList <node>();
	theList.add(((node)start));
	while(theList.size()>0){
		
		node tempNode=theList.get(0);
		theList.remove(0);
		level[tempNode.position[0]][tempNode.position[1]].checked=true;

		y=tempNode.position[1];
		x =tempNode.position[0];


		int where=0;
		if(theList.size()>1){
		 	
			where=theList.size()-1;
			
				    						
		}
			for(int i =y-1;i<y+2;i++){

		    	for(int r=x-1;r<x+2;r++){
		    		//only concerned with certain tiles
		    		if( (r==x-1&&i==y)||
		    			(r==x+1&&i==y)||
		    			(r==x&&i==y+1)||
		    			(r==x&&i==y-1)){


			    				//if checked

			    			if(isValid(r,i)){
			    				if(level[r][i].solidtype!="stonewall"
			    					&&level[r][i].solidtype!="racoon"){
				    				if(level[r][i].checked==false){
				    					level[r][i].checked=true;
				    					int[] newcoord={r,i};
				    					if(newcoord[0]==goalx&&newcoord[1]==goaly){
				    						goalcheck=true;
				    						////maybe finicky for attack
				    						goalnode=tempNode;
				    						break;
				    					}
				    						
				    						theList.add(where,new node(newcoord,tempNode));
				    					


				    				}
								}
							}
				  	}		
				}
			}
	}

	if(goalcheck==true){

		while(((node)goalnode).hasparent==true){
			thePath.add(0,(node)goalnode);
			goalnode=((node)goalnode).myParent;

		}
		if(thePath.size()>0){
			nextstepnode=thePath.get(0);
			if(thePath.size()>1){
				secondstep=thePath.get(1);
			}
			//lol go all the way out then back in
			// for(int i=0;i<thePath.size();i++){

			// 	node debug=thePath.get(i);
			// 	System.out.println(i+" "+((node)debug).position[0]+","+((node)debug).position[1]);
			// }
		}
		returner[0]=((node)nextstepnode).position[0];
		returner[1]=((node)nextstepnode).position[1];
		returner[2]=((node)secondstep).position[0];
		returner[3]=((node)secondstep).position[1];
		
		
	}


	return returner;
}

public int[] astar(Object start,int goalx,int goaly){
	///a star yes
	//problem:depends on order of nodes because he may just cahnge his course once he goes 
	//to the next square in a sequence, sending him back to start rip
	int x;
	int y;

	boolean goalcheck=false;
	
	Object goalnode=start;
	Object nextstepnode=start;
	
	for(int i =0;i<row;i++){

    	for(int r=0;r<col;r++){
    		level[i][r].checked=false;
    	}
    }     
	int[] returner = {
		((node)start).position[0], 
		((node)start).position[1],
		((node)start).position[0],
		((node)start).position[1]};
	ArrayList <node> theList= new ArrayList <node>();
	ArrayList <node> thePath= new ArrayList <node>();
	ArrayList <node> closedList=new ArrayList <node>();
	theList.add(((node)start));
	//f=g+h
	//g actual cost, h heruistic
	while(theList.size()>0){
		//find node with least f
		int lowindex=0;
		for(int i=0;i<theList.size();i++){

			node trial=theList.get(i);
			node jury=theList.get(lowindex);
		 	if(trial.f<jury.f){

		 		lowindex=i;
		 	}
		}


		node tempNode=theList.get(lowindex);
		theList.remove(lowindex);
		level[tempNode.position[0]][tempNode.position[1]].checked=true;

		y=tempNode.position[1];
		x =tempNode.position[0];


		int where=0;
		if(theList.size()>1){
		 	
			where=theList.size()-1;
			
				    						
		}
			for(int i =y-1;i<y+2;i++){

		    	for(int r=x-1;r<x+2;r++){
		    		//only concerned with certain tiles
		    		if( (r==x-1&&i==y)||
		    			(r==x+1&&i==y)||
		    			(r==x&&i==y+1)||
		    			(r==x&&i==y-1)){


			    				//if checked

			    			if(isValid(r,i)){
			    				if(level[r][i].solidtype!="stonewall"
			    					&&level[r][i].solidtype!="racoon"){
				    				if(level[r][i].checked==false){

				    					level[r][i].checked=true;
				    					int[] newcoord={r,i};
				    					if(newcoord[0]==goalx&&newcoord[1]==goaly){
				    						goalcheck=true;
				    						////maybe finicky for attack
				    						goalnode=tempNode;
				    						break;
				    					}

				    					node successor=new node(newcoord,tempNode);
				    						successor.g=tempNode.g+1;
				    						successor.h=sqrt(sq(goalx-r)+sq(goaly-i));
				    						successor.f=successor.g+successor.h;
				    						theList.add(where,successor);
				    					
				    					////////could use some more optimization but it works so w/e

				    				}
								}
							}
				  	}		
				}
			}

			closedList.add(0,tempNode);

	}

	if(goalcheck==true){

		while(((node)goalnode).hasparent==true){
			thePath.add(0,(node)goalnode);
			goalnode=((node)goalnode).myParent;

		}
		if(thePath.size()>0){
			nextstepnode=thePath.get(0);
			
			//lol go all the way out then back in
			// for(int i=0;i<thePath.size();i++){

			// 	node debug=thePath.get(i);
			// 	System.out.println(i+" "+((node)debug).position[0]+","+((node)debug).position[1]);
			// }
		}
		returner[0]=((node)nextstepnode).position[0];
		returner[1]=((node)nextstepnode).position[1];
		
		
		
	}


	return returner;
}

public int[][] bresenham(int x0,int y0,int x1, int y1){
  int[][] returner=new int[col][row];
  int[][] backup ={};
  
  int dx=abs(x1-x0);
  int dy=-abs(y1-y0);
  //tracks how off of pure line it is
  int error=dx+dy;
  int e2;
  ///adjust for diff quadrants if x<x1 true:1 false:-1
  int sx = x0 < x1 ? 1 : -1; 
  int sy = y0 < y1 ? 1 : -1; 


  int y=y0;
  int x=x0;
  int counter=-1;
   for(;;){  /* loop */
      counter++;
      bresCounter=counter;
      returner[counter][0]=x;
       returner[counter][1]=y;
      
      if (x==x1 && y==y1) break;
      e2 = 2*error;
      if (e2 >= dy) { error += dy; x += sx; } /* e_xy+e_x > 0 */
      if (e2 <= dx) { error += dx; y += sy; } /* e_xy+e_y < 0 */
   }

  
  
  
  
  return returner;
}

public int sign(int x){
 if(x<0)
   return -1;
 
  return 1;
  
}


public void creation(){



	level= new space[row][col];
createSpace();
createGrass();
createStonewall();
createRacoon();
//createDino();
createAnteater();
createPlayer();
}

public void menu(){
if(gamecounter==0){
	fill(0,153,0);
			textSize(20);
	text("Press r to play.", 400, 400);

}
if(gamecounter==2){
	fill(0,153,0);
			textSize(20);
	text("The alligators got you.", 400, 400);
}












}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "roguelike" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
