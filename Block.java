import greenfoot.*;

/**
 * @author (Daniel Furrer, Christian Cidecian)
 * @version (v2.69)
 */
public class Block extends Actor
{
    public Block(Color blockColor)
    {
        GreenfootImage image = new GreenfootImage(32, 32);
        image.setColor(blockColor);
        image.fillRect(0, 0, 31, 31);
        this.setImage(image);
    }
}
