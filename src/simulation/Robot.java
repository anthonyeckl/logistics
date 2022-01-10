package simulation;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Robot extends SimObject {

    // final destination
    private int targetX;
    private int targetY;
    // path to destination with waypoints
    ArrayList<int[]> pathToTarget = new ArrayList<int[]>();
    // path Tile numbers
    ArrayList<int[]> pathTiles = new ArrayList<>();
    // 5x5 proximity Tiles
    ArrayList<int[]> proximityTiles = new ArrayList<>();
    // current direction 0: up 1:right 2:down 3: left
    private int direction = -1;
    // dinstance left until reaching destination
    private float distanceLeft = 0f;
    // current state
    private String task = "IDLE";
    // robot number
    private int number;
    // sim organiser
    Organiser organiser;
    //wait start time
    private long waitStartTime;
    private boolean doWait = false;
    // following Route
    int[] followingRoute = new int[4];

    public Robot(int x, int y, ID id, int number, Organiser organiser){
        super(x, y, id);
        this.number = number;
        this.organiser = organiser;
        setVelX(2);
        setVelY(2);
        //setPathToTarget(calcPathToTarget(300, 270));
            /*
        for (int t:
                pathToTarget.get(0)) {
            System.out.println(t);
        }
        for (int t:
                pathToTarget.get(1)) {
            System.out.println(t);
        }
        */

    }

    public void newJob(int[] jobCords){
        int fromX = jobCords[0];
        int fromY = jobCords[1];
        int toX = jobCords[2];
        int toY = jobCords[3];
        // if the robot is not already on the from location
        if(fromX != x && fromY != y){
            // two different routes
            int[] transitRoute = {fromX, fromY, x, y};
            int[] transportRoute = {toX, toY, fromX, fromY};
            // setting the following route of the robot ti the realtransport mission
            followingRoute = transportRoute.clone();
            // beginn with transit
            this.newRoute(transitRoute[0],transitRoute[1],transitRoute[2],transitRoute[3]);
            this.setTaskTransit();
        }else{
            // if the robot already is on the from location it directly starts with the transport
            this.newRoute(toX, toY, x, y);
            this.setTaskTransport();
        }

    }

    // new Destination
    public void newRoute(int tx, int ty, int sx, int sy){
        if(tx % 10 == 0 && ty % 10 == 0 && tx >= 0 && tx <= 790 && ty >= 0 && ty <= 790){
            //System.out.println(this.number+"Made new Route " + this);
            targetX = tx;
            targetY = ty;
            // calculate waypoints
            setPathToTarget(calcPathToTarget(targetX, targetY, sx, sy, "any"));
            System.out.println(this.direction);
            // calculate Tile numbers of Tiles on the path
            setPathTiles(calcPathTiles(x,y));
            pathTiles.remove(0);
            calcDirection(pathTiles.get(0));
            //System.out.println("ptl"+ pathTiles.size());

        } else {
            System.out.println("Invalid coordinates");
        }
    }
    // pathToTarget -> waypoints where a direction change is planned
    private ArrayList<int[]> calcPathToTarget(int tx, int ty, int sx, int sy, String dir){
        ArrayList<int[]> tpath = new ArrayList<int[]>();
        if(dir.equals("any")){
                dir = "top";
        }
        if(tx != sx && ty != sy){
            if(dir.equals("bottom")){
                // Manhatten way top
                // Waypoint 1
                int[] tcord1 = new int[2];
                tcord1[0] = sx;
                tcord1[1] = ty;
                tpath.add(tcord1);
                // Waypoint 2 (should be the destination)
                int[] tcord2 = new int[2];
                tcord2[0] = tx;
                tcord2[1] = ty;
                tpath.add(tcord2);
            }else if(dir.equals("top")){
                // Manhatten way bottom
                // Waypoint 1
                int[] tcord1 = new int[2];
                tcord1[0] = tx;
                tcord1[1] = sy;
                tpath.add(tcord1);
                // Waypoint 2 (should be the destination)
                int[] tcord2 = new int[2];
                tcord2[0] = tx;
                tcord2[1] = ty;
                tpath.add(tcord2);
            }

        } else{
            // if the robot is already aligned with the target and does not need another waypoint
            int[] tcord1 = new int[2];
            tcord1[0] = tx;
            tcord1[1] = ty;
            tpath.add(tcord1);
        }

        return tpath;
    }

    // calculates and returns the list of the coordinates of the pathTiles
    private ArrayList<int[]> calcPathTiles(int fromX, int fromY){
        ArrayList<int[]> pTiles = new ArrayList<>();

        for (int i = 0; i < pathToTarget.size(); i++) {
            int locx;
            int locy;
            if (i == 0){
                locx = fromX;
                locy = fromY;
            }else{
                locx = pathToTarget.get(i-1)[0];
                locy = pathToTarget.get(i-1)[1];
            }
            int tarx;
            int tary;
            if(pathToTarget.size() != 1){
                tarx = pathToTarget.get(i)[0];
                tary = pathToTarget.get(i)[1];
            } else{
                tarx = pathToTarget.get(0)[0];
                tary = pathToTarget.get(0)[1];
            }

            //System.out.println("lx"+ locx + "ly" +locy);
            //System.out.println("tx"+ tarx + "ty" +tary);
            // direction up 0
            if(locy > tary && locx == tarx){
                for (int j = locy/10; j > tary/10; j--) {
                    int[] cords = new int[2];
                    cords[0] = locx;
                    cords[1] = locy+((j-locy/10)*10);
                    //System.out.println("c1"+"," + cords[0] +","+ cords[1]);
                    pTiles.add(cords);
                }
            }
            //direction right 1
            else if(locx < tarx && locy == tary){
                for (int j = locx/10; j < tarx/10; j++) {
                    int[] cords = new int[2];
                    cords[0] = locx+((j-locx/10)*10);
                    cords[1] = locy;
                    //System.out.println("c2"+"," + cords[0] +","+ cords[1]);
                    pTiles.add(cords);
                }
            }
            // direction down 2
            else if(locy < tary && locx == tarx){
                for (int j = locy/10; j < tary/10; j++) {
                    int[] cords = new int[2];
                    cords[0] = locx;
                    cords[1] = locy+((j-locy/10)*10);
                    //System.out.println("c3"+"," + cords[0] +","+ cords[1]+"---"+ "j" + j);
                    pTiles.add(cords);
                }
            }
            // direction left 3
            else if(locx > tarx && locy == tary){
                for (int j = locx/10; j > tarx/10; j--) {
                    int[] cords = new int[2];
                    cords[0] = locx+((j-locx/10)*10);
                    cords[1] = locy;
                    //System.out.println("c4"+"," + cords[0] +","+ cords[1]);
                    pTiles.add(cords);
                }
            }


        }
        int[] cords = new int[2];
        cords[0] = targetX;
        cords[1] = targetY;
        pTiles.add(cords);
        return pTiles;
    }

    // depending on the waypoint passed into the function the direction of the robot is changed accordingly
    // 0:UP 1:RIGHT 2:DOWN 3:LEFT
    private void calcDirection(int[] waypoint){
        if(waypoint[0] == x && waypoint[1] < y )
            direction = 0;
        else if(waypoint[0] == x && waypoint[1] > y )
            direction = 2;

        else if(waypoint[0] > x && waypoint[1] == y )
            direction = 1;
        else if(waypoint[0] < x && waypoint[1] == y )
            direction = 3;
    }

    public void tick() {
        if (!doWait) {
            // move the robot
            if(task.equals("TRANSIT") || task.equals("TRANSPORT")){
                if(direction == 0)
                    y -= velY;
                else if(direction == 1)
                    x += velX;
                else if(direction == 2)
                    y += velY;
                else if(direction == 3)
                    x -= velX;
            }
            // clamping the cords -> keeping the bot in bounds
            x = Sim.clamp(x, 0, 790);
            y = Sim.clamp(y, 0, 790);

            if (pathToTarget.size() != 0){
                // check if waypint is reached
                if(x == pathToTarget.get(0)[0] && y == pathToTarget.get(0)[1]){
                    //System.out.println("reached waypoint");
                    // remove reached waypint
                    if (pathToTarget.size() > 1){
                        pathToTarget.remove(0);
                    }
                    // in case there is only one waypoint (the target) left
                    else if (pathToTarget.size() == 1){
                        pathToTarget.remove(0);
                        setProximityTiles(calcProximityTiles(x,y));
                        // depending on what the task was, the following actions are initiated
                        if(task.equals("TRANSPORT")){
                            setTaskIdle();
                            organiser.removeInUse(this);
                            organiser.addInIdle(this);
                            targetX = -1;
                            targetY = -1;
                        } else if(task.equals("TRANSIT")){
                            setTaskTransport();
                            this.newRoute(followingRoute[0],followingRoute[1],followingRoute[2],followingRoute[3]);
                            this.followingRoute = new int[]{-1, -1, -1, -1};
                        }

                    }
                }
            }
            // remove the reached pathTile and recalculate the proximity tiles
            if(pathTiles.size()>=1){
                if(x == pathTiles.get(0)[0] && y == pathTiles.get(0)[1]){
                    pathTiles.remove(0);
                    setProximityTiles(calcProximityTiles(x,y));
                }

            }
            // calc new direction based on next pathTile
            if(pathTiles.size()>0)
                calcDirection(pathTiles.get(0));
        }
        // if the robot is forced to wait the current time gets compared to the stop time
        else{
            long currentTime = System.currentTimeMillis();
            if(currentTime-waitStartTime>1000){
                doWait = false;
            }
        }


    }
    // calculating the proximity Tiles 5x5 area around the robot
    public ArrayList<int[]> calcProximityTiles(int x, int y){
        ArrayList<int[]> pTiles = new ArrayList<>();
        int[] cTile = new int[2];
        cTile[0] = (x/10) * 10;
        cTile[1] = (y/10) * 10;

        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                int[] pTile = new int[2];
                pTile[0] = cTile[0]-(i*10);
                pTile[1] = cTile[1]-(j*10);
                pTiles.add(pTile);
            }
        }
        for (int i = 0; i < pTiles.size(); i++) {
            if(pTiles.get(i)[0] < 0 || pTiles.get(i)[1] < 0)
                pTiles.remove(i);
        }
        return pTiles;
    }

    public void obstacleOnPath(int index){
        //horizontal
        if(index != 0 && pathTiles.get(index)[1] == pathTiles.get(index-1)[1]){
            if (targetY>=y && y != Sim.HEIGHT - 10){
                //System.out.println("bottom");
                setPathToTarget(calcPathToTarget(targetX, targetY, x, y+10, "top"));
                setPathTiles(calcPathTiles(x,y+10));
                //pathToTarget.add(0, new int[]{pathToTarget.get(0)[0], pathToTarget.get(0)[1] + 10});
                //setPathTiles(calcPathTiles(x, y+10));
                //System.out.println("ptl"+pathTiles.size());
                //System.out.println("ptTl"+pathToTarget.size());
             }else if(targetY<=y && y != 10){
                //System.out.println("top");
                setPathToTarget(calcPathToTarget(targetX, targetY, x, y-10, "top"));
                setPathTiles(calcPathTiles(x,y-10));

            }
        }
        // vertical
        else if(index != 0 && pathTiles.get(index)[0] == pathTiles.get(index-1)[0]){
            if (targetX>=x && x != Sim.WIDTH - 10){
                //System.out.println("vbottom");
                setPathToTarget(calcPathToTarget(targetX, targetY, x+10, y, "bottom"));
                setPathTiles(calcPathTiles(x+10,y));
            }else if(targetX<=x && x != 0){
                //System.out.println("vtop");
                setPathToTarget(calcPathToTarget(targetX, targetY, x-10, y, "bottom"));
                setPathTiles(calcPathTiles(x-10,y));

            }
        }
        calcDirection(pathTiles.get(0));
    }

    public void waitRobot(){
        waitStartTime = System.currentTimeMillis();
        doWait = true;
    }

    // GETTERS AND SETTERS ----------------- START ----------------------
    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setPathToTarget(ArrayList<int[]> pathToTarget) {
        this.pathToTarget = pathToTarget;
    }

    public ArrayList<int[]> getPathToTarget() {
        return pathToTarget;
    }

    public void getPrintedPathToTarget(){
        for (int i = 0; i < pathToTarget.size(); i++) {
            System.out.println("waypoint: "+ i+ ", x:" + pathToTarget.get(i)[0]);
            System.out.println("waypoint: "+ i+ ", y:" + pathToTarget.get(i)[1]);
        }
    }

    public void setPathTiles(ArrayList<int[]> pathTiles) {
        this.pathTiles = pathTiles;
    }

    public ArrayList<int[]> getPathTiles() {
        return pathTiles;
    }

    public void setProximityTiles(ArrayList<int[]> proximityTiles) {
        this.proximityTiles = proximityTiles;
    }

    public int getNumber() {
        return number;
    }

    public String getTask() {
        return task;
    }

    public void setTaskIdle(){
        this.task = "IDLE";
    }
    public void setTaskTransit(){
        this.task = "TRANSIT";
    }
    public void setTaskTransport(){
        this.task = "TRANSPORT";
    }

    // GETTERS AND SETTERS ----------------- END ----------------------


    public void render(Graphics g) {
        //draw ptiles
        if(!task.equals("IDLE")){
            for (int i = 0; i < proximityTiles.size(); i++) {
                //System.out.println("draw p"+ pathTiles.get(i)[0] +pathTiles.get(i)[1]);
                g.setColor(new Color(255, 43, 127));
                g.fillRect(proximityTiles.get(i)[0]+2, proximityTiles.get(i)[1]+2, 5, 5);
            }
        }
        // draw Robot
        if(task.equals("TRANSIT"))
            g.setColor(new Color(255, 255, 77));
        else if(task.equals("TRANSPORT"))
            g.setColor(new Color(25, 178, 255));
        else
            g.setColor(Color.white);
        g.fillRect(x, y, 10, 10);
        // draw destination
        if(targetX != -1 && targetY != -1 && !task.equals("IDLE")){
            g.setColor(Color.RED);
            g.fillRect(targetX, targetY, 10, 10);
        }
        //draw path
        for (int i = 0; i < pathTiles.size(); i++) {
            //System.out.println("draw p"+ pathTiles.get(i)[0] +pathTiles.get(i)[1]);
            g.setColor(Color.GREEN);
            g.fillRect(pathTiles.get(i)[0]+2, pathTiles.get(i)[1]+2, 5, 5);
        }



    }
}
