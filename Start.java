import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Start here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Start extends World
{

    /**
     * Constructor for objects of class Start.
     * 
     */
    public Start()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(10, 20, 32    , false); 
        showText("to start game", 3, 18);
        prepare();
    }
    
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        text text = new text();
        addObject(text,4,16);
        text.setLocation(2,16);
        text.setLocation(6,16);
        text.setLocation(4,16);
        text.setLocation(5,15);
        text.setLocation(8,15);
        text.setLocation(3,14);
        text.setLocation(5,16);
        text.setLocation(2,17);
    }
}
