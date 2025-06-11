package gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.enemies.Enemy;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.Gas;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;
import gamelogic.tiles.Portal;
public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private ArrayList<Portal> portals = new ArrayList<>(); 
	private ArrayList<Portal> portalPairs = new ArrayList<>();
	public ArrayList<Water> waters = new ArrayList<>();
	public ArrayList<Gas> gasList = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;
	private long countDown = System.currentTimeMillis();

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];
		waters = new ArrayList();
		gasList = new ArrayList();
		portals = new ArrayList();
		portalPairs = new ArrayList();
		//portals.addAll(portalPairs);
		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15){
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				    gasList.add((Gas)tiles[x][y]);
				}
				else if (values[x][y] == 16){
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
					 gasList.add((Gas)tiles[x][y]);
				}
				else if (values[x][y] == 17){
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
					 gasList.add((Gas)tiles[x][y]);
				}
				else if (values[x][y] == 18){
				tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				waters.add((Water)tiles[x][y]);}	
				else if (values[x][y] == 19){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				waters.add((Water)tiles[x][y]);}
				else if (values[x][y] == 20){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				waters.add((Water)tiles[x][y]);}
				else if (values[x][y] == 21){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
				waters.add((Water)tiles[x][y]);}
				else if (values[x][y] == 22){
					tiles[x][y] = new Portal(xPosition, yPosition, tileSize, false ,tileset.getImage("Portal_exit"), this);
					portalPairs.add((Portal)tiles[x][y]);
					//portalPairs.set(0, (Portal)tiles[x][y]); //first element in each pair is the CLOSED portal
				}
				else if (values[x][y] == 23){
					tiles[x][y] = new Portal(xPosition, yPosition, tileSize, true ,tileset.getImage("Portal_enter"), this);
					portalPairs.add((Portal)tiles[x][y]);
					//portalPairs.set(1, (Portal)tiles[x][y]); //second element in each pair is the OPEN portal
				}
			}
			

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>()); //change numSquaresToFill back to 20 later
						// gasList.add((Gas) map.getTiles()[flowers.get(i).getCol()][flowers.get(i).getRow()]);

						flowers.remove(i);
						i--;
				}
			}

			//WATER MODIFICATIONS - makes player slower when they are touching water
			boolean touchedWater = false;
			for(Water w : waters){
				if(w.getHitbox().isIntersecting(player.getHitbox())){
					//System.out.println("touched water");
					touchedWater = true;
					player.walkSpeed = 200; 
				}
			}
			if(!touchedWater){
				//System.out.println("never touched water");
				player.walkSpeed = 400;
			}


			//GAS MODIFICATIONS - kills you if you stay in contact with it for more than 5s
			boolean touchedGas = false;

			for(Gas gg : gasList){

				if((gg).getHitbox().isIntersecting(player.getHitbox())){
					//System.out.println("touched gas");
					touchedGas = true;
					
				}
			}
			if(!touchedGas){//If not touching gas
				countDown = 0; //Set to 0 to indicate that no collision took place
				//System.out.println("never touched gas");
			}else{//If touching gas
				if(countDown == 0){//Set to 0 to indicate that a collision took place
					countDown = System.currentTimeMillis(); //time when collision took place
				}
				else{
					 Long timeSince = (System.currentTimeMillis()-countDown)/1000; //time since collision in seconds
					
					if((timeSince>5)){//If it has been more than 5 seconds
						onPlayerDeath();
					}
				}
			}

			//---------------------------------------------------PORTAL MODIFICATIONS ----------------------------------------------------------
            //make an arraylist of portal pairs (enter/exit) within an arraylist of portals


			//portals.size increases infinitely??? -- portals.size should be 1 in this case because there's only one pair of portals
		/* 	portals.addAll(portalPairs);
			System.out.println("portals.size: " + portals.size());
			System.out.println("portalPairs.size:" + portalPairs.size());

			 float xClosed = player.getX();
			 float yClosed = player.getY();

			 for(int i = 0; i < portals.size(); i++){//for all portal pairs in the bigger ArrayList
				for(int j = 0; j < portalPairs.size(); j++){//for all elements in the portal pairs
					//first element in the pair is the closed portal (the closed door is read first in restartLevel)
					xClosed = portalPairs.get(0).getX();
					yClosed = portalPairs.get(0).getY();

					//System.out.println("condition:" + player.getHitbox().isIntersecting(portalPairs.get(1).getHitbox()));
					if(player.getHitbox().isIntersecting(portalPairs.get(1).getHitbox())){
					player.setX(xClosed);
					player.setY(yClosed);
					
					}
					
				}
			 }*/

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	
	
	//#############################################################################################################
	//Your code goes here! 
	//Please make sure you read the rubric/directions carefully and implement the solution recursively!

	//Pre-condition: none
	//Post-condition: displays water blocks with the correct fullness depending on the player's current position (and whether or not it is on top of a solid)
	private void water(int col, int row, Map map, int fullness) {
	 
		//make sure we display the correct water image
		String image = "";
		if (fullness == 3){
			image="Full_water";
		} else if(fullness == 2){
			image="Half_water";
		} else if(fullness == 1){
			image="Quarter_water";
		} else if(fullness == 0){
			image="Falling_water";	
		}
		if(map.getTiles()[col][row].isSolid()){//If the method is called on a tile that is a solid, then exit the method
			return;
		}

		//make a new water object that matches the current scenario depending on 'fullness' (parameter)
		//If fullness is originally 0, there's no water below, row+2 is in bounds, and there's a solid 2 tiles below
		if(fullness == 0 &&  row+1 < map.getTiles()[col].length && map.getTiles()[col][row+1].isSolid()){
			fullness = 3;
			image = "Full_water";
		}
		Water w = new Water (col, row, tileSize, tileset.getImage(image), this, fullness); 
		map.addTile(col, row, w);
		waters.add(w);
		

		//check if we can go down

		//if we can’t go down go left and right.

		if(col+1 < map.getTiles().length && !(map.getTiles()[col+1][row] instanceof Water)) {//If whatever is to the right is in bounds + isn't water
			if(col<map.getTiles().length-1 && (row + 1 < map.getTiles()[col].length && map.getTiles()[col][row+1].isSolid())){//If the column to the right and the row below is in bounds + the tile below is a solid platform
					
				if(fullness == 1){
					water(col+1, row, map, fullness);
				} else{
					water(col+1, row, map, fullness-1);
				}	 //ends up adding a water block with a fullness that decreases while there is still a solid under and to the right of the player
					
				}
		}

		if(col-1 >= 0 && !(map.getTiles()[col-1][row] instanceof Water)) {//If whatever is to the right is in bounds + isn't water
			if(col>0 && (row < map.getTiles()[col].length -1 && map.getTiles()[col][row+1].isSolid())){//If the column to the left and the row below is in bounds + the tile below is a solid platform
					water(col-1, row, map, fullness-1); //ends up adding a water block with a fullness that decreases while there is still a solid under and to the left of the player
			}
		}
		
		if(row < map.getTiles()[col].length -1 && !map.getTiles()[col][row+1].isSolid()){//If the tile below is in bounds and is NOT a solid
			if(map.getTiles()[col][row+1].isSolid()){//If the tile 2 spots below the current tile is a solid
				water(col, row+1, map, 3); //full water block directly above the solid block
			} else{
				water(col, row+1, map, 0); 
			}
			
		}
		
		if(row < map.getTiles()[col].length -1 && map.getTiles()[col][row+1].isSolid()){//If the tile below the water is a solid platform
			water(col, row+1, map, 3); //Then it should show up as a full block of water
		}
	}
		
	
	//pre-condition: Map is not null, row and col are in bounds, numSquaresToFill is a positive number/integer
	//post-condition: Adds gas tiles until the requisite number of squares are filled or there is no more room 
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		
		//replace flower with gas
		Gas gas = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
		map.addTile(col, row, gas);
		numSquaresToFill--;
		placedThisRound.add(gas);
		gasList.add(gas);

		while(placedThisRound.size()>0 && numSquaresToFill>0){
			row = placedThisRound.get(0).getRow();
			col = placedThisRound.get(0).getCol();
			placedThisRound.remove(0); //so that the gas is spread around the next item in placedThisRound


		//Insert gas in the 8 spots around the flower + check for numSquaresToFill in each conditional
		
		if(row-1>=0 && !map.getTiles()[col][row-1].isSolid() && !(map.getTiles()[col][row-1] instanceof Gas)){//above
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col, row-1, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col, row-1, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		} 
		if(row-1>=0 && col+1< map.getTiles().length && !map.getTiles()[col+1][row-1].isSolid() && !(map.getTiles()[col+1][row-1] instanceof Gas)){//above+right
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col+1, row-1, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col+1, row-1, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		}
		if(col-1>=0 && row-1>=0 && !map.getTiles()[col-1][row-1].isSolid()&& !(map.getTiles()[col-1][row-1] instanceof Gas)){//above+left
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col-1, row-1, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col-1, row-1, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		}
		if(col+1<map.getTiles().length && !map.getTiles()[col+1][row].isSolid() && !(map.getTiles()[col+1][row] instanceof Gas)){//right
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col+1, row, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col+1, row, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		}
		if(col-1>=0 && !map.getTiles()[col-1][row].isSolid()&& !(map.getTiles()[col-1][row] instanceof Gas)){//left
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col-1, row, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col-1, row, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		}
		if(row+1 < map.getTiles()[col].length && !map.getTiles()[col][row+1].isSolid()&& !(map.getTiles()[col][row+1] instanceof Gas)){//below
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col, row+1, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col, row+1, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		}
		if(row+1 < map.getTiles()[col].length && col+1<map.getTiles()[col].length && !map.getTiles()[col+1][row+1].isSolid()&& !(map.getTiles()[col+1][row+1] instanceof Gas)){//right+below
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col+1, row+1, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col+1, row+1, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		}
		if(row+1 < map.getTiles()[col].length && col-1>=0 && !map.getTiles()[col-1][row+1].isSolid()&& !(map.getTiles()[col-1][row+1] instanceof Gas)){//left+below
			if(numSquaresToFill == 0){
				return;
			}
			Gas g = new Gas (col-1, row+1, tileSize, tileset.getImage("GasOne"), this, 0);
			map.addTile(col-1, row+1, g);
			numSquaresToFill--;
			placedThisRound.add(g);
			gasList.add(g);

		}
	
	}

		}

		

	

	public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 // Draw the enemies
	   	 for (int i = 0; i < enemies.length; i++) {
	   		 enemies[i].draw(g);
	   	 }


	   	 // Draw the player
	   	 player.draw(g);




	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());
	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}


//Collision code
