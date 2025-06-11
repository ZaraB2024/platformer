package gamelogic.tiles;
import java.awt.image.BufferedImage;
import gameengine.hitbox.RectHitbox;
import gamelogic.level.Level;

public class Portal extends Tile {

    private boolean isOpenPortal;
    
 public Portal(float x, float y, int size, boolean type, BufferedImage image, Level level) {
   	 super(x, y, size, image, false, level);
     isOpenPortal = type; 

   	 this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
    }

    public boolean portalType(){
        return isOpenPortal;
    }


}
