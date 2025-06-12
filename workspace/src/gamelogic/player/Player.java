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
	private boolean doubleJump = true;
	private boolean jumpKeyDown = false;

	public Player(float x, float y, Level level) {
	
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
		time = System.currentTimeMillis();
	}

	@Override

	public void update(float tslf) {
		super.update(tslf);
	
		movementVector.x = 0;
		if(PlayerInput.isLeftKeyDown()) { 
			
			movementVector.x = -walkSpeed;
		}
		if(PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
		}
		

		if(PlayerInput.isJumpKeyDown()) { //If the space bar is down

			if(!isJumping){ //If the player isn't already jumping
				
			   System.out.println("first jump");
				movementVector.y = -jumpPower;
				isJumping = true; //player is now jumping
			}
			else if(isJumping == true && doubleJump && jumpKeyDown == false){ //If the space bar is down, the player is jumping, and can doubleJump, and they have released the space bar before repressing it (jumpKeyDown == false makes sure that you can actually see the second jump)
			System.out.println("second jump");
		    movementVector.y = -jumpPower;
			doubleJump = false; //after the second jump you shouldn't be able to double-jump again
		    }
			jumpKeyDown = true; //since the player's pressing the space bar now
		}	
		if(!PlayerInput.isJumpKeyDown()) //Accounts for if you let go of the space bar
		{
			jumpKeyDown = false;
		} 
	
		isJumping = true;
		if(collisionMatrix[BOT] != null) {isJumping = false; doubleJump = true;} //If the player hits the ground they aren't jumping anymore, so they should have the ability to double jump
	}








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
