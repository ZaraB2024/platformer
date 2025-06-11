package gamelogic.player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gamelogic.Main;
import gamelogic.level.Level;
import gamelogic.tiles.Gas;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;


public class Player extends PhysicsObject{
	public float walkSpeed = 400;
	public float jumpPower = 1350;

	private boolean isJumping = false;
	private long time;
	private int jumpCount = 0;
	private boolean touchGround = true;
	private boolean doubleJump = false;

	public Player(float x, float y, Level level) {
	
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
		time = System.currentTimeMillis();
	}

	@Override

	/*Double-jump Information:
			- space bar must have been pressed already/player should have jumped already
			- release and repress the space bar
			- limit jump count to 2 jumps 

	*/

	public void update(float tslf) {
		super.update(tslf);
	
		movementVector.x = 0;
		if(PlayerInput.isLeftKeyDown()) { 
			
			movementVector.x = -walkSpeed;
		}
		if(PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
		}
		if(PlayerInput.isJumpKeyDown() && !isJumping) {

			System.out.println("first jump");
			movementVector.y = -jumpPower;
			isJumping = true;
			touchGround = false;
			doubleJump = true; //you can double jump after the space bar is pressed once
			
		} else if(isJumping == true && doubleJump){
			movementVector.y = -jumpPower;
			doubleJump = false;
		}
		
		isJumping = true;
		if(collisionMatrix[BOT] != null) isJumping = false;
	}

/*Double-jump Information:
			- space bar must have been pressed already/player should have jumped already
			- release and repress the space bar
			- limit jump count to 2 jumps 

			- If the player is pressing the space bar down and isn't already jumping
			  - add to the y velocity (make them jump)
			  - the player has the ability to double jump
			  - If the player is in mid-air (isJumping is true), they can doubleJump, and the jumpCount is less than 2, add to the y velocity

	*/






	@Override
	public void draw(Graphics g) {


		g.setColor(Color.YELLOW);
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
		
		g.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));

		g.drawString(((System.currentTimeMillis()-time)/1000)+ "", (int)getX(), (int)getY());

		if(System.currentTimeMillis()-time > 5000){ //count up to 5s
			time = System.currentTimeMillis();
		
		}

		if(Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if(t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}
		
		hitbox.draw(g);
	}
}
