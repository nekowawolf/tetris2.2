import greenfoot.*;

import java.util.List;

/**
 * @author (Daniel Furrer, Christian Cidecian)
 * @version (v2.69)
 */
public class Game extends World
{
    
    public Game()
    {    
        super(10, 20, 32, false);
        prepare();
        setBackground(new GreenfootImage("13.png"));
        GreenfootSound backgroundMusic = new GreenfootSound("medival.mp3");
        backgroundMusic.playLoop();
    }
    Block curBlocks[] = new Block[4];
    BlockType curShape;
    int rotation;
    boolean rotatedLeft = false;
    boolean rotatedRight = false;
    int time = 0;
    int delay = 0;
    int gravity = 48;
    int drop = 2;
    boolean dropped = false;
    boolean downHeld = false;
    int rows[] = new int[getHeight()];
    private void prepare()
    {
        Greenfoot.setSpeed(50);                                                 
        GreenfootImage image = new GreenfootImage(getWidth(), getHeight());     
        image.setColor(Color.BLACK);                                            
        image.fillRect(0, 0, getWidth(), getHeight());                          
        this.setBackground(image);                                              

        newBlocks();
    }

    public void act()
    {
        boolean collideRight = collideRight();
        boolean collideLeft = collideLeft();
        boolean collideBottomNoStop = collideBottomNoStop();
        
        time++;
        if (Greenfoot.isKeyDown("down") && !dropped && !downHeld)
        {
            time = gravity;                                                     //
            dropped = true;                                                     //Blöcke schneller senken
            downHeld = true;                                                    //
        }
        else if (!Greenfoot.isKeyDown("down")) 
        {
            dropped = false;                                                    //Sicherstellen dass Blöcke nicht schneller gesenkt werden
            downHeld = false;                                                   //
        }
        
        if (Greenfoot.isKeyDown("left") && delay >= 10 && !collideLeft)
        {
            delay = 0;                                                                  //
            curBlocks[0].setLocation(curBlocks[0].getX() - 1, curBlocks[0].getY());     //
            curBlocks[1].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());     //Bewegung der Blöcke nach links (X Koordinaten -1)
            curBlocks[2].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());     //
            curBlocks[3].setLocation(curBlocks[3].getX() - 1, curBlocks[3].getY());     //
        }
        else { delay++; }
        if (Greenfoot.isKeyDown("right") && delay >= 10 && !collideRight)
        {
            delay = 0;                                                                  //
            curBlocks[0].setLocation(curBlocks[0].getX() + 1, curBlocks[0].getY());     //
            curBlocks[1].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());     //Bewegung der Blöcke nach links (X Koordinaten +1)
            curBlocks[2].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());     //
            curBlocks[3].setLocation(curBlocks[3].getX() + 1, curBlocks[3].getY());     //
        }
        else { delay++; }
        
        boolean shapeWillCollide = 
            (curShape == BlockType.I && rotation == 90 ||                                                               //
            (curShape == BlockType.J || curShape == BlockType.L) && (rotation == 90 || rotation == 270) ||              //
            (curShape == BlockType.S || curShape == BlockType.Z) && (rotation == 90) ||                                 //Prüft ob bei Rotierung die Form mir der Wand kollidiert
            curShape == BlockType.T && (rotation == 90 || rotation == 270)                                              //
            );                                                                                                          //
        
        boolean stopRotateLeft = shapeWillCollide && collideLeft || curShape == BlockType.I && rotation == 90 && curBlocks[0].getX() + 1 <= 2;              // Blockiert drehung wenn sie nicht möglich ist.
        boolean stopRotateRight = shapeWillCollide && collideRight || curShape == BlockType.I && rotation == 90 && curBlocks[0].getX() + 1 >= getWidth();   //
        
        if (!rotatedLeft && Greenfoot.isKeyDown("a") && !(stopRotateLeft || stopRotateRight) && !collideBottomNoStop)   //
        {                                                                                                               //
            rotateLeft();                                                                                               //
            rotatedLeft = true;                                                                                         //
        }                                                                                                               // Bewegung des Blockes nach links
        else if (!Greenfoot.isKeyDown("a"))                                                                             //
        {                                                                                                               //
            rotatedLeft = false;                                                                                        //
        }                                                                                                               //
        if (!rotatedRight && Greenfoot.isKeyDown("d") && !(stopRotateRight || stopRotateLeft)&& !collideBottomNoStop)   //
        {                                                                                                               //
            rotateRight();                                                                                              //
            rotatedRight = true;                                                                                        //
        }                                                                                                               // Bewegung des Blockes nach rechts
        else if (!Greenfoot.isKeyDown("d"))                                                                             //
        {                                                                                                               //
            rotatedRight = false;                                                                                       //
        }                                                                                                               //
        
        if ((time >= gravity || (dropped && time > drop)) && !collideBottomStop())                                      //
        {                                                                                                               //
            time = 0;                                                                                                   //
            curBlocks[0].setLocation(curBlocks[0].getX(), curBlocks[0].getY() + 1);                                     //
            curBlocks[1].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);                                     // Bewegung des Blockes nach unten
            curBlocks[2].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);                                     //
            curBlocks[3].setLocation(curBlocks[3].getX(), curBlocks[3].getY() + 1);                                     //
        }                                                                                                               //
    }
    
    public void checkRows()
    {
        for (int row = 0; row < rows.length; row++)                     //Geht durch alle Reihen durch
        {
            if (rows[row] == 10)                                        //wenn Reihe voll ist mache...
            {
                for (int x = 0; x < getWidth(); x++)                    
                {
                    removeObjects(getObjectsAt(x, row, Block.class));   //entfernt Blöcke welche in voller reihe sind
                }
                for (int y = row; y >= 0; y--)                          //
                {                                                       //
                    for (int x = 0; x < getWidth(); x++)                //
                    {                                                   //
                        List blocks = getObjectsAt(x, y, Block.class);  //
                        for (Actor b : (List<Actor>) blocks)            //setzt nicht entfernte Blöcke auf die unterste Linie
                        {                                               //
                            b.setLocation(x, y + 1);                    //
                        }                                               
                    }                                                   
                    if (y != 0) { rows[y] = rows[y - 1]; }              //sicherstellen das die Blöcke auf de untersten Reihe sind.
                }
                rows[0] = 0;
            }
        }
    }
    
    public boolean collideBottomStop()
    {
        if (collideBottomNoStop())
        {
            if (curBlocks[0].getY() == 0 || curBlocks[1].getY() == 0 || curBlocks[2].getY() == 0 || curBlocks[3].getY() == 0) { Greenfoot.stop(); }
            rows[curBlocks[0].getY()]++;
            rows[curBlocks[1].getY()]++;
            rows[curBlocks[2].getY()]++;
            rows[curBlocks[3].getY()]++;
            checkRows();
            newBlocks();
            return true;   
        }
        return false;
    }
    
    public boolean collideBottomNoStop()
    {
        for (Block b : curBlocks)
        {
            List objs = getObjectsAt(b.getX(), b.getY() + 1, Block.class);
            objs.remove(curBlocks[0]);
            objs.remove(curBlocks[1]);
            objs.remove(curBlocks[2]);
            objs.remove(curBlocks[3]);
            if (b.getY() + 1 == getHeight() || !objs.isEmpty())
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean collideLeft()                                                
    {
        for (Block b : curBlocks)
        {
            List objs = getObjectsAt(b.getX() - 1, b.getY(), Block.class);          //
            objs.remove(curBlocks[0]);                                              //
            objs.remove(curBlocks[1]);                                              //
            objs.remove(curBlocks[2]);                                              //
            objs.remove(curBlocks[3]);                                              //prüft Kollision mit linker Kante
            if (b.getX() == 0 || !objs.isEmpty())                                   // Wenn an Linker Kante mach gib 'true' aus
            {                                                                       //
                return true;                                                        //
            }
        }
        return false;
    }
    
    public boolean collideRight()
    {
        for (Block b : curBlocks)
        {
            List objs = getObjectsAt(b.getX() + 1, b.getY(), Block.class);          //
            objs.remove(curBlocks[0]);                                              //
            objs.remove(curBlocks[1]);                                              //
            objs.remove(curBlocks[2]);                                              //
            objs.remove(curBlocks[3]);                                              //prüft Kollision mit rechter Kante
            if (b.getX() + 1 == getWidth() || !objs.isEmpty())                      // Wenn an rechter Kante (getX() + 1 == getWidth()) gib 'true' ausx 
            {                                                                       //
                return true;                                                        //
            }                                                                       //
        }
        return false;
    }
    
        public void newBlocks()
    {
        curShape = BlockType.fromInt(Greenfoot.getRandomNumber(BlockType.values().length));                         // Wählt zufällige Zahl und schribt Sie einem Buchstaben in BlockType zu.
        dropped = false; 
        rotation = 0;
        Color blockColor = Color.WHITE; 
        
        switch (curShape) {                                                                                         // Erstellt Block wenn Bedingung Zutrifft
            case I:
                blockColor = Color.RED;
                curBlocks[0] = new Block(blockColor);
                addObject(curBlocks[0], 3, 0);
                curBlocks[1] = new Block(blockColor);
                addObject(curBlocks[1], 4, 0);                                                                      // erstellt I Block in rot
                curBlocks[2] = new Block(blockColor);
                addObject(curBlocks[2], 5, 0);
                curBlocks[3] = new Block(blockColor);                                                               
                addObject(curBlocks[3], 6, 0);
                break;

            case J:
                blockColor = Color.BLUE;
                curBlocks[0] = new Block(blockColor);
                addObject(curBlocks[0], 6, 1);
                curBlocks[1] = new Block(blockColor);
                addObject(curBlocks[1], 4, 0);                                                                      // erstellt J Block in blau
                curBlocks[2] = new Block(blockColor);
                addObject(curBlocks[2], 5, 0);
                curBlocks[3] = new Block(blockColor);
                addObject(curBlocks[3], 6, 0);
                break;

            case L:
                blockColor = Color.GREEN;
                curBlocks[0] = new Block(blockColor);
                addObject(curBlocks[0], 4, 1);
                curBlocks[1] = new Block(blockColor);
                addObject(curBlocks[1], 4, 0);                                                                      // erstellt L Block in grün
                curBlocks[2] = new Block(blockColor);
                addObject(curBlocks[2], 5, 0);
                curBlocks[3] = new Block(blockColor);
                addObject(curBlocks[3], 6, 0);
                break;

            case O:
                blockColor = Color.CYAN;
                curBlocks[0] = new Block(blockColor);
                addObject(curBlocks[0], 4, 0);
                curBlocks[1] = new Block(blockColor);
                addObject(curBlocks[1], 5, 0);                                                                      // erstellt O Block in türkis
                curBlocks[2] = new Block(blockColor);
                addObject(curBlocks[2], 4, 1);
                curBlocks[3] = new Block(blockColor);
                addObject(curBlocks[3], 5, 1);
                break;

            case S:
                blockColor = Color.ORANGE;
                curBlocks[0] = new Block(blockColor);
                addObject(curBlocks[0], 5, 0);
                curBlocks[1] = new Block(blockColor);
                addObject(curBlocks[1], 6, 0);                                                                      // erstellt S Block in rot
                curBlocks[2] = new Block(blockColor);
                addObject(curBlocks[2], 4, 1);
                curBlocks[3] = new Block(blockColor);
                addObject(curBlocks[3], 5, 1);
                break;

            case T:
                blockColor = Color.YELLOW;
                curBlocks[0] = new Block(blockColor);
                addObject(curBlocks[0], 4, 0);
                curBlocks[1] = new Block(blockColor);
                addObject(curBlocks[1], 5, 0);                                                                      // erstellt T Block in gelb
                curBlocks[2] = new Block(blockColor);
                addObject(curBlocks[2], 6, 0);
                curBlocks[3] = new Block(blockColor);
                addObject(curBlocks[3], 5, 1);
                break;

            case Z:
                blockColor = Color.MAGENTA;
                curBlocks[0] = new Block(blockColor);
                addObject(curBlocks[0], 4, 0);
                curBlocks[1] = new Block(blockColor);
                addObject(curBlocks[1], 5, 0);                                                                      // erstellt Z Block in pink
                curBlocks[2] = new Block(blockColor);
                addObject(curBlocks[2], 5, 1);
                curBlocks[3] = new Block(blockColor);
                addObject(curBlocks[3], 6, 1);
                break;
        }
    }
    
    public void rotateLeft()
    {
        switch (curShape) {
            case I:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 2);
                    curBlocks[1].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);             
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() - 2, curBlocks[2].getY());
                    curBlocks[1].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    rotation = 0;
                }
                break;

            case J:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    curBlocks[1].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY() - 1);
                    curBlocks[1].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    rotation = 180;
                }
                else if (rotation == 180)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY() - 1);
                    curBlocks[1].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    rotation = 270;
                }
                else if (rotation == 270)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY() + 1);
                    curBlocks[1].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    rotation = 0;
                }
                break;

            case L:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY() - 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY() - 1);
                    rotation = 180;
                }
                else if (rotation == 180)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() , curBlocks[1].getY() + 1);
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY() + 1);
                    rotation = 270;
                }
                else if (rotation == 270)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY() + 1);
                    rotation = 0;
                }
                break;

            case S:
                if (rotation == 0)
                {
                    curBlocks[1].setLocation(curBlocks[0].getX(), curBlocks[0].getY() - 1);
                    curBlocks[2].setLocation(curBlocks[0].getX() + 1, curBlocks[0].getY());
                    curBlocks[3].setLocation(curBlocks[0].getX() + 1, curBlocks[0].getY() + 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[1].setLocation(curBlocks[0].getX() + 1, curBlocks[0].getY());
                    curBlocks[2].setLocation(curBlocks[0].getX() - 1, curBlocks[0].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[0].getX(), curBlocks[0].getY() + 1);
                    rotation = 0;
                }
                break;

            case T:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    rotation = 180;
                }
                else if (rotation == 180)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    rotation = 270;
                }
                else if (rotation == 270)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    rotation = 0;
                }
                break;

            case Z:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[2].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY() - 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY() + 1);
                    rotation = 0;
                }
                break;
        }
    }
    
    public void rotateRight()
    {
        switch (curShape) {
            case I:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 2);
                    curBlocks[1].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() - 2, curBlocks[2].getY());
                    curBlocks[1].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    rotation = 0;
                }
                break;

            case J:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY() - 1);
                    curBlocks[1].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);
                    rotation = 270;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY() + 1);
                    curBlocks[1].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    rotation = 0;
                }
                else if (rotation == 180)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY() + 1);
                    curBlocks[1].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    rotation = 90;
                }
                else if (rotation == 270)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY() - 1);
                    curBlocks[1].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    rotation = 180;
                }
                break;

            case L:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY() + 1);
                    curBlocks[1].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);
                    rotation = 270;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY() + 1);
                    curBlocks[1].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    rotation = 0;
                }
                else if (rotation == 180)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY() - 1);
                    curBlocks[1].setLocation(curBlocks[2].getX(), curBlocks[2].getY() - 1);
                    curBlocks[3].setLocation(curBlocks[2].getX(), curBlocks[2].getY() + 1);
                    rotation = 90;
                }
                else if (rotation == 270)
                {
                    curBlocks[0].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY() - 1);
                    curBlocks[1].setLocation(curBlocks[2].getX() + 1, curBlocks[2].getY());
                    curBlocks[3].setLocation(curBlocks[2].getX() - 1, curBlocks[2].getY());
                    rotation = 180;
                }
                break;

            case S:
                if (rotation == 0)
                {
                    curBlocks[1].setLocation(curBlocks[0].getX(), curBlocks[0].getY() - 1);
                    curBlocks[2].setLocation(curBlocks[0].getX() + 1, curBlocks[0].getY());
                    curBlocks[3].setLocation(curBlocks[0].getX() + 1, curBlocks[0].getY() + 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[1].setLocation(curBlocks[0].getX() + 1, curBlocks[0].getY());
                    curBlocks[2].setLocation(curBlocks[0].getX() - 1, curBlocks[0].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[0].getX(), curBlocks[0].getY() + 1);
                    rotation = 0;
                }
                break;

            case T:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    rotation = 270;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    rotation = 0;
                }
                else if (rotation == 180)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    rotation = 90;
                }
                else if (rotation == 270)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX(), curBlocks[1].getY() - 1);
                    rotation = 180;
                }
                break;

            case Z:
                if (rotation == 0)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[2].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY());
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY() - 1);
                    rotation = 90;
                }
                else if (rotation == 90)
                {
                    curBlocks[0].setLocation(curBlocks[1].getX() - 1, curBlocks[1].getY());
                    curBlocks[2].setLocation(curBlocks[1].getX(), curBlocks[1].getY() + 1);
                    curBlocks[3].setLocation(curBlocks[1].getX() + 1, curBlocks[1].getY() + 1);
                    rotation = 0;
                }
                break;
        }
    }
}
