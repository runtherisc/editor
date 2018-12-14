package data;

public interface Constants {
	
	//string for convenience
	String MINIMUM_APK_VERSION = "0.1";

	float X_PERCENT = 1.0F;
	float Y_PERCENT = 1.0F;
	float X_BLOCK = 16.0F;
	float Y_BLOCK = 16.0F;

    //what gets painted, will need to be slightly large for map items just off the screen
    //480x800 = 30x50 actual screen size galaxy S2 (normal high def and large low def)
    float OVERDRAW = 5;
	
	
	//this must be even and all image must be a multiple of it
//	final static int BLOCK_SIZE = 16;
    int DISPLAY_WIDTH = 50;
    int DISPLAY_HEIGHT = 25;

    //how far in we go before x=0 and y=0, so items cannot be placed on the edge of the map
    int OVER_SCROLL = 4;

    int TIMER_SPEED = 8;
	int TIMER_SPEED_ADJ = 4;//when called by calc tasks or increase time, as they fire every other tick

}
